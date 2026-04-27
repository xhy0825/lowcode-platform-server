package com.lowcode.platform.form.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.form.entity.FormData;
import com.lowcode.platform.form.entity.FormDefinition;
import com.lowcode.platform.form.mapper.FormDataMapper;
import com.lowcode.platform.form.mapper.FormDefinitionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 表单数据服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class FormDataServiceImplTest {

    @Mock
    private FormDefinitionMapper formMapper;

    @Mock
    private FormDataMapper formDataMapper;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private FormDataServiceImpl dataService;

    private FormDefinition testForm;
    private Map<String, Object> testData;

    @BeforeEach
    void setUp() {
        testForm = new FormDefinition();
        testForm.setId(1L);
        testForm.setFormName("测试表单");
        testForm.setFormCode("test_form");
        testForm.setStatus(1); // 已发布
        testForm.setTenantId("000000");
        testForm.setModelId(null); // 无关联模型

        testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("email", "test@example.com");
    }

    @Test
    @DisplayName("提交表单数据-成功(无模型)")
    void submitFormData_noModel_success() {
        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formDataMapper.insert(any())).thenReturn(1);

        Long result = dataService.submitFormData(1L, testData);

        assertNotNull(result);
        verify(formDataMapper).insert(any());
    }

    @Test
    @DisplayName("提交表单数据-表单不存在")
    void submitFormData_formNotFound() {
        when(formMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> dataService.submitFormData(1L, testData));
    }

    @Test
    @DisplayName("提交表单数据-表单未发布")
    void submitFormData_formNotPublished() {
        testForm.setStatus(0);
        when(formMapper.selectById(1L)).thenReturn(testForm);

        assertThrows(RuntimeException.class, () -> dataService.submitFormData(1L, testData));
    }

    @Test
    @DisplayName("更新表单数据-成功")
    void updateFormData_success() {
        FormData formData = new FormData();
        formData.setId(1L);
        formData.setData(JSON.toJSONString(testData));

        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formDataMapper.selectById(1L)).thenReturn(formData);
        when(formDataMapper.updateById(any())).thenReturn(1);

        boolean result = dataService.updateFormData(1L, 1L, testData);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除表单数据-成功")
    void deleteFormData_success() {
        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formDataMapper.deleteById(1L)).thenReturn(1);

        boolean result = dataService.deleteFormData(1L, 1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("查询单条数据-成功")
    void getFormData_success() {
        FormData formData = new FormData();
        formData.setId(1L);
        formData.setData(JSON.toJSONString(testData));

        when(formDataMapper.selectById(1L)).thenReturn(formData);

        FormData result = dataService.getFormData(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("分页查询数据-无模型")
    void queryFormDataPage_noModel() {
        Page<FormData> mockPage = new Page<>(1, 10);
        mockPage.setTotal(2);
        FormData fd1 = new FormData();
        fd1.setId(1L);
        fd1.setData(JSON.toJSONString(testData));
        fd1.setCreatedTime(java.time.LocalDateTime.now());
        mockPage.setRecords(List.of(fd1));

        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formDataMapper.selectPage(any(), any())).thenReturn(mockPage);

        Page<Map<String, Object>> result = dataService.queryFormDataPage(1L, null, 1, 10);

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("查询数据列表-无模型")
    void queryFormDataList_noModel() {
        FormData fd1 = new FormData();
        fd1.setId(1L);
        fd1.setData(JSON.toJSONString(testData));

        when(formMapper.selectById(1L)).thenReturn(testForm);
        when(formDataMapper.selectByFormDefinitionId(1L, 0, 1000))
                .thenReturn(List.of(fd1));

        List<Map<String, Object>> result = dataService.queryFormDataList(1L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).containsKey("name"));
    }
}