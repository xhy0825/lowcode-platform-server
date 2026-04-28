package com.lowcode.platform.file.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO服务地址
     */
    private String endpoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 私密密钥
     */
    private String secretKey;

    /**
     * 默认存储桶
     */
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        // endpoint URL决定是否使用HTTPS（http://开头则为非安全连接）
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}