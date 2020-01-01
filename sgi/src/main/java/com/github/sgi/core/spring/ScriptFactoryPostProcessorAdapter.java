package com.github.sgi.core.spring;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.groovy.GroovyObjectCustomizer;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @FileName: ScriptFactoryPostProcessorAdapter.java
 * @Description: ScriptFactoryPostProcessorAdapter.java类说明
 * @Author: timestatic
 * @Date: 2018/9/30 9:38
 */
public class ScriptFactoryPostProcessorAdapter extends ScriptFactoryPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(ScriptFactoryPostProcessorAdapter.class);
    private static final String SCRIPT_FACTORY_NAME_PREFIX = "scriptFactory.";
    private static final String SCRIPTED_OBJECT_NAME_PREFIX = "scriptedObject.";
    private static DefaultListableBeanFactory scriptBeanFactory;
    /**
     * Map from bean name String to ScriptSource object
     */
    private static Map<String, ScriptSource> scriptSourceCache;

    private DefaultListableBeanFactory beanFactory;

    public ScriptFactoryPostProcessorAdapter(ApplicationContext applicationContext) {
        beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.setAllowBeanDefinitionOverriding(true);
        init(this);
    }

    private void init(ScriptFactoryPostProcessor scriptFactoryPostProcessor) {
        Class clasz = this.getClass().getSuperclass();
        Field factoryField = null;
        Field cacheField = null;
        try {
            factoryField = clasz.getDeclaredField("scriptBeanFactory");
            factoryField.setAccessible(true);
            cacheField = clasz.getDeclaredField("scriptSourceCache");
            cacheField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            log.warn("spring scriptFactoryPostProcessor getField error", e);
        } catch (Exception e) {
            log.error("get field from scriptFactoryPostProcessor error", e);
        }
        try {
            scriptBeanFactory = (DefaultListableBeanFactory) factoryField.get(scriptFactoryPostProcessor);
            scriptSourceCache = (Map<String, ScriptSource>) cacheField.get(scriptFactoryPostProcessor);
        } catch (Exception e) {
            log.error("get field reference from scriptFactoryPostProcessor error", e);
        }
    }

    /**
     * 销毁script bean
     * @param beanName
     */
    public void destroySingleBean(String beanName) {
        removeBeanDefinition(beanFactory, beanName);
        removeBeanDefinition(scriptBeanFactory, SCRIPTED_OBJECT_NAME_PREFIX + beanName);
        removeBeanDefinition(scriptBeanFactory, SCRIPT_FACTORY_NAME_PREFIX + beanName);
        scriptSourceCache.remove(SCRIPT_FACTORY_NAME_PREFIX + beanName);
        log.info("destroy groovy script bean {}", beanName);
    }

    private void removeBeanDefinition(DefaultListableBeanFactory beanFactory, String beanName) {
        try {
            beanFactory.destroySingleton(beanName);
            if (beanFactory.containsBeanDefinition(beanName)) {
                beanFactory.removeBeanDefinition(beanName);
            }
        } catch (NoSuchBeanDefinitionException e) {
        }
    }

    /**
     * 创建 script bean
     * @param beanName
     * @param script
     * @throws IOException
     */
    public void registerBean(String beanName, String script) {
        ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, INLINE_SCRIPT_PREFIX + script);
        registerBean(beanName, argumentValues);
    }

    public void registerBean(String beanName, String script, CompilerConfiguration compilerConfiguration) {
        ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, INLINE_SCRIPT_PREFIX + script);
        argumentValues.addIndexedArgumentValue(1, compilerConfiguration);
        registerBean(beanName, argumentValues);
    }

    public void registerBean(String beanName, String script, GroovyObjectCustomizer groovyObjectCustomizer) {
        ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
        argumentValues.addIndexedArgumentValue(0, INLINE_SCRIPT_PREFIX + script);
        argumentValues.addIndexedArgumentValue(1, groovyObjectCustomizer);
        registerBean(beanName, argumentValues);
    }

    public void registerBean(String beanName, ConstructorArgumentValues argumentValues) {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(GroovyScriptFactory.class);
        bd.setAttribute(ScriptFactoryPostProcessor.LANGUAGE_ATTRIBUTE, "groovy");
        bd.setConstructorArgumentValues(argumentValues);

        registerBean(beanName, bd);
    }

    public void registerBean(String beanName, GenericBeanDefinition beanDefinition) {
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
        log.info("register groovy script bean {}", beanName);
    }
}
