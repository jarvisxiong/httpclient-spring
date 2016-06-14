/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

    public static List<Field> getAllField(Class<?> clazz) {
        List<Field> result = fieldCache.get(clazz);
        if (result != null) {
            return result;
        }

        result = new ArrayList<>();
        if (clazz == Object.class) {
            return result;
        }

        Field[] declaredFields = clazz.getDeclaredFields();
        result.addAll(Arrays.asList(declaredFields));
        Class<?> superclass = clazz.getSuperclass();
        result.addAll(getAllField(superclass));
        fieldCache.put(clazz, result);
        return result;
    }
}
