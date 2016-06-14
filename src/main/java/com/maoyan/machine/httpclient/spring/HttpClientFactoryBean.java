package com.maoyan.machine.httpclient.spring;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.maoyan.machine.httpclient.spring.converter.RequestEntityConverter;
import com.maoyan.machine.httpclient.spring.converter.ResponseEntityConverter;
import com.maoyan.machine.httpclient.spring.meta.InterceptorInfo;
import com.maoyan.machine.httpclient.spring.meta.MetasManager;

public class HttpClientFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {
    private Map<String, RequestEntityConverter> requestEntityConverters = new HashMap<>();
    private Map<String, ResponseEntityConverter> responseEntityConverts = new HashMap<>();
    private HttpClient httpClient = null;
    private Class<T> mapperInterface;
    private ApplicationContext applicationContext;
    private List<InterceptorInfo> globleInterceptors;
    private String baseUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setRequestEntityConverters(Map<String, RequestEntityConverter> requestEntityConverters) {
        this.requestEntityConverters = requestEntityConverters;
    }

    public void setResponseEntityConverts(Map<String, ResponseEntityConverter> responseEntityConverts) {
        this.responseEntityConverts = responseEntityConverts;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void setGlobleInterceptors(List<InterceptorInfo> globleInterceptors) {
        this.globleInterceptors = globleInterceptors;
    }

    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        MetasManager metasManager = new MetasManager(this.mapperInterface, applicationContext, this.globleInterceptors, this.baseUrl);
        HttpClientInvocationHandler handler = new HttpClientInvocationHandler(requestEntityConverters, responseEntityConverts, httpClient,
                metasManager);

        Object newProxyInstance = Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, handler);
        return (T) newProxyInstance;
    }

    /**
     * {@inheritDoc}
     */
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
