package com.maoyan.machine.httpclient.spring.meta;

/**
 * 该接口用于提供配置动态baseUrl的动能
 * 
 * 方法：配置baseUrl时，使用spEL表达式
 * 指定一个实现了DynamicBaseUrl接口的bean
 *
 * @author xujia06
 * @created 2016年8月15日
 *
 * @version 1.0
 */
public interface DynamicBaseUrl {
    String getBaseUrl();
}
