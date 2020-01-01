package com.github.sgi.core;

import com.github.sgi.core.anno.GImpl;
import com.github.sgi.core.anno.SGI;
import com.github.sgi.core.groovy.GroovyScriptingEngine;
import com.github.sgi.core.groovy.GroovyScriptingEngineBuilder;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import javax.script.ScriptException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @FileName: ExtensionLoader.java
 * @Description: ExtensionLoader.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 17:59
 */
public class ExtensionLoader<T> {

    private static ImportCustomizer importCustomizer = new ImportCustomizer()
            .addImports(GImpl.class.getName())
            ;
    protected static final GroovyScriptingEngine groovyScriptingEngine = GroovyScriptingEngineBuilder.newBuilder()
            .addCompilerConfiguration(compilerConfiguration -> compilerConfiguration.addCompilationCustomizers(importCustomizer))
            .build();

    /**
     * 缓存interface 和 对应的ExtensionLoader
     */
    protected static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /**
     * 缓存name 和 class
     */
    protected final ConcurrentMap<String, Class<?>> cachedClasses = new ConcurrentHashMap<>();

    /**
     * 缓存 class 和 name的映射
     */
    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<>();

    /**
     * 缓存name, 与 instance 的对应
     */
    private final ConcurrentMap<String, T> cachedInstances = new ConcurrentHashMap<>();

    protected final Class<?> type;

    protected ExtensionLoader(Class<?> type) {
        this.type = type;
    }


    protected static void sgiInterfaceCheck(Class type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }
        if (!withExtensionAnnotation(type)) {
            throw new IllegalArgumentException("Extension type (" + type +
                    ") is not an extension, because it is NOT annotated with @" + SGI.class.getSimpleName() + "!");
        }
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        sgiInterfaceCheck(type);

        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    private static <T> boolean withExtensionAnnotation(Class<T> type) {
        return type.isAnnotationPresent(SGI.class);
    }

    public boolean hasExtension(String name) {
        return this.getExtensionClass(name) != null;
    }

    private Class<?> getExtensionClass(String name) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Extension name == null");
        }
        return cachedClasses.get(name);
    }

    public T getExtension(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Extension name == null");
        }

        return cachedInstances.get(name);
    }

    public void addExtension(String script) throws ScriptException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        Class clazz = groovyScriptingEngine.compiledScriptGetClass(script);
        addExtension(clazz);
    }

    public void addExtension(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Input type " +
                    clazz + " doesn't implement the Extension " + type);
        }
        if (clazz.isInterface()) {
            throw new IllegalStateException("Input type " +
                    clazz + " can't be interface!");
        }

        if (!clazz.isAnnotationPresent(GImpl.class)) {
            throw new IllegalStateException("Input type " +
                    clazz + "it is NOT annotated with @" + GImpl.class.getSimpleName() + "!");
        }

        GImpl gimpl = clazz.getAnnotation(GImpl.class);
        String name = !"".equals(gimpl.value()) ? gimpl.value() : clazz.getSimpleName();
        boolean override = gimpl.override();

        if (cachedClasses.containsKey(name)) {
            if (override) {
                putExtension(name, clazz);
            }
            return;
        }
        putExtension(name, clazz);
    }

    protected void putExtension(String name, Class<?> clazz) throws IllegalAccessException, InstantiationException {
        cachedInstances.put(name, (T) clazz.newInstance());
        cachedNames.put(clazz, name);
        cachedClasses.put(name, clazz);
    }

    public String getExtensionName(Class<?> extensionClass) {
        return cachedNames.get(extensionClass);
    }

}
