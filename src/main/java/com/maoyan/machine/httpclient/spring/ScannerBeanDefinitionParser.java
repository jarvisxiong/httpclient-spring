package com.maoyan.machine.httpclient.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.maoyan.machine.httpclient.spring.converter.FormUrlEncodedConverter;
import com.maoyan.machine.httpclient.spring.converter.JacksonConverter;
import com.maoyan.machine.httpclient.spring.converter.RequestEntityConverter;
import com.maoyan.machine.httpclient.spring.converter.ResponseEntityConverter;
import com.maoyan.machine.httpclient.spring.meta.InterceptorInfo;

public class ScannerBeanDefinitionParser implements BeanDefinitionParser {

    private static String ATTRIBUTE_PACKAGE = "package";
    private static String ATTRIBUTE_SOCKET_TIMEOUT = "socketTimeout";
    private static String ATTRIBUTE_MAX_TOTAL_CONNECTION = "maxTotalConnection";
    private static String ATTRIBUTE_DEFAULT_MAX_PER_ROUTE = "defaultMaxPerRoute";
    private static String ATTRIBUTE_CONNECT_TIMEOUT = "connectTimeout";
    private static String ATTRIBUTE_BASE_URL = "baseUrl";
    private static String NAME_SPACE = "http://machine.maoyan.com/schema/httpclient";

    public synchronized BeanDefinition parse(Element element, ParserContext parserContext) {
        ClassPathHttpClientScanner scanner = new ClassPathHttpClientScanner(parserContext.getRegistry());
        XmlReaderContext readerContext = parserContext.getReaderContext();
        scanner.setResourceLoader(readerContext.getResourceLoader());
        scanner.registerFilters();

        Configuration configuration = this.getConfiguration(element, parserContext);
        scanner.setHttpClient(this.createHttpClient(configuration));
        scanner.setRequestEntityConverters(this.getRequestEntityConverters(configuration));
        scanner.setResponseEntityConverts(this.getResponseEntityConverters(configuration));
        scanner.setGlobleInterceptors(configuration.getGloblInterceptors());
        scanner.setBaseUrl(configuration.getBaseUrl());
        String basePackage = element.getAttribute(ATTRIBUTE_PACKAGE);
        scanner.scan(StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        return null;
    }

    private Configuration getConfiguration(Element element, ParserContext parserContext) {
        Configuration configuration = new Configuration();
        String attribute = element.getAttribute(ATTRIBUTE_SOCKET_TIMEOUT);
        if (attribute != null && !attribute.equals("")) {
            configuration.setSocketTimeout(Integer.parseInt(attribute));
        }
        attribute = element.getAttribute(ATTRIBUTE_MAX_TOTAL_CONNECTION);
        if (attribute != null && !attribute.equals("")) {
            configuration.setMaxTotalConnection(Integer.parseInt(attribute));
        }
        attribute = element.getAttribute(ATTRIBUTE_DEFAULT_MAX_PER_ROUTE);
        if (attribute != null && !attribute.equals("")) {
            configuration.setDefaultMaxPerRoute(Integer.parseInt(attribute));
        }
        attribute = element.getAttribute(ATTRIBUTE_CONNECT_TIMEOUT);
        if (attribute != null && !attribute.equals("")) {
            configuration.setConnectTimeout(Integer.parseInt(attribute));
        }
        attribute = element.getAttribute(ATTRIBUTE_BASE_URL);
        configuration.setBaseUrl(attribute);

        List<String> converterClassNames = new ArrayList<>();
        NodeList convertersNodeList = element.getElementsByTagNameNS(NAME_SPACE, "entity-converter");
        if (convertersNodeList != null && convertersNodeList.getLength() > 0) {
            for (int i = 0; i < convertersNodeList.getLength(); i++) {
                Node converterNode = convertersNodeList.item(i);
                Node textNode = converterNode.getFirstChild();
                String coverterClassName = textNode.getNodeValue();
                converterClassNames.add(coverterClassName);
            }
        }
        configuration.setConverterClassNames(converterClassNames);

        List<InterceptorInfo> interceptors = new ArrayList<>();
        NodeList interceptorNodeList = element.getElementsByTagNameNS(NAME_SPACE, "interceptor");
        if (interceptorNodeList != null && interceptorNodeList.getLength() > 0) {
            for (int i = 0; i < interceptorNodeList.getLength(); i++) {
                Element interceptorElement = (Element) interceptorNodeList.item(i);
                int order = Integer.parseInt(interceptorElement.getAttribute("order"));
                interceptors.add(new InterceptorInfo(order, interceptorElement.getAttribute("beanName")));
            }
        }
        configuration.setGloblInterceptors(interceptors);
        return configuration;
    }

    private HttpClient createHttpClient(Configuration configuration) {
        PoolingHttpClientConnectionManager httpConnectionPool = new PoolingHttpClientConnectionManager();
        httpConnectionPool.setMaxTotal(configuration.getMaxTotalConnection());
        httpConnectionPool.setDefaultMaxPerRoute(configuration.getDefaultMaxPerRoute());
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(configuration.getSocketTimeout())
                .setConnectTimeout(configuration.getConnectTimeout()).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpConnectionPool).setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }

    private Map<String, RequestEntityConverter> getRequestEntityConverters(Configuration configuration) {
        Map<String, RequestEntityConverter> result = new HashMap<>();

        // 添加默认converer
        FormUrlEncodedConverter forUrlEncodedConverter = new FormUrlEncodedConverter();
        result.put(forUrlEncodedConverter.getMimeType(), forUrlEncodedConverter);
        JacksonConverter jacksonConverter = new JacksonConverter();
        result.put(jacksonConverter.getMimeType(), jacksonConverter);

        for (String converterClassName : configuration.getConverterClassNames()) {
            try {
                Class<?> converterClass = Class.forName(converterClassName);
                if (RequestEntityConverter.class.isAssignableFrom(converterClass)) {
                    RequestEntityConverter newInstance = (RequestEntityConverter) converterClass.newInstance();
                    result.put(newInstance.getMimeType(), newInstance);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private Map<String, ResponseEntityConverter> getResponseEntityConverters(Configuration configuration) {
        Map<String, ResponseEntityConverter> result = new HashMap<>();

        // 添加默认converer
        JacksonConverter jacksonConverter = new JacksonConverter();
        result.put(jacksonConverter.getMimeType(), jacksonConverter);

        for (String converterClassName : configuration.getConverterClassNames()) {
            try {
                Class<?> converterClass = Class.forName(converterClassName);
                if (ResponseEntityConverter.class.isAssignableFrom(converterClass)) {
                    ResponseEntityConverter newInstance = (ResponseEntityConverter) converterClass.newInstance();
                    result.put(newInstance.getMimeType(), newInstance);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
