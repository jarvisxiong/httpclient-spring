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

import com.maoyan.machine.httpclient.spring.meta.Charset;
import com.maoyan.machine.httpclient.spring.meta.MimeType;

/**
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
     * 请求方法
     */
    String method();

    String mimeType() default MimeType.FORM_Urlencoded;

    String charset() default Charset.UTF_8;
}
