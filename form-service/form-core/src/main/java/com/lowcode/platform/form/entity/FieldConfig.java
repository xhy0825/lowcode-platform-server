package com.lowcode.platform.form.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 表单字段配置
 */
@Data
public class FieldConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 字段编码 */
    private String fieldCode;

    /** 字段名称 */
    private String fieldName;

    /** 组件类型 */
    private String widgetType;

    /** 占位符 */
    private String placeholder;

    /** 默认值 */
    private String defaultValue;

    /** 数据字典类型 */
    private String dictType;

    /** 是否必填 */
    private Integer isRequired;

    /** 是否只读 */
    private Integer isReadonly;

    /** 是否隐藏 */
    private Integer isHidden;

    /** 列宽度 */
    private Integer colSpan;

    /** 行顺序 */
    private Integer rowOrder;

    /** 校验类型 */
    private String validateType;

    /** 自定义正则 */
    private String validateRegex;

    /** 校验失败提示 */
    private String validateMessage;

    /** 最小长度 */
    private Integer minLength;

    /** 最大长度 */
    private Integer maxLength;

    /** 最小值 */
    private Integer minValue;

    /** 最大值 */
    private Integer maxValue;

    /** 扩展配置 */
    private Object extraConfig;
}