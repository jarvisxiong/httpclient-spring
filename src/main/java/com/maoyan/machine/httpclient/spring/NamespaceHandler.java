package com.maoyan.machine.httpclient.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NamespaceHandler extends NamespaceHandlerSupport {

    /**
     * {@inheritDoc}
     */
    public void init() {
        registerBeanDefinitionParser("http-api-scan", new ScannerBeanDefinitionParser());
    }

}