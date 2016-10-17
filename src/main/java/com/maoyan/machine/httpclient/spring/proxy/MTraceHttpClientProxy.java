package com.maoyan.machine.httpclient.spring.proxy;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.meituan.mtrace.http.TraceHttpProcessor;

public class MTraceHttpClientProxy<T> extends HttpClientProxy<T> {
    private String appKey;
    
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    protected HttpClient createHttpClient() {
        PoolingHttpClientConnectionManager httpConnectionPool = new PoolingHttpClientConnectionManager();
        httpConnectionPool.setMaxTotal(this.maxTotalConnection);
        httpConnectionPool.setDefaultMaxPerRoute(this.defaultMaxPerRoute);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(this.socketTimeout)
                .setConnectTimeout(this.connectTimeout).build();
        TraceHttpProcessor traceHttpProcessor = new TraceHttpProcessor();
        if(this.appKey != null) {
            traceHttpProcessor.initAppkey(appKey);
        }
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(httpConnectionPool)
                .addInterceptorFirst((HttpResponseInterceptor)traceHttpProcessor)
                .addInterceptorFirst((HttpRequestInterceptor)traceHttpProcessor)
                .setDefaultRequestConfig(requestConfig)
                .build();
        return httpClient;
    }
}
