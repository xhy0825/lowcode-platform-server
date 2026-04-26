package com.lowcode.platform.common.groovy.api;

/**
 * 服务定位器 - 用于Groovy脚本中获取服务
 */
public class ServiceLocator {

    private static ServiceLocator instance;

    private org.springframework.context.ApplicationContext applicationContext;

    public static ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    public void setApplicationContext(org.springframework.context.ApplicationContext ctx) {
        this.applicationContext = ctx;
    }

    /**
     * 根据名称获取Bean
     */
    public Object getBean(String name) {
        if (applicationContext == null) {
            throw new RuntimeException("ApplicationContext未初始化");
        }
        return applicationContext.getBean(name);
    }

    /**
     * 根据类型获取Bean
     */
    public <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            throw new RuntimeException("ApplicationContext未初始化");
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称和类型获取Bean
     */
    public <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            throw new RuntimeException("ApplicationContext未初始化");
        }
        return applicationContext.getBean(name, clazz);
    }
}