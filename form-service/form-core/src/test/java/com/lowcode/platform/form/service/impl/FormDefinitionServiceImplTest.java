package com.lowcode.platform.form.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.form.entity.FieldConfig;
import com.lowcode.platform.form.entity.FormDefinition;
import com.lowcode.platform.form.mapper.FormDefinitionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 表单定义服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class FormDefinitionServiceImplTest {

    @Mock
    private FormDefinitionMapper formMapper;

    @InjectMocks
    private FormDefinitionServiceImpl formService;

    private FormDefinition testForm;

    @BeforeEach
    void setUp() {
        testForm = new FormDefinition();
        testForm.setId(1L);
        testForm.setFormName("测试表单");
        testForm.setFormCode("test_form");
        testForm.setStatus(0);
        testForm.setVersion(1);
        testForm.setDelFlag(0);
    }

    private FieldConfig createFieldConfig(String code, String name, String type) {
        FieldConfig fc = new FieldConfig();
        fc.setFieldCode(code);
        fc.setFieldName(name);
        fc.setWidgetType(type);
        return fc;
    }

    @Test
    @DisplayName("创建表单-成功")
    void createForm_success() {
        // 编码不存在重复
        when(formMapper.selectByFormCode("test_form")).thenReturn(null);
        when(formMapper.insert(any())).thenReturn(1);

        boolean result = formService.createForm(testForm);

        assertTrue(result);
        assertEquals(0, testForm.getStatus());
        assertEquals(1, testForm.getVersion());
        verify(formMapper).insert(any());
    }

    @Test
    @DisplayName("创建表单-编码重复")
    void createForm_duplicateCode() {
        when(formMapper.selectByFormCode("test_form")).thenReturn(testForm);

        assertThrows(RuntimeException.class, () -> formService.createForm(testForm));
        verify(formMapper, never()).insert(any());
    }

    @Test
    @DisplayName("发布表单-成功")
    void publishForm_success() {
        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formMapper.updateById(any())).thenReturn(1);

        boolean result = formService.publishForm(1L);

        assertTrue(result);
        assertEquals(1, testForm.getStatus());
    }

    @Test
    @DisplayName("发布表单-表单不存在")
    void publishForm_notFound() {
        when(formMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> formService.publishForm(1L));
    }

    @Test
    @DisplayName("获取字段配置-成功")
    void getFieldConfig_success() {
        List<FieldConfig> fields = Arrays.asList(
                createFieldConfig("username", "用户名", "input"),
                createFieldConfig("email", "邮箱", "input")
        );
        testForm.setFieldConfig(JSON.toJSONString(fields));
        when(formMapper.selectById(1L)).thenReturn(testForm);

        List<FieldConfig> result = formService.getFieldConfig(1L);

        assertEquals(2, result.size());
        assertEquals("username", result.get(0).getFieldCode());
    }

    @Test
    @DisplayName("获取字段配置-空配置")
    void getFieldConfig_empty() {
        testForm.setFieldConfig(null);
        when(formMapper.selectById(1L)).thenReturn(testForm);

        List<FieldConfig> result = formService.getFieldConfig(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("更新字段配置-成功")
    void updateFieldConfig_success() {
        List<FieldConfig> fields = Arrays.asList(
                createFieldConfig("name", "姓名", "input")
        );
        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formMapper.updateById(any())).thenReturn(1);

        boolean result = formService.updateFieldConfig(1L, fields);

        assertTrue(result);
        assertNotNull(testForm.getFieldConfig());
    }

    @Test
    @DisplayName("分页查询-按名称过滤")
    void selectPage_withNameFilter() {
        Page<FormDefinition> page = new Page<>(1, 10);
        when(formMapper.selectPage(any(), any())).thenReturn(page);

        FormDefinition query = new FormDefinition();
        query.setFormName("测试");
        Page<FormDefinition> result = formService.selectPage(page, query);

        assertNotNull(result);
        verify(formMapper).selectPage(any(), any());
    }

    @Test
    @DisplayName("删除表单-软删除")
    void deleteForm_softDelete() {
        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formMapper.updateById(any())).thenReturn(1);

        boolean result = formService.deleteForm(1L);

        assertTrue(result);
        assertEquals(1, testForm.getDelFlag());
    }
}