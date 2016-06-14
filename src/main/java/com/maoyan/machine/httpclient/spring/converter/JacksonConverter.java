/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.maoyan.machine.httpclient.spring.meta.MimeType;
import com.maoyan.machine.httpclient.spring.utils.JackUtils;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
public class JacksonConverter implements RequestEntityConverter, ResponseEntityConverter {

    @Override
    public Object convert(HttpEntity httpEntity, final Type type, Charset charset) throws ParseException, IOException {
        String string = EntityUtils.toString(httpEntity, charset);
        return JackUtils.jsonToBeanByTypeReference(string, new TypeReference<Object>() {

            @Override
            public Type getType() {
                return type;
            }

        });
    }

    @Override
    public HttpEntity getEntity(Object object, Charset charset) throws JsonProcessingException {
        return new StringEntity(JackUtils.toJson(object), charset);
    }

    @Override
    public String getMimeType() {
        return MimeType.JSON;
    }

}
