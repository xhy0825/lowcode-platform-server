package com.lowcode.platform.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lowcode.platform.common.core.context.TenantContextHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 字段自动填充处理器
 */
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 租户ID
        this.strictInsertFill(metaObject, "tenantId", String.class, TenantContextHolder.getTenantId());
        // 创建人（从安全上下文获取，暂时使用默认值）
        this.strictInsertFill(metaObject, "createdBy", String.class, "system");
        // 创建时间
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        // 更新人
        this.strictInsertFill(metaObject, "updatedBy", String.class, "system");
        // 更新时间
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        // 删除标记
        this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新人
        this.strictUpdateFill(metaObject, "updatedBy", String.class, "system");
        // 更新时间
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
    }
}