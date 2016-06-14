/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.interceptor;

import java.util.List;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月12日
 * 
 * @version 1.0
 */
public abstract class Invocation {
    private List<HttpApiInterceptor> interceptorList;
    private int index = 0;

    public Invocation(List<HttpApiInterceptor> interceptorList) {
        this.interceptorList = interceptorList;
    }

    public Object proceed() throws Throwable {
        if (this.index == this.interceptorList.size()) {
            return this.invoke();
        }
        Object result = interceptorList.get(index++).invoke(this);
        return result;
    }

    abstract protected Object invoke() throws Throwable;
}
