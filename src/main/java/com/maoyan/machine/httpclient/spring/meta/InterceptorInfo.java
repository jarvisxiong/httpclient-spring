/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.meta;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月14日
 * 
 * @version 1.0
 */
public class InterceptorInfo {
    private int order;
    private String InterceptorBeanName;

    public InterceptorInfo(int order, String interceptorBeanName) {
        super();
        this.order = order;
        InterceptorBeanName = interceptorBeanName;
    }

    public int getOrder() {
        return order;
    }

    public String getInterceptorBeanName() {
        return InterceptorBeanName;
    }
}
