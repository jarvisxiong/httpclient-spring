/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
public class ModelMeta {
    private List<HeaderFieldInfo> headerFields;
    private List<Field> pathVariableFields;
    private Field bodyField;
    private Type bodyType;

    public List<HeaderFieldInfo> getHeaderFields() {
        return headerFields;
    }

    public void setHeaderFields(List<HeaderFieldInfo> headerFields) {
        this.headerFields = headerFields;
    }

    public Field getBodyField() {
        return bodyField;
    }

    public void setBodyField(Field bodyField) {
        this.bodyField = bodyField;
    }

    public Type getBodyType() {
        return bodyType;
    }

    public void setBodyType(Type bodyType) {
        this.bodyType = bodyType;
    }

    public List<Field> getPathVariableFields() {
        return pathVariableFields;
    }

    public void setPathVariableFields(List<Field> pathVariableFields) {
        this.pathVariableFields = pathVariableFields;
    }
}
