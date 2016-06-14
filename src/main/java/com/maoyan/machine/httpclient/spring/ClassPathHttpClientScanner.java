/*
 *    Copyright 2010-2013 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.maoyan.machine.httpclient.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import com.maoyan.machine.httpclient.spring.converter.RequestEntityConverter;
import com.maoyan.machine.httpclient.spring.converter.ResponseEntityConverter;
import com.maoyan.machine.httpclient.spring.meta.InterceptorInfo;

/**
 * A {@link ClassPathBeanDefinitionScanner} that registers Mappers by
 * {@code basePackage}, {@code annotationClass}, or {@code markerInterface}. If
 * an {@code annotationClass} and/or {@code markerInterface} is specified, only
 * the specified types will be searched (searching for all interfaces will be
 * disabled).
 * <p>
 * This functionality was previously a private class of
 * {@link MapperScannerConfigurer}, but was broken out in version 1.2.0.
 * 
 * @author Hunter Presnall
 * @author Eduardo Macarron
 * 
 * @see HttpClientFactoryBean
 * @since 1.2.0
 * @version $Id$
 */
public class ClassPathHttpClientScanner extends ClassPathBeanDefinitionScanner {
    private Map<String, RequestEntityConverter> requestEntityConverters = new HashMap<>();
    private Map<String, ResponseEntityConverter> responseEntityConverts = new HashMap<>();
    private HttpClient httpClient = null;

    private Class<? extends Annotation> annotationClass;

    private Class<?> markerInterface;

    private List<InterceptorInfo> globleInterceptors;

    private String baseUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setRequestEntityConverters(Map<String, RequestEntityConverter> requestEntityConverters) {
        this.requestEntityConverters = requestEntityConverters;
    }

    public void setResponseEntityConverts(Map<String, ResponseEntityConverter> responseEntityConverts) {
        this.responseEntityConverts = responseEntityConverts;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setGlobleInterceptors(List<InterceptorInfo> globleInterceptors) {
        this.globleInterceptors = globleInterceptors;
    }

    public ClassPathHttpClientScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    /**
     * Configures parent scanner to search for the right interfaces. It can
     * search
     * for all interfaces or just for those that extends a markerInterface
     * or/and
     * those annotated with the annotationClass
     */
    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        // if specified, use the given annotation and / or marker interface
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        // override AssignableTypeFilter to ignore matches on the actual marker
        // interface
        if (this.markerInterface != null) {
            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
                @Override
                protected boolean matchClassName(String className) {
                    return false;
                }
            });
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    return true;
                }
            });
        }

        // exclude package-info.java
        addExcludeFilter(new TypeFilter() {
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            }
        });
    }

    /**
     * Calls the parent search that will search and register all the candidates.
     * Then the registered objects are post processed to set them as
     * MapperFactoryBeans
     */
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No http Api was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            for (BeanDefinitionHolder holder : beanDefinitions) {
                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

                if (logger.isDebugEnabled()) {
                    logger.debug("Creating MapperFactoryBean with name '" + holder.getBeanName() + "' and '" + definition.getBeanClassName()
                            + "' mapperInterface");
                }

                definition.getPropertyValues().add("mapperInterface", definition.getBeanClassName());
                definition.setBeanClass(HttpClientFactoryBean.class);
                definition.getPropertyValues().add("requestEntityConverters", this.requestEntityConverters);
                definition.getPropertyValues().add("responseEntityConverts", this.responseEntityConverts);
                definition.getPropertyValues().add("httpClient", this.httpClient);
                definition.getPropertyValues().add("baseUrl", this.baseUrl);
                definition.getPropertyValues().add("globleInterceptors", this.globleInterceptors);
                definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            }
        }

        return beanDefinitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping MapperFactoryBean with name '" + beanName + "' and '" + beanDefinition.getBeanClassName() + "' mapperInterface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }

}
