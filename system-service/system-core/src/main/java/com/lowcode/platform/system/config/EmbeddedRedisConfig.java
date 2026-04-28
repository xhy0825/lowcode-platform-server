package com.lowcode.platform.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

/**
 * 内嵌Redis配置（开发环境）
 * 仅在dev profile下启用，自动启动本地Redis服务器
 */
@Configuration
@Profile("dev")
public class EmbeddedRedisConfig {

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        // Windows需要设置maxheap避免"insufficient disk space"错误
        redisServer = new RedisServerBuilder()
                .port(redisPort)
                .setting("maxheap 128MB")
                .build();
        redisServer.start();
        System.out.println("========================================");
        System.out.println("Embedded Redis started on port: " + redisPort);
        System.out.println("========================================");
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
            System.out.println("Embedded Redis stopped");
        }
    }
}