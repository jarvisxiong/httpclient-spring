/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package api;

import model.TestModel;

import com.maoyan.machine.httpclient.spring.annotation.BaseUrl;
import com.maoyan.machine.httpclient.spring.annotation.Interceptor;
import com.maoyan.machine.httpclient.spring.annotation.Interceptors;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
@BaseUrl("#{configProperties['baseUrl']}")
@Interceptor(order = 2, beanName = "interceptor")
@Interceptors({ @Interceptor(order = 3, beanName = "interceptor"), @Interceptor(order = 4, beanName = "interceptor") })
public interface TestApi {
    @Interceptor(order = 2, beanName = "interceptor")
    @Interceptors({ @Interceptor(order = 3, beanName = "interceptor"), @Interceptor(order = 4, beanName = "interceptor") })
    TestModel.Resp test(TestModel.Req req);
}
