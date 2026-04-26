package com.lowcode.platform.common.groovy.config;

import com.lowcode.platform.common.groovy.api.ServiceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Groovy配置类
 */
@Configuration
public class GroovyConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ServiceLocator.getInstance().setApplicationContext(applicationContext);
    }
}