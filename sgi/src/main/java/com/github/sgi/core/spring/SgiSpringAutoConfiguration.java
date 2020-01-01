package com.github.sgi.core.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @FileName: SgiSpringAutoConfiguration.java
 * @Description: SgiSpringAutoConfiguration.java类说明
 * @Author: timestatic
 * @Date: 2020/1/1 17:50
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.", name = "sgi", matchIfMissing = true, havingValue = "true")
public class SgiSpringAutoConfiguration {

    @Bean
    public ScriptFactoryPostProcessorAdapter scriptFactoryPostProcessorAdapter(@Autowired ApplicationContext applicationContext) {
        return new ScriptFactoryPostProcessorAdapter(applicationContext);
    }

}
