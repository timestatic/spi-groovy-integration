package com.github.sgi.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @FileName: SpringContextUtils.java
 * @Description: SpringContextUtils.java类说明
 * @Author: timestatic
 * @Date: 2018/5/15 14:29
 */
@Component
@Lazy(false)
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static<T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public boolean containsBeanDefinition(String beanName) {
        return applicationContext.containsBeanDefinition(beanName);
    }

    public boolean containsBean(String beanName) {
        return applicationContext.containsBean(beanName);
    }

}
