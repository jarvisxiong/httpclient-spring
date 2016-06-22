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
 * 当标记在请求实体字段上时，该字段值将被添加到请求头中，请求头名为字段名。
 * 当标记在响应体的字段上时，该字段将被赋予响应中同名响应头的值。
 * 
 * @author xujia06
 * @created 2016年6月6日
 * 
 * @version 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.FIELD })
public @interface Header {
    /**
     * Header名，如果不指定则使用被注解的字段名
     */
    String value() default "";
}
