package com.lowcode.platform.flow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 流程服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.lowcode.platform.flow.mapper")
public class FlowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowServiceApplication.class, args);
    }
}