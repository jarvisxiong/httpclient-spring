/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.meta;

import java.lang.reflect.Field;

/**
 * TODO 在这里编写类的功能描述
 *
 * @author xujia06
 * @created 2016年6月22日
 *
 * @version 1.0
 */
public class HeaderFieldInfo {
    private final Field field;
    private final String headerName;
    
    public HeaderFieldInfo(Field field, String headerName) {
        this.field = field;
        this.headerName = headerName;
    }
    public Field getField() {
        return field;
    }
    public String getHeaderName() {
        return headerName;
    }
}
