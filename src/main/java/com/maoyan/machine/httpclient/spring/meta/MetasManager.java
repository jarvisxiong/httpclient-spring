/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package com.maoyan.machine.httpclient.spring.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import com.maoyan.machine.httpclient.spring.annotation.BaseUrl;
import com.maoyan.machine.httpclient.spring.annotation.Entity;
import com.maoyan.machine.httpclient.spring.annotation.Header;
import com.maoyan.machine.httpclient.spring.annotation.Interceptor;
import com.maoyan.machine.httpclient.spring.annotation.Interceptors;
import com.maoyan.machine.httpclient.spring.annotation.PathVariable;
import com.maoyan.machine.httpclient.spring.annotation.Request;
import com.maoyan.machine.httpclient.spring.annotation.Response;
import com.maoyan.machine.httpclient.spring.interceptor.HttpApiInterceptor;

/**
 * @author xujia06
 * @created 2016年6月6日
 * 
 * @version 1.0
 */
public class MetasManager {
    private final HashMap<Method, HttpApiMeta> apiMetasCache = new HashMap<Method, HttpApiMeta>();

    public MetasManager(Class<?> interfaceClass, ApplicationContext applicationContext, List<InterceptorInfo> globleInterceptors, String globleBaseUrl) {
        ClassMeta classMeta = this.parseClassMeta(interfaceClass, applicationContext, globleBaseUrl);
        Method[] methods = interfaceClass.getDeclaredMethods();
        for (Method method : methods) {
            HttpApiMeta apiMeta = new MetasParser(method, applicationContext, globleInterceptors, classMeta).parse();
            this.apiMetasCache.put(method, apiMeta);
        }
    }

    private ClassMeta parseClassMeta(Class<?> interfaceClass, ApplicationContext applicationContext, String globleBaseUrl) {
        String baseUrl = globleBaseUrl;
        ClassMeta classMeta = new ClassMeta();
        BaseUrl baseUrlAnno = interfaceClass.getAnnotation(BaseUrl.class);
        if (baseUrlAnno != null) {
            baseUrl = baseUrlAnno.value();
        }

        if (baseUrl == null || baseUrl.equals("")) {
            throw new RuntimeException("没有指定baseUrl");
        }
        baseUrl = (String) getSpELValue(baseUrl, applicationContext);
        classMeta.setBaseUrl(baseUrl);

        List<Interceptor> interceptorAnnoList = new ArrayList<>();
        Interceptor interceptorAnno = interfaceClass.getAnnotation(Interceptor.class);
        if (interceptorAnno != null) {
            interceptorAnnoList.add(interceptorAnno);
        }
        Interceptors interceptorsAnno = interfaceClass.getAnnotation(Interceptors.class);
        if (interceptorsAnno != null) {
            Interceptor[] value = interceptorsAnno.value();
            interceptorAnnoList.addAll(Arrays.asList(value));
        }
        List<InterceptorInfo> interceptors = getInterceptorsFromAnnotation(interceptorAnnoList);
        classMeta.setInterceptors(interceptors);

        return classMeta;
    }

    private List<InterceptorInfo> getInterceptorsFromAnnotation(List<Interceptor> list) {
        List<InterceptorInfo> result = new ArrayList<>();
        for (Interceptor interceptorAnno : list) {
            int order = interceptorAnno.order();
            String beanName = interceptorAnno.beanName();
            result.add(new InterceptorInfo(order, beanName));
        }

        return result;
    }

    private ModelMeta parseModelMeatas(Class<?> modelType) {
        ModelMeta result = new ModelMeta();
        List<Field> headerFields = getAnnotatedFields(modelType, Header.class);
        result.setHeaderFields(this.getHeaderFieldInfo(headerFields));
        List<Field> pathVariableFields = getAnnotatedFields(modelType, PathVariable.class);
        result.setPathVariableFields(pathVariableFields);
        List<Field> bodyFieds = getAnnotatedFields(modelType, Entity.class);
        if (bodyFieds.size() == 0) {
            result.setBodyType(modelType);
        } else if (bodyFieds.size() == 1) {
            result.setBodyField(bodyFieds.get(0));
            result.setBodyType(result.getBodyField().getType());
        } else {
            throw new RuntimeException("一个实体类上，只能有最多一个@body注解");
        }

        return result;
    }
    
    private List<HeaderFieldInfo> getHeaderFieldInfo(List<Field> headerFields) {
        List<HeaderFieldInfo> result = new ArrayList<>();
        for (Field field : headerFields) {
            Header headerAnno = field.getAnnotation(Header.class);
            String headerName = headerAnno.value();
            if("".equals(headerName)) {
                headerName = field.getName();
            }
            result.add(new HeaderFieldInfo(field, headerName));
        }
        return result;
    }
    

    public HttpApiMeta getApiMeta(Method method) {
        return apiMetasCache.get(method);
    }

    private static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                field.setAccessible(true);
                result.add(field);
            }
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != Object.class) {
            result.addAll(getAnnotatedFields(superclass, annotationClass));
        }
        return result;
    }

    private Object getSpELValue(String spELString, ApplicationContext applicationContext) {
        ConfigurableApplicationContext cotext = (ConfigurableApplicationContext) applicationContext;
        ConfigurableListableBeanFactory beanFactory = cotext.getBeanFactory();
        BeanExpressionResolver beanExpressionResolver = beanFactory.getBeanExpressionResolver();
        return beanExpressionResolver.evaluate(spELString, new BeanExpressionContext(beanFactory, null));
    }

    private class MetasParser {
        private Method method;
        private ApplicationContext applicationContext;
        private List<InterceptorInfo> globleInterceptors;
        private ClassMeta classMeta;

        private Class<?> requestType;
        private Class<?> responseType;
        private Request request;
        private Response response;

        private MetasParser(Method method, ApplicationContext applicationContext, List<InterceptorInfo> globleInterceptors, ClassMeta classMeta) {
            this.method = method;
            this.applicationContext = applicationContext;
            this.globleInterceptors = globleInterceptors;
            this.classMeta = classMeta;
            this.init();
        }

        private HttpApiMeta parse() {
            HttpApiMeta result = new HttpApiMeta();
            result.setRequestType(this.requestType);
            result.setResponseType(this.responseType);
            result.setUrl(this.getUrl());
            result.setMethod(this.request.method());
            result.setRequestMimeType(this.getRequestMimeType());
            result.setRequestCharset(this.request.charset());
            result.setResponseMimeType(this.response.mimeType());
            result.setResponseCharset(this.response.charset());
            result.setResponseModelMeta(parseModelMeatas(this.responseType));
            result.setRequestModelMeta(parseModelMeatas(this.requestType));
            result.setInterceptors(this.getInterceptors());
            return result;
        }

        private String getRequestMimeType() {
            return this.request.mimeType();
        }

        private void init() {
            this.requestType = method.getParameterTypes()[0];
            this.responseType = method.getReturnType();
            this.request = method.getAnnotation(Request.class);
            if (this.request == null) {
                this.request = this.requestType.getAnnotation(Request.class);
            }
            if (this.request == null) {
                throw new RuntimeException(String.format("没有找到@Request配置信息，method：%s", this.method.toString()));
            }
            this.response = method.getAnnotation(Response.class);
            if (this.response == null) {
                this.response = responseType.getAnnotation(Response.class);
            }
            if (this.response == null) {
                throw new RuntimeException(String.format("没有找到@Response配置信息，method：%s", this.method.toString()));
            }
        }

        private String getUrl() {
            return this.classMeta.getBaseUrl() + request.path();
        }

        private List<HttpApiInterceptor> getInterceptors() {
            List<Interceptor> interceptorAnnoList = new ArrayList<>();
            Interceptor interceptorAnno = method.getAnnotation(Interceptor.class);
            if (interceptorAnno != null) {
                interceptorAnnoList.add(interceptorAnno);
            }
            Interceptors interceptorsAnno = method.getAnnotation(Interceptors.class);
            if (interceptorsAnno != null) {
                Interceptor[] value = interceptorsAnno.value();
                interceptorAnnoList.addAll(Arrays.asList(value));
            }

            List<InterceptorInfo> interceptors = getInterceptorsFromAnnotation(interceptorAnnoList);
            interceptors.addAll(this.globleInterceptors);
            interceptors.addAll(this.classMeta.getInterceptors());
            Collections.sort(interceptors, new Comparator<InterceptorInfo>() {

                @Override
                public int compare(InterceptorInfo o1, InterceptorInfo o2) {
                    return o1.getOrder() - o2.getOrder();
                }
            });

            List<HttpApiInterceptor> result = new ArrayList<>();
            for (InterceptorInfo interceptorInfo : interceptors) {
                Object bean = this.applicationContext.getBean(interceptorInfo.getInterceptorBeanName());
                if (bean == null || !(bean instanceof HttpApiInterceptor)) {
                    throw new RuntimeException(String.format("解析Inteceptor出错，找不到bean或者bean没有实现HttpApiInterceptor接口，beanName",
                            interceptorInfo.getInterceptorBeanName()));
                }
                result.add((HttpApiInterceptor) bean);
            }
            return result;
        }

    }
}
