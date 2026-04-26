package com.lowcode.platform.form.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.form.entity.FormDefinition;
import com.lowcode.platform.form.entity.FormData;

import java.util.List;
import java.util.Map;

/**
 * 表单定义服务接口
 */
public interface FormDefinitionService extends IService<FormDefinition> {

    /** 分页查询 */
    Page<FormDefinition> selectPage(Page<FormDefinition> page, FormDefinition query);

    /** 根据编码查询 */
    FormDefinition selectByFormCode(String formCode);

    /** 创建表单 */
    boolean createForm(FormDefinition form);

    /** 更新表单 */
    boolean updateForm(FormDefinition form);

    /** 发布表单 */
    boolean publishForm(Long formId);

    /** 删除表单 */
    boolean deleteForm(Long formId);

    /** 获取表单字段配置 */
    List<FieldConfig> getFieldConfig(Long formId);

    /** 更新字段配置 */
    boolean updateFieldConfig(Long formId, List<FieldConfig> fields);

    /** 更新布局配置 */
    boolean updateLayoutConfig(Long formId, String layoutConfig);
}

/**
 * 字段配置
 */
public class FieldConfig {
    private String fieldCode;
    private String fieldName;
    private String widgetType;
    private String placeholder;
    private String defaultValue;
    private String dictType;
    private Integer isRequired;
    private Integer isReadonly;
    private Integer isHidden;
    private Integer colSpan;
    private Integer rowOrder;
    private String validateRegex;
    private Map<String, Object> extraConfig;

    // getters and setters
    public String getFieldCode() { return fieldCode; }
    public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getWidgetType() { return widgetType; }
    public void setWidgetType(String widgetType) { this.widgetType = widgetType; }
    public String getPlaceholder() { return placeholder; }
    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    public String getDictType() { return dictType; }
    public void setDictType(String dictType) { this.dictType = dictType; }
    public Integer getIsRequired() { return isRequired; }
    public void setIsRequired(Integer isRequired) { this.isRequired = isRequired; }
    public Integer getIsReadonly() { return isReadonly; }
    public void setIsReadonly(Integer isReadonly) { this.isReadonly = isReadonly; }
    public Integer getIsHidden() { return isHidden; }
    public void setIsHidden(Integer isHidden) { this.isHidden = isHidden; }
    public Integer getColSpan() { return colSpan; }
    public void setColSpan(Integer colSpan) { this.colSpan = colSpan; }
    public Integer getRowOrder() { return rowOrder; }
    public void setRowOrder(Integer rowOrder) { this.rowOrder = rowOrder; }
    public String getValidateRegex() { return validateRegex; }
    public void setValidateRegex(String validateRegex) { this.validateRegex = validateRegex; }
    public Map<String, Object> getExtraConfig() { return extraConfig; }
    public void setExtraConfig(Map<String, Object> extraConfig) { this.extraConfig = extraConfig; }
}