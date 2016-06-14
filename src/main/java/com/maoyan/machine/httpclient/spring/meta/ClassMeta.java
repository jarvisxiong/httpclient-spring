/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.meta;

import java.util.List;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月14日
 * 
 * @version 1.0
 */
public class ClassMeta {
    private String baseUrl;
    private List<InterceptorInfo> interceptors;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<InterceptorInfo> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<InterceptorInfo> interceptors) {
        this.interceptors = interceptors;
    }
}
