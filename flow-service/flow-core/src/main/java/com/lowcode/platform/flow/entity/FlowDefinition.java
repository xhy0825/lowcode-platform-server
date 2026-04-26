package com.lowcode.platform.flow.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程定义
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_definition")
public class FlowDefinition extends BaseEntity {

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 关联表单ID
     */
    private Long formId;

    /**
     * 节点配置(JSON)
     */
    private String nodes;

    /**
     * 流转配置(JSON)
     */
    private String edges;

    /**
     * 状态：0-草稿，1-已发布
     */
    private Integer status;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 流程描述
     */
    private String description;
}