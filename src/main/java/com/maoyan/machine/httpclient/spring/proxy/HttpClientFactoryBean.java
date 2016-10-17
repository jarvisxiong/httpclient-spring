package com.maoyan.machine.httpclient.spring.proxy;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.FactoryBean;

import com.meituan.mtrace.http.TraceHttpProcessor;

public class HttpClientFactoryBean implements FactoryBean<HttpClient> {
    protected int socketTimeout = 3000;
    protected int maxTotalConnection = 100;
    protected int defaultMaxPerRoute = 100;
    protected int connectTimeout = 3000;
    protected boolean trace = false;
    protected String appKey;
    
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

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public HttpClient getObject() throws Exception {
        PoolingHttpClientConnectionManager httpConnectionPool = new PoolingHttpClientConnectionManager();
        httpConnectionPool.setMaxTotal(this.maxTotalConnection);
        httpConnectionPool.setDefaultMaxPerRoute(this.defaultMaxPerRoute);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.socketTimeout)
                .setConnectTimeout(this.connectTimeout).build();
        
        if(this.trace) {
            TraceHttpProcessor traceHttpProcessor = new TraceHttpProcessor();
            if(this.appKey != null) {
                traceHttpProcessor.initAppkey(appKey);
            }
            
            return HttpClients.custom()
                    .setConnectionManager(httpConnectionPool)
                    .addInterceptorFirst((HttpResponseInterceptor)traceHttpProcessor)
                    .addInterceptorFirst((HttpRequestInterceptor)traceHttpProcessor)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
        }else {
            return HttpClients.custom().setConnectionManager(httpConnectionPool).setDefaultRequestConfig(requestConfig).build();
        }
    }

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
    

}
