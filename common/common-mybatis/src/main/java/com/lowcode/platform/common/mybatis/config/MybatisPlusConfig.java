package com.lowcode.platform.common.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor.TenantLineHandler;
import com.lowcode.platform.common.core.context.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * MyBatis Plus 配置
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 不需要租户过滤的表（系统公共表）
     */
    private static final List<String> IGNORE_TABLES = Arrays.asList(
            "sys_tenant",
            "sys_package",
            "sys_command",
            "sys_command_log"
    );

    /**
     * MyBatis Plus 插件配置
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                String tenantId = TenantContextHolder.getTenantId();
                return new StringValue(tenantId);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 系统租户时忽略所有表的多租户过滤
                if (TenantContextHolder.isSystemTenant()) {
                    return true;
                }
                // 系统公共表忽略多租户过滤
                return IGNORE_TABLES.contains(tableName.toLowerCase());
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }
        });
        interceptor.addInnerInterceptor(tenantInterceptor);

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInterceptor);

        return interceptor;
    }

    /**
     * 全局配置
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setDbConfig(new GlobalConfig.DbConfig()
                .setLogicDeleteField("delFlag")
                .setLogicDeleteValue("1")
                .setLogicNotDeleteValue("0"));
        return globalConfig;
    }
}