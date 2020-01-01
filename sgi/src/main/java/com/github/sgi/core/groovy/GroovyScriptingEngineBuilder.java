package com.github.sgi.core.groovy;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.util.function.Consumer;

/**
 * @FileName: GroovyScriptingEngineBuilder.java
 * @Description: GroovyScriptingEngineBuilder.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 16:51
 */
public class GroovyScriptingEngineBuilder {

    /**
     * default value
     */
    private CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

    private GroovyScriptingEngineBuilder() {
    }

    public static GroovyScriptingEngineBuilder newBuilder() {
        return new GroovyScriptingEngineBuilder();
    }


    public GroovyScriptingEngineBuilder addImportCustomizer(ImportCustomizer importCustomizer) {
        this.compilerConfiguration.addCompilationCustomizers(importCustomizer);
        return this;
    }

    public GroovyScriptingEngineBuilder addCompilerConfiguration(Consumer<CompilerConfiguration> configurationConsumer) {
        configurationConsumer.accept(compilerConfiguration);
        return this;
    }

    public GroovyScriptingEngine build() {
        return new GroovyScriptingEngine(this);
    }


    public CompilerConfiguration getCompilerConfiguration() {
        return compilerConfiguration;
    }

}
