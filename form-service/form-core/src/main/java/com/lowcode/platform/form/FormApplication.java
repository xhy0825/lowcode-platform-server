package com.lowcode.platform.form;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Form 表单服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.lowcode.platform")
@ComponentScan(basePackages = "com.lowcode.platform")
public class FormApplication {

    public static void main(String[] args) {
        SpringApplication.run(FormApplication.class, args);
        System.out.println("========================================");
        System.out.println("Form 服务启动成功");
        System.out.println("========================================");
    }
}