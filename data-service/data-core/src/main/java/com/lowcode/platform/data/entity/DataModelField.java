package com.lowcode.platform.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据模型字段实体
 */
@Data
@TableName("data_model_field")
public class DataModelField implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 字段ID */
    private Long id;

    /** 模型ID */
    private Long modelId;

    /** 租户ID */
    private String tenantId;

    /** 字段名称 */
    private String fieldName;

    /** 字段编码 */
    private String fieldCode;

    /** 数据库列名 */
    private String columnName;

    /** 字段类型 */
    private String fieldType;

    /** 长度 */
    private Integer length;

    /** 精度 */
    private Integer precision;

    /** 小数位 */
    private Integer scale;

    /** 是否必填 */
    private Integer isRequired;

    /** 是否唯一 */
    private Integer isUnique;

    /** 是否索引 */
    private Integer isIndexed;

    /** 是否主键 */
    private Integer isPrimary;

    /** 默认值 */
    private String defaultValue;

    /** 关联字典 */
    private String dictType;

    /** 关联类型 */
    private String relationType;

    /** 关联模型ID */
    private Long relationModelId;

    /** 排序 */
    private Integer orderNum;

    /** 删除标记 */
    private Integer delFlag;

    /** 创建人 */
    private String createdBy;

    /** 创建时间 */
    private java.time.LocalDateTime createdTime;
}