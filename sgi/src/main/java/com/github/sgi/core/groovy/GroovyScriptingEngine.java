package com.github.sgi.core.groovy;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.jsr223.GroovyCompiledScript;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import java.lang.reflect.Field;
import java.util.Map;

import static javax.script.ScriptContext.ENGINE_SCOPE;
import static javax.script.ScriptContext.GLOBAL_SCOPE;

/**
 * @FileName: GroovyScriptingEngine.java
 * @Description: GroovyScriptingEngine.java类说明
 * @Author: timestatic
 * @Date: 2019/12/22 16:49
 */
public class GroovyScriptingEngine {

    private final GroovyScriptEngineImpl groovyEngine;
    private final GroovyScriptingEngineBuilder builder;

    public GroovyScriptingEngine(GroovyScriptingEngineBuilder builder) {
        this.builder = builder;
        this.groovyEngine = (GroovyScriptEngineImpl) new ScriptEngineManager().getEngineByName("groovy");
        groovyEngine.setClassLoader(new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), builder.getCompilerConfiguration()));
    }


    public CompiledScript compiledScript(String script) throws ScriptException {
        return groovyEngine.compile(script);
    }


    /**
     * 返回脚本compile后的class , {@link groovy.lang.Script} 或者脚本中定义的class
     *
     * @param script
     * @return
     * @throws ScriptException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public Class compiledScriptGetClass(String script) throws NoSuchFieldException, ScriptException, IllegalAccessException {
        GroovyCompiledScript compiledScript = (GroovyCompiledScript) groovyEngine.compile(script);
        Field field = compiledScript.getClass().getDeclaredField("clasz");
        field.setAccessible(true);
        return (Class) field.get(compiledScript);
    }

    /**
     * 传入参数并执行脚本
     *
     * @param script
     * @param argsMap
     * @return
     * @throws ScriptException
     */
    public Object evalScript(String script, Map<String, Object> argsMap) throws ScriptException {
        Bindings binds = new SimpleBindings();
        binds.putAll(argsMap);
        ScriptContext scriptContext = createScriptContext(binds);
        return compiledScript(script).eval(scriptContext);
    }

    public Object evalScript(String script) throws ScriptException {
        ScriptContext scriptContext = createScriptContext(new SimpleBindings());
        return compiledScript(script).eval(scriptContext);
    }


    public ScriptContext createScriptContext(Bindings binds) {
        ScriptContext defaultEngineCtx = groovyEngine.getContext();

        ScriptContext customScriptCtx = new SimpleScriptContext();
        customScriptCtx.setBindings(binds, ENGINE_SCOPE);
        customScriptCtx.setBindings(defaultEngineCtx.getBindings(GLOBAL_SCOPE), GLOBAL_SCOPE);
        customScriptCtx.setWriter(defaultEngineCtx.getWriter());
        customScriptCtx.setReader(defaultEngineCtx.getReader());
        customScriptCtx.setErrorWriter(defaultEngineCtx.getErrorWriter());
        return customScriptCtx;
    }

    public GroovyScriptEngineImpl getGroovyEngine() {
        return groovyEngine;
    }

    public CompilerConfiguration getCompilerConfiguration() {
        return builder.getCompilerConfiguration();
    }


}
