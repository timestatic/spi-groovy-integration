package com.github.sgi.core;

import com.github.sgi.core.anno.GImpl;
import com.github.sgi.core.spring.ScriptFactoryPostProcessorAdapter;
import com.github.sgi.core.spring.SpringContextUtils;

import javax.script.ScriptException;

/**
 * @FileName: ExtensionSpringLoader.java
 * @Description: ExtensionSpringLoader.java类说明
 * @Author: timestatic
 * @Date: 2020/1/1 15:51
 */
public class ExtensionSpringLoader<T> extends ExtensionLoader {

    private ScriptFactoryPostProcessorAdapter scriptFactoryPostProcessorAdapter = SpringContextUtils.getBean(ScriptFactoryPostProcessorAdapter.class);

    private ExtensionSpringLoader(Class type) {
        super(type);
    }

    public static <T> ExtensionSpringLoader<T> getExtensionSpringLoader(Class<T> type) {
        sgiInterfaceCheck(type);

        ExtensionSpringLoader<T> loader = (ExtensionSpringLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionSpringLoader<T>(type));
            loader = (ExtensionSpringLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    public void addExtensionSpringContext(String script) throws IllegalAccessException, NoSuchFieldException, ScriptException, InstantiationException {
        Class clazz = groovyScriptingEngine.compiledScriptGetClass(script);
        if (!super.type.isAssignableFrom(clazz)) {
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

        GImpl gimpl = (GImpl) clazz.getAnnotation(GImpl.class);
        if (gimpl.isSpringBean()) {
            scriptFactoryPostProcessorAdapter.registerBean(!"".equals(gimpl.value()) ? gimpl.value() : clazz.getSimpleName(),
                    script,
                    groovyScriptingEngine.getCompilerConfiguration());
        }

        super.addExtension(clazz);
    }

}
