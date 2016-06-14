/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring;

import java.util.List;

import com.maoyan.machine.httpclient.spring.meta.InterceptorInfo;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月8日
 * 
 * @version 1.0
 */
public class Configuration {
    private int socketTimeout = 3000;
    private int maxTotalConnection = 100;
    private int defaultMaxPerRoute = 100;
    private int connectTimeout = 3000;
    private String baseUrl;
    private List<String> converterClassNames;
    List<InterceptorInfo> globlInterceptors;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<InterceptorInfo> getGloblInterceptors() {
        return globlInterceptors;
    }

    public void setGloblInterceptors(List<InterceptorInfo> globlInterceptors) {
        this.globlInterceptors = globlInterceptors;
    }

    public List<String> getConverterClassNames() {
        return converterClassNames;
    }

    public void setConverterClassNames(List<String> converterClassNames) {
        this.converterClassNames = converterClassNames;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxTotalConnection() {
        return maxTotalConnection;
    }

    public void setMaxTotalConnection(int maxTotalConnection) {
        this.maxTotalConnection = maxTotalConnection;
    }

    public int getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
