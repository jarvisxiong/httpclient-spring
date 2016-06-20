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

import com.maoyan.machine.httpclient.spring.meta.Charsets;

/**
 * 用于定义http响应，可配置在请求的实体类上或接口方法上，接口方法上的优先级更高。
 * 
 * @author xujia06
 * @created 2016年6月6日
 * 
 * @version 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Response {

    /**
     * 响应体类型，如果用户不指定，将通过http响应‘Content-Type’确定。
     * 优先使用用户指定的类型
     */
    String mimeType() default "";

    /**
     * 响应体的默认字符集，当http响应‘Content-Type’中无法判断字符集时使用
     */
    String charset() default Charsets.UTF_8;
}
