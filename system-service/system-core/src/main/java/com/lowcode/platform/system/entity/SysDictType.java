package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    /** 字典类型 */
    private String dictType;

    /** 字典名称 */
    private String dictName;

    /** 状态 */
    private Integer status;
}