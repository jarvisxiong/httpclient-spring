/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 配置在接口类上，用来指定http调用的Base URL
 * 
 * @author xujia06
 * @created 2016年6月6日
 * 
 * @version 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface BaseUrl {
    /**
     * 返回值可以是普通String或spEL表达式，
     * spEL表达式的计算结果可以是普通字符
     * 串也可以是实现了DynamicBaseUrl接
     * 口的bean，DynamicBaseUrl接口用来实现
     * 动态的baseUrl
     */
    String value();
}
