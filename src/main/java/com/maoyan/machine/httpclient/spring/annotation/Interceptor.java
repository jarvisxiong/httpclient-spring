package com.maoyan.machine.httpclient.spring.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Interceptor {
    Class<?> interceptorClass() default Object.class;

    String beanName() default "";

    int order();
}
