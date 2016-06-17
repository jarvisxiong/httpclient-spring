/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;

import com.maoyan.machine.httpclient.spring.converter.RequestEntityConverter;
import com.maoyan.machine.httpclient.spring.converter.ResponseEntityConverter;
import com.maoyan.machine.httpclient.spring.interceptor.HttpApiInterceptor;
import com.maoyan.machine.httpclient.spring.interceptor.Invocation;
import com.maoyan.machine.httpclient.spring.meta.HttpApiMeta;
import com.maoyan.machine.httpclient.spring.meta.MetasManager;
import com.maoyan.machine.httpclient.spring.meta.ModelMeta;
import com.maoyan.machine.httpclient.spring.utils.ReflectUtils;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月3日
 * 
 * @version 1.0
 */
public class HttpClientInvocationHandler implements InvocationHandler {
    private final Map<String, RequestEntityConverter> requestEntityConverters;
    private final Map<String, ResponseEntityConverter> responseEntityConverts;
    private final HttpClient httpClient;
    private final MetasManager metasManager;

    public HttpClientInvocationHandler(Map<String, RequestEntityConverter> requestEntityConverters,
            Map<String, ResponseEntityConverter> responseEntityConverts, HttpClient httpClient, MetasManager metasManager) {
        this.requestEntityConverters = requestEntityConverters;
        this.responseEntityConverts = responseEntityConverts;
        this.httpClient = httpClient;
        this.metasManager = metasManager;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Exception {
        List<HttpApiInterceptor> interceptors = metasManager.getApiMeta(method).getInterceptors();
        if (interceptors == null || interceptors.size() == 0) {
            return this.invoke0(proxy, method, args);
        }

        return new Invocation(interceptors, method, args[0]) {

            @Override
            protected Object invoke() throws Exception {
                return invoke0(proxy, this.method, this.request);
            }

        }.proceed();
    }

    private Object invoke0(Object proxy, Method method, Object request) throws Exception {
        HttpApiMeta meta = this.metasManager.getApiMeta(method);
        RequestParser requestParser = new RequestParser(request, meta.getRequestModelMeta());
        Map<String, String> headers = requestParser.getHeaders();
        Map<String, String> pathVariables = requestParser.getPathVariables();
        Object body = requestParser.getBody();

        HttpResponse response;
        String url = meta.getUrl();
        url = addPathVariablesToUrl(url, pathVariables);
        String httpMehtod = meta.getMethod();
        if (httpMehtod.equalsIgnoreCase("GET")) {
            response = this.doGet(url, headers, this.getQueryString(body));
        } else if (httpMehtod.equalsIgnoreCase("DELETE")) {
            response = this.doDelete(url, headers, this.getQueryString(body));
        } else {
            RequestEntityConverter requestConverter = requestEntityConverters.get(meta.getRequestMimeType());
            HttpEntity entity = requestConverter.getEntity(body, Charset.forName(meta.getRequestCharset()));
            String contentType = meta.getRequestMimeType() + ";charset=" + meta.getRequestCharset();
            if (httpMehtod.equalsIgnoreCase("POST")) {
                response = this.doPost(url, headers, entity, contentType);
            } else if (httpMehtod.equalsIgnoreCase("PUT")) {
                response = this.doPut(url, headers, entity, contentType);
            } else {
                throw new RuntimeException(String.format("不支持http method ‘%s’", httpMehtod));
            }
        }

        this.checkResponse(response);

        ModelMeta responseModelMeta = meta.getResponseModelMeta();
        Type responseBodyType = responseModelMeta.getBodyType();
        ResponseEntityConverter responseConvert = responseEntityConverts.get(meta.getResponseMimeType());
        Object result = responseConvert.convert(response.getEntity(), responseBodyType, Charset.forName(meta.getResponseCharset()));
        this.loadResponseHeaders(result, response, responseModelMeta);

        return result;
    }

    private void checkResponse(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        int code = statusLine.getStatusCode();
        if (code != 200) {
            String entityStr = "";
            try {
                entityStr = EntityUtils.toString(response.getEntity(),"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            } 
            throw new RuntimeException(String.format("http请求出错，code：%s，原因：%s，entity：%s", code, statusLine.getReasonPhrase(), entityStr));
        }
    }

    private String getQueryString(Object requestModel) throws IllegalArgumentException, IllegalAccessException, UnsupportedEncodingException {
        List<Field> allField = ReflectUtils.getEntityFields(requestModel.getClass());
        StringBuilder sbBuilder = new StringBuilder();
        sbBuilder.append("?");
        for (Field field : allField) {
            field.setAccessible(true);
            Object value = field.get(requestModel);
            if (value == null) {
                continue;
            }
            sbBuilder.append(field.getName()).append("=").append(value).append("&");
        }

        sbBuilder.setLength(sbBuilder.length() - 1);
        return sbBuilder.toString();
    }

    private static String addPathVariablesToUrl(String url, Map<String, String> pathVariables) {
        Pattern p = Pattern.compile("\\{(\\w+)\\}");
        Matcher m = p.matcher(url);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String s = m.group(1);
            String pathVariablValue = pathVariables.get(s);
            if (pathVariablValue == null) {
                throw new RuntimeException(String.format("没有为path variable '%s' 赋值，URL:%s", s, url));
            }
            m.appendReplacement(sb, pathVariablValue);
        }
        m.appendTail(sb);

        return sb.toString();
    }

    private HttpResponse doPost(String url, Map<String, String> headers, HttpEntity entity, String contentType) throws ClientProtocolException,
            IOException {
        HttpPost httpPost = new HttpPost(url);
        for (Entry<String, String> header : headers.entrySet()) {
            httpPost.setHeader(header.getKey(), header.getValue());
        }
        httpPost.addHeader("Content-Type", contentType);
        httpPost.setEntity(entity);
        return httpClient.execute(httpPost);
    }

    private HttpResponse doPut(String url, Map<String, String> headers, HttpEntity entity, String contentType) throws ClientProtocolException,
            IOException {
        HttpPut httpPut = new HttpPut(url);
        for (Entry<String, String> header : headers.entrySet()) {
            httpPut.setHeader(header.getKey(), header.getValue());
        }
        httpPut.addHeader("Content-Type", contentType);
        httpPut.setEntity(entity);
        return httpClient.execute(httpPut);
    }

    private HttpResponse doDelete(String url, Map<String, String> headers, String query) throws ClientProtocolException, IOException {
        HttpDelete httpDelete = new HttpDelete(url + query);
        for (Entry<String, String> header : headers.entrySet()) {
            httpDelete.setHeader(header.getKey(), header.getValue());
        }
        return httpClient.execute(httpDelete);
    }

    private HttpResponse doGet(String url, Map<String, String> headers, String query) throws ClientProtocolException, IOException {
        HttpGet httpGet = new HttpGet(url + query);
        for (Entry<String, String> header : headers.entrySet()) {
            httpGet.setHeader(header.getKey(), header.getValue());
        }
        return httpClient.execute(httpGet);
    }

    private void loadResponseHeaders(Object response, HttpResponse httpResponse, ModelMeta responseModelMeta) throws IllegalArgumentException,
            IllegalAccessException {
        List<Field> headerFields = responseModelMeta.getHeaderFields();
        for (Field field : headerFields) {
            String headerName = field.getName();
            String headerValue = httpResponse.getHeaders(headerName)[0].getValue();
            field.set(response, headerValue);
        }

    }

    private static class RequestParser {
        private final Map<String, String> headers;
        private final Map<String, String> pathVariables;
        private final Object body;

        private RequestParser(Object request, ModelMeta requestModelMeta) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
            this.headers = this.getHead(request, requestModelMeta);
            this.pathVariables = this.getPathVariable(request, requestModelMeta);
            if (requestModelMeta.getBodyField() != null) {
                this.body = requestModelMeta.getBodyField().get(request);
            } else {
                this.body = this.copyEntityField(request);
            }
        }
        
        private Object copyEntityField(Object requestModel) throws InstantiationException, IllegalAccessException{
            Object result = requestModel.getClass().newInstance();
            List<Field> entityFields = ReflectUtils.getEntityFields(requestModel.getClass());
            for (Field field : entityFields) {
                field.set(result, field.get(requestModel));
            }
            
            return result;
        }

        private Map<String, String> getHead(Object request, ModelMeta modelMeta) throws IllegalArgumentException, IllegalAccessException {
            Map<String, String> result = new HashMap<>();
            List<Field> headerFields = modelMeta.getHeaderFields();
            for (Field field : headerFields) {
                Object value = field.get(request);
                if (value == null) {
                    continue;
                }
                result.put(field.getName(), value.toString());
            }
            return result;
        }

        private Map<String, String> getPathVariable(Object request, ModelMeta modelMeta) throws IllegalArgumentException,
                IllegalAccessException {
            Map<String, String> result = new HashMap<>();
            List<Field> pathVariableFields = modelMeta.getPathVariableFields();
            for (Field field : pathVariableFields) {
                Object value = field.get(request);
                if (value == null) {
                    continue;
                }
                result.put(field.getName(), value.toString());
            }
            return result;
        }

        private Map<String, String> getHeaders() {
            return headers;
        }

        private Object getBody() {
            return body;
        }

        private Map<String, String> getPathVariables() {
            return pathVariables;
        }

    }
}
