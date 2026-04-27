package com.lowcode.platform.form.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.form.entity.FormDefinition;
import com.lowcode.platform.form.entity.FieldConfig;

import java.util.List;

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