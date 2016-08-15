/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.meta;

import java.util.List;

import com.maoyan.machine.httpclient.spring.interceptor.HttpApiInterceptor;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月6日
 * 
 * @version 1.0
 */
public class HttpApiMeta {
    private Object baseUrl;
    private String path;
    private String method;
    private String requestMimeType;
    private String responseMimeType;
    private String requestCharset;
    private String responseCharset;
    private Class<?> requestType;
    private Class<?> responseType;
    private ModelMeta requestModelMeta;
    private ModelMeta responseModelMeta;
    private List<HttpApiInterceptor> interceptors;

    public List<HttpApiInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<HttpApiInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?> getResponseType() {
        return responseType;
    }

    public void setResponseType(Class<?> responseType) {
        this.responseType = responseType;
    }

    public Class<?> getRequestType() {
        return requestType;
    }

    public void setRequestType(Class<?> requestType) {
        this.requestType = requestType;
    }

    public ModelMeta getRequestModelMeta() {
        return requestModelMeta;
    }

    public void setRequestModelMeta(ModelMeta requestModelMeta) {
        this.requestModelMeta = requestModelMeta;
    }

    public ModelMeta getResponseModelMeta() {
        return responseModelMeta;
    }

    public void setResponseModelMeta(ModelMeta responseModelMeta) {
        this.responseModelMeta = responseModelMeta;
    }

    public String getRequestMimeType() {
        return requestMimeType;
    }

    public void setRequestMimeType(String requestMimeType) {
        this.requestMimeType = requestMimeType;
    }

    public String getResponseMimeType() {
        return responseMimeType;
    }

    public void setResponseMimeType(String responseMimeType) {
        this.responseMimeType = responseMimeType;
    }

    public String getRequestCharset() {
        return requestCharset;
    }

    public void setRequestCharset(String requestCharset) {
        this.requestCharset = requestCharset;
    }

    public String getResponseCharset() {
        return responseCharset;
    }

    public void setResponseCharset(String responseCharset) {
        this.responseCharset = responseCharset;
    }

    public Object getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(Object baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
