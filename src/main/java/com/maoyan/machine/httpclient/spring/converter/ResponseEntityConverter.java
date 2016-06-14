/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.converter;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;

/**
 * @author xujia06
 * @created 2016年6月3日
 * 
 * @version 1.0
 */
public interface ResponseEntityConverter {
    Object convert(HttpEntity httpEntity, Type type, Charset charset) throws Exception;

    String getMimeType();
}
