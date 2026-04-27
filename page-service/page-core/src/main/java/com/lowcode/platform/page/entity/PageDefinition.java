package com.lowcode.platform.page.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 页面定义实体
 */
@Data
@TableName("page_definition")
public class PageDefinition {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面编码
     */
    private String pageCode;

    /**
     * 页面类型：list/form/detail/dashboard
     */
    private String pageType;

    /**
     * 关联表单ID
     */
    private Long formId;

    /**
     * 关联数据模型ID
     */
    private Long modelId;

    /**
     * 页面布局配置(JSON)
     */
    private String layoutConfig;

    /**
     * 组件配置(JSON)
     */
    private String componentConfig;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}