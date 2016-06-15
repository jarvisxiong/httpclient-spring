/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.interceptor;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月12日
 * 
 * @version 1.0
 */
public interface HttpApiInterceptor {
    Object invoke(Invocation invocation) throws Exception;
}
