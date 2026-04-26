package com.lowcode.platform.form.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.form.entity.FormDefinition;
import com.lowcode.platform.form.mapper.FormDefinitionMapper;
import com.lowcode.platform.form.service.FormDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 表单定义服务实现
 */
@Service
@RequiredArgsConstructor
public class FormDefinitionServiceImpl extends ServiceImpl<FormDefinitionMapper, FormDefinition> implements FormDefinitionService {

    private final FormDefinitionMapper formMapper;

    @Override
    public Page<FormDefinition> selectPage(Page<FormDefinition> page, FormDefinition query) {
        LambdaQueryWrapper<FormDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormDefinition::getDelFlag, 0);
        if (StringUtils.hasText(query.getFormName())) {
            wrapper.like(FormDefinition::getFormName, query.getFormName());
        }
        if (StringUtils.hasText(query.getFormCode())) {
            wrapper.like(FormDefinition::getFormCode, query.getFormCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(FormDefinition::getStatus, query.getStatus());
        }
        if (query.getModelId() != null) {
            wrapper.eq(FormDefinition::getModelId, query.getModelId());
        }
        wrapper.orderByDesc(FormDefinition::getCreatedTime);
        return formMapper.selectPage(page, wrapper);
    }

    @Override
    public FormDefinition selectByFormCode(String formCode) {
        return formMapper.selectByFormCode(formCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createForm(FormDefinition form) {
        // 检查编码是否重复
        FormDefinition exist = formMapper.selectByFormCode(form.getFormCode());
        if (exist != null) {
            throw new BusinessException("表单编码已存在");
        }
        form.setStatus(0);
        form.setVersion(1);
        form.setDelFlag(0);
        return formMapper.insert(form) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateForm(FormDefinition form) {
        FormDefinition exist = formMapper.selectById(form.getId());
        if (exist == null) {
            throw new BusinessException("表单不存在");
        }
        return formMapper.updateById(form) > 0;
    }

    @Override
    public boolean publishForm(Long formId) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }
        form.setStatus(1);
        return formMapper.updateById(form) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteForm(Long formId) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }
        form.setDelFlag(1);
        return formMapper.updateById(form) > 0;
    }

    @Override
    public List<FieldConfig> getFieldConfig(Long formId) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null || !StringUtils.hasText(form.getFieldConfig())) {
            return java.util.Collections.emptyList();
        }
        return JSON.parseArray(form.getFieldConfig(), FieldConfig.class);
    }

    @Override
    public boolean updateFieldConfig(Long formId, List<FieldConfig> fields) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }
        form.setFieldConfig(JSON.toJSONString(fields));
        return formMapper.updateById(form) > 0;
    }

    @Override
    public boolean updateLayoutConfig(Long formId, String layoutConfig) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }
        form.setLayoutConfig(layoutConfig);
        return formMapper.updateById(form) > 0;
    }
}