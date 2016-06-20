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
import com.maoyan.machine.httpclient.spring.meta.MimeTypes;

/**
 * 用于定义http请求，可配置在请求的实体类上或接口方法上，接口方法上的优先级更高。
 * 
 * @author xujia06
 * @created 2016年6月6日
 * 
 * @version 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Request {
    /**
     * 请求路径
     */
    String path();

    /**
     * 请求方法，当前支持GET、POST、PUT、DELETE
     */
    String method();

    /**
     * 请求体类型，方法是GET和Delete是不起作用
     */
    String mimeType() default MimeTypes.FORM_Urlencoded;

    /**
     * 请求体使用的字符集，方法是GET和Delete是不起作用
     */
    String charset() default Charsets.UTF_8;
}
