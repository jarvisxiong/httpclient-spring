package com.maoyan.machine.httpclient.spring.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.maoyan.machine.httpclient.spring.HttpClientInvocationHandler;
import com.maoyan.machine.httpclient.spring.converter.FormUrlEncodedConverter;
import com.maoyan.machine.httpclient.spring.converter.JacksonConverter;
import com.maoyan.machine.httpclient.spring.converter.RequestEntityConverter;
import com.maoyan.machine.httpclient.spring.converter.ResponseEntityConverter;
import com.maoyan.machine.httpclient.spring.interceptor.HttpApiInterceptor;
import com.maoyan.machine.httpclient.spring.meta.InterceptorInfo;
import com.maoyan.machine.httpclient.spring.meta.MetasManager;

public class HttpClientProxy<T> implements FactoryBean<T>, ApplicationContextAware {
    private ApplicationContext applicationContext;
    
    private HttpClient httpClient = null;
    private Class<T> serviceInterface;
    private List<HttpApiInterceptor> interceptors;
    private String baseUrl;
    
    protected int socketTimeout = 3000;
    protected int maxTotalConnection = 100;
    protected int defaultMaxPerRoute = 100;
    protected int connectTimeout = 3000;

    public void setInterceptors(List<HttpApiInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setMaxTotalConnection(int maxTotalConnection) {
        this.maxTotalConnection = maxTotalConnection;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void setGlobleInterceptors(List<HttpApiInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
        MetasManager metasManager = new MetasManager(this.serviceInterface, applicationContext, new ArrayList<InterceptorInfo>(0), this.baseUrl);
        if(this.interceptors != null && this.interceptors.size() > 0){
            Method[] methods = this.serviceInterface.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                metasManager.getApiMeta(methods[i]).setInterceptors(this.interceptors);
            }
        }
        
        if(this.httpClient == null) {
            this.httpClient = this.createHttpClient();
        }
        HttpClientInvocationHandler handler = new HttpClientInvocationHandler(this.getRequestEntityConverters(), this.getResponseEntityConverter(), httpClient,
                metasManager);

        Object newProxyInstance = Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] { serviceInterface }, handler);
        return (T) newProxyInstance;
    }
    
    protected HttpClient createHttpClient() {
        PoolingHttpClientConnectionManager httpConnectionPool = new PoolingHttpClientConnectionManager();
        httpConnectionPool.setMaxTotal(this.maxTotalConnection);
        httpConnectionPool.setDefaultMaxPerRoute(this.defaultMaxPerRoute);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.socketTimeout)
                .setConnectTimeout(this.connectTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpConnectionPool).setDefaultRequestConfig(requestConfig).build();
        
        
        return httpClient;
    }

    /**
     * {@inheritDoc}
     */
    public Class<T> getObjectType() {
        return this.serviceInterface;
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
    
    protected Map<String, RequestEntityConverter> getRequestEntityConverters() {
        Map<String, RequestEntityConverter> result = new HashMap<>();

        // 添加默认converer
        FormUrlEncodedConverter forUrlEncodedConverter = new FormUrlEncodedConverter();
        result.put(forUrlEncodedConverter.getMimeType(), forUrlEncodedConverter);
        JacksonConverter jacksonConverter = new JacksonConverter();
        result.put(jacksonConverter.getMimeType(), jacksonConverter);
        
        return result;
    }
    
    protected Map<String, ResponseEntityConverter> getResponseEntityConverter(){
        Map<String, ResponseEntityConverter> result = new HashMap<>();

        // 添加默认converer
        JacksonConverter jacksonConverter = new JacksonConverter();
        result.put(jacksonConverter.getMimeType(), jacksonConverter);
        
        return result;
    }
}
