package com.lowcode.platform.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 数据模型实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_model")
public class DataModel extends BaseEntity {

    /** 模型名称 */
    private String modelName;

    /** 模型编码 */
    private String modelCode;

    /** 物理表名 */
    private String tableName;

    /** 描述 */
    private String description;

    /** 状态 0-草稿 1-已发布 */
    private Integer status;

    /** 版本号 */
    private Integer version;

    /** 字段列表（非数据库字段） */
    private List<DataModelField> fields;
}