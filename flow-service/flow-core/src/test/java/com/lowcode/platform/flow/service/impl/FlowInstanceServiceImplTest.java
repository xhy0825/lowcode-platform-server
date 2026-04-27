package com.lowcode.platform.flow.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowcode.platform.flow.entity.FlowDefinition;
import com.lowcode.platform.flow.entity.FlowInstance;
import com.lowcode.platform.flow.entity.FlowTask;
import com.lowcode.platform.flow.mapper.FlowDefinitionMapper;
import com.lowcode.platform.flow.mapper.FlowInstanceMapper;
import com.lowcode.platform.flow.mapper.FlowTaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 流程实例服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class FlowInstanceServiceImplTest {

    @Mock
    private FlowInstanceMapper flowInstanceMapper;

    @Mock
    private FlowDefinitionMapper flowDefinitionMapper;

    @Mock
    private FlowTaskMapper flowTaskMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private FlowInstanceServiceImpl flowInstanceService;

    private FlowDefinition testDefinition;
    private FlowInstance testInstance;

    @BeforeEach
    void setUp() {
        testDefinition = new FlowDefinition();
        testDefinition.setId(1L);
        testDefinition.setFlowName("请假审批流程");
        testDefinition.setFlowCode("leave_approval");
        testDefinition.setStatus(1);
        testDefinition.setNodes("[{\"nodeId\":\"start\",\"nodeType\":\"start\"},{\"nodeId\":\"task1\",\"nodeType\":\"user_task\",\"nodeName\":\"部门经理审批\"}]");
        testDefinition.setEdges("[{\"source\":\"start\",\"target\":\"task1\"}]");

        testInstance = new FlowInstance();
        testInstance.setId(1L);
        testInstance.setFlowDefinitionId(1L);
        testInstance.setFlowName("请假审批流程");
        testInstance.setStatus("running");
    }

    @Test
    @DisplayName("分页查询流程实例")
    void listPage_success() {
        Page<FlowInstance> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testInstance));

        when(flowInstanceMapper.selectPage(any(), any())).thenReturn(mockPage);

        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", 1);
        params.put("pageSize", 10);

        Page<FlowInstance> result = flowInstanceService.listPage(params);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("获取流程实例详情")
    void getDetail_success() {
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(flowTaskMapper.selectList(any())).thenReturn(List.of());

        Map<String, Object> result = flowInstanceService.getDetail(1L);

        assertNotNull(result);
        assertTrue(result.containsKey("instance"));
        assertTrue(result.containsKey("nodes"));
    }

    @Test
    @DisplayName("获取详情-实例不存在")
    void getDetail_notFound() {
        when(flowInstanceMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> flowInstanceService.getDetail(1L));
    }

    @Test
    @DisplayName("取消流程实例-成功")
    void cancel_success() {
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(flowInstanceMapper.updateById(any())).thenReturn(1);
        when(flowTaskMapper.selectList(any())).thenReturn(List.of());

        flowInstanceService.cancel(1L);

        assertEquals("cancelled", testInstance.getStatus());
        verify(flowInstanceMapper).updateById(any());
    }

    @Test
    @DisplayName("取消流程-实例不存在")
    void cancel_notFound() {
        when(flowInstanceMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> flowInstanceService.cancel(1L));
    }

    @Test
    @DisplayName("取消流程-状态不正确")
    void cancel_wrongStatus() {
        testInstance.setStatus("completed");
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);

        assertThrows(RuntimeException.class, () -> flowInstanceService.cancel(1L));
    }

    @Test
    @DisplayName("获取表单数据")
    void getFormData_success() {
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);

        Map<String, Object> result = flowInstanceService.getFormData(1L);

        assertNotNull(result);
        assertEquals(1L, result.get("instanceId"));
    }
}