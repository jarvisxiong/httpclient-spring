/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package api.i;

import model.TestModel;

import com.maoyan.machine.httpclient.spring.annotation.BaseUrl;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
@BaseUrl("#{configBean}")
public interface TestApi {
//    @Interceptor(order = 2, beanName = "interceptor")
    TestModel.Resp test(TestModel.Req req);
}
