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
 * 用于将响应体反序列化为java对象
 * 
 * @author xujia06
 * @created 2016年6月3日
 * 
 * @version 1.0
 */
public interface ResponseEntityConverter {
    /**
     * @param httpEntity 响应实体
     * @param type 被反序列化对象的类型
     * @param charset 响应头字符集
     * @return
     * @throws Exception
     */
    Object convert(HttpEntity httpEntity, Type type, Charset charset) throws Exception;
    /**
     * 返回该Converter处理的MimeType
     */
    String getMimeType();
}
