/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package interceptor;

import com.maoyan.machine.httpclient.spring.interceptor.HttpApiInterceptor;
import com.maoyan.machine.httpclient.spring.interceptor.Invocation;

/**
 * TODO 在这里编写类的功能描述
 * 
 * @author xujia06
 * @created 2016年6月13日
 * 
 * @version 1.0
 */
public class TestInterceptor implements HttpApiInterceptor {

    /**
     * TODO 在这里编写被覆盖方法的注释
     */
    @Override
    public Object invoke(Invocation invocation) throws Throwable {
        System.err.println("===============before===============");
        Object proceed = invocation.proceed();
        System.err.println("===============after===============");
        return proceed;
    }

}
