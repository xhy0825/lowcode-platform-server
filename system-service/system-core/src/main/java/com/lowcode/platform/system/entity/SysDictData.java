package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    /** 字典类型 */
    private String dictType;

    /** 字典标签 */
    private String dictLabel;

    /** 字典值 */
    private String dictValue;

    /** 排序 */
    private Integer dictSort;

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认 */
    private Integer isDefault;

    /** 状态 */
    private Integer status;
}