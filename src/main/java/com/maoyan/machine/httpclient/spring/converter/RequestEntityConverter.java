/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.converter;

import java.nio.charset.Charset;

import org.apache.http.HttpEntity;

/**
 * 用于java对象转换成http请求体
 * 
 * @author xujia06
 * @created 2016年6月3日
 * 
 * @version 1.0
 */
public interface RequestEntityConverter {
    /**
     * @param object 请求对应的java对象
     * @param charset 请求体字符集
     * @return HttpEntity
     * @throws Exception
     */
    HttpEntity getEntity(Object object, Charset charset) throws Exception;

    /**
     * 返回该Converter处理的MimeType
     */
    String getMimeType();
}
