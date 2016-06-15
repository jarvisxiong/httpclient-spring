/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.interceptor;

import java.lang.reflect.Method;
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
    protected Method method;
    protected Object request;
    private int index = 0;

    public Invocation(List<HttpApiInterceptor> interceptorList, Method method, Object request) {
        super();
        this.interceptorList = interceptorList;
        this.method = method;
        this.request = request;
    }

    public Object proceed() throws Exception {
        if (this.index == this.interceptorList.size()) {
            return this.invoke();
        }
        Object result = interceptorList.get(index++).invoke(this);
        return result;
    }
    
    

    public Method getMethod() {
        return method;
    }

    public Object getRequest() {
        return request;
    }

    abstract protected Object invoke() throws Exception;
}
