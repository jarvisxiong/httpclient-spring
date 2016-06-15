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
 * @author xujia06
 * @created 2016年6月6日
 * 
 * @version 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Response {

    String mimeType() default MimeTypes.JSON;

    String charset() default Charsets.UTF_8;
}
