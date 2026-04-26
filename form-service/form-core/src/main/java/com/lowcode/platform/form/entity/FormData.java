package com.lowcode.platform.form.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单数据实例
 * 注意：实际数据存储在动态创建的表中
 */
@Data
@TableName("form_data_template")
public class FormData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据ID */
    private Long id;

    /** 租户ID */
    private String tenantId;

    /** 表单定义ID */
    private Long formDefinitionId;

    /** 数据(JSON) */
    private String data;

    /** 创建人 */
    private String createdBy;

    /** 创建时间 */
    private LocalDateTime createdTime;

    /** 更新人 */
    private String updatedBy;

    /** 更新时间 */
    private LocalDateTime updatedTime;
}