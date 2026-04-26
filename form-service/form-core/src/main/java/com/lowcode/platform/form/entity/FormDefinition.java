package com.lowcode.platform.form.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单定义实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("form_definition")
public class FormDefinition extends BaseEntity {

    /** 表单名称 */
    private String formName;

    /** 表单编码 */
    private String formCode;

    /** 关联数据模型ID */
    private Long modelId;

    /** 字段配置(JSON) */
    private String fieldConfig;

    /** 布局配置(JSON) */
    private String layoutConfig;

    /** 校验规则(JSON) */
    private String validateRules;

    /** 状态 0-草稿 1-已发布 */
    private Integer status;

    /** 版本号 */
    private Integer version;
}