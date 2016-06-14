/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.converter;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.maoyan.machine.httpclient.spring.meta.MimeType;
import com.maoyan.machine.httpclient.spring.utils.ReflectUtils;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
public class ForUrlEncodedConverter implements RequestEntityConverter {

    @Override
    public HttpEntity getEntity(Object object, Charset charset) throws Exception {
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        List<Field> allField = ReflectUtils.getAllField(object.getClass());
        for (Field field : allField) {
            field.setAccessible(true);
            Object value = field.get(object);
            if (value == null) {
                continue;
            }
            nameValuePairs.add(new BasicNameValuePair(field.getName(), value.toString()));

        }

        return new UrlEncodedFormEntity(nameValuePairs, charset);
    }

    @Override
    public String getMimeType() {
        return MimeType.FORM_Urlencoded;
    }

}
