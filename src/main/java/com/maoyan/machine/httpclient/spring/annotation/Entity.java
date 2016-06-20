/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 当标记在请求实体字段上时，该字段的值将被序列化为请求体。
 * 如果请求实体中没有标记@Entity的字段，整个实体将被序列化为请求体（不包括Header和PathVariable）。
 * 
 * 当标记在响应实体字段上时，该字段将被赋予由响应体反序列化得到的对象。
 * 如果响应实体中没有标记@Entity的字段， 响应体将被反序列化到该实体对象中。 
 * 
 * 注意，一个实体类中只能最多有一个字段标记@Entity。
 * 
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
@Retention(RUNTIME)
@Target({ ElementType.FIELD })
public @interface Entity {

}
