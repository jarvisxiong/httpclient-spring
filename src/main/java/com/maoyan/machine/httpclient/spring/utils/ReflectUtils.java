/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.maoyan.machine.httpclient.spring.annotation.Header;
import com.maoyan.machine.httpclient.spring.annotation.PathVariable;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月13日
 * 
 * @version 1.0
 */
public class ReflectUtils {
    private static final ConcurrentHashMap<Class<?>, List<Field>> fieldCache = new ConcurrentHashMap<>();

    public static List<Field> getEntityFields(Class<?> requestType) {
        List<Field> result = fieldCache.get(requestType);
        if(result != null) {
            return result;
        }
        
        result = new ArrayList<>();
        Field[] declaredFields = requestType.getDeclaredFields();
        for (Field field : declaredFields) {
            if(Modifier.isStatic(field.getModifiers())
                    || field.isAnnotationPresent(Header.class)
                    || field.isAnnotationPresent(PathVariable.class)){
                continue;
            }
            field.setAccessible(true);
            result.add(field);
        }
        Class<?> superclass = requestType.getSuperclass();
        if(superclass != Object.class){
            result.addAll(getEntityFields(superclass));
        }
        
        fieldCache.put(requestType, result);
        return result;
    }
}
