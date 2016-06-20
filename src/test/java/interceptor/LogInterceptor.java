/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package interceptor;

import java.lang.reflect.Method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maoyan.machine.httpclient.spring.interceptor.HttpApiInterceptor;
import com.maoyan.machine.httpclient.spring.interceptor.Invocation;
import com.maoyan.machine.httpclient.spring.utils.JackUtils;

/**
 * TODO 在这里编写类的功能描述
 *
 * @author xujia06
 * @created 2016年6月16日
 *
 * @version 1.0
 */
public class LogInterceptor implements HttpApiInterceptor {
    @Override
    public Object invoke(Invocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        long spendTime = System.currentTimeMillis() - startTime;
        
        StringBuilder sb = new StringBuilder("http接口调用:");
        Method method = invocation.getMethod();
        String className = method.getDeclaringClass().getName();
        sb.append(className).append("#").append(method.getName()).append(":耗时").append(spendTime).append("ms");
        sb.append("\r\nrequest:").append(this.serialize(invocation.getRequest()));
        sb.append("\r\nresponse:").append(this.serialize(result));
        
        System.err.println(sb.toString());
        return result;
    }
    
    private String serialize(Object o) throws JsonProcessingException{
        if(o==null) return "null";
        return JackUtils.toJson(o);
    }

}
