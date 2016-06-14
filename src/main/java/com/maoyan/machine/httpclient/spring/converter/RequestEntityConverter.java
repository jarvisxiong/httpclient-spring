/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.converter;

import java.nio.charset.Charset;

import org.apache.http.HttpEntity;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月3日
 * 
 * @version 1.0
 */
public interface RequestEntityConverter {
    HttpEntity getEntity(Object object, Charset charset) throws Exception;

    String getMimeType();
}
