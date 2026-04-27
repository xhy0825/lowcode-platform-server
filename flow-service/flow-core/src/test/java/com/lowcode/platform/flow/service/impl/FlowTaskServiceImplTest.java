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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 流程任务服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class FlowTaskServiceImplTest {

    @Mock
    private FlowTaskMapper flowTaskMapper;

    @Mock
    private FlowInstanceMapper flowInstanceMapper;

    @Mock
    private FlowDefinitionMapper flowDefinitionMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private FlowTaskServiceImpl flowTaskService;

    private FlowTask testTask;
    private FlowInstance testInstance;
    private FlowDefinition testDefinition;

    @BeforeEach
    void setUp() {
        testTask = new FlowTask();
        testTask.setId(1L);
        testTask.setInstanceId(1L);
        testTask.setNodeId("task1");
        testTask.setNodeName("部门经理审批");
        testTask.setStatus("pending");

        testInstance = new FlowInstance();
        testInstance.setId(1L);
        testInstance.setFlowDefinitionId(1L);
        testInstance.setStatus("running");

        testDefinition = new FlowDefinition();
        testDefinition.setId(1L);
        testDefinition.setNodes("[{\"nodeId\":\"start\",\"nodeType\":\"start\"},{\"nodeId\":\"task1\",\"nodeType\":\"user_task\",\"nodeName\":\"部门经理审批\"},{\"nodeId\":\"end\",\"nodeType\":\"end\"}]");
        testDefinition.setEdges("[{\"source\":\"start\",\"target\":\"task1\"},{\"source\":\"task1\",\"target\":\"end\"}]");
    }

    @Test
    @DisplayName("分页查询待办任务")
    void listPage_pending() {
        Page<FlowTask> mockPage = new Page<>(1, 10);
        mockPage.setRecords(List.of(testTask));

        when(flowTaskMapper.selectPage(any(), any())).thenReturn(mockPage);

        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", 1);
        params.put("pageSize", 10);
        params.put("taskType", "pending");

        Page<FlowTask> result = flowTaskService.listPage(params);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("审批通过任务")
    void handle_approve() {
        when(flowTaskMapper.selectById(1L)).thenReturn(testTask);
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(flowDefinitionMapper.selectById(1L)).thenReturn(testDefinition);
        when(flowTaskMapper.updateById(any())).thenReturn(1);
        when(flowInstanceMapper.updateById(any())).thenReturn(1);

        flowTaskService.handle(1L, "approve", "同意", null);

        assertEquals("approved", testTask.getStatus());
        assertEquals("approve", testTask.getAction());
        verify(flowTaskMapper).updateById(any());
    }

    @Test
    @DisplayName("驳回任务")
    void handle_reject() {
        when(flowTaskMapper.selectById(1L)).thenReturn(testTask);
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(flowDefinitionMapper.selectById(1L)).thenReturn(testDefinition);
        when(flowTaskMapper.updateById(any())).thenReturn(1);
        when(flowInstanceMapper.updateById(any())).thenReturn(1);

        flowTaskService.handle(1L, "reject", "不同意", null);

        assertEquals("rejected", testTask.getStatus());
        assertEquals("reject", testTask.getAction());
    }

    @Test
    @DisplayName("转办任务-成功")
    void handle_delegate_success() {
        when(flowTaskMapper.selectById(1L)).thenReturn(testTask);
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(flowTaskMapper.updateById(any())).thenReturn(1);
        when(flowTaskMapper.insert(any())).thenReturn(1);

        flowTaskService.handle(1L, "delegate", "转办处理", "user002");

        assertEquals("delegated", testTask.getStatus());
        assertEquals("user002", testTask.getDelegateUser());
        verify(flowTaskMapper).insert(any()); // 创建转办任务
    }

    @Test
    @DisplayName("转办任务-未指定人员")
    void handle_delegate_noUser() {
        when(flowTaskMapper.selectById(1L)).thenReturn(testTask);
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);

        assertThrows(RuntimeException.class, () -> flowTaskService.handle(1L, "delegate", "转办", null));
    }

    @Test
    @DisplayName("处理任务-任务不存在")
    void handle_taskNotFound() {
        when(flowTaskMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> flowTaskService.handle(1L, "approve", "同意", null));
    }

    @Test
    @DisplayName("处理任务-任务已处理")
    void handle_taskAlreadyProcessed() {
        testTask.setStatus("approved");
        when(flowTaskMapper.selectById(1L)).thenReturn(testTask);

        assertThrows(RuntimeException.class, () -> flowTaskService.handle(1L, "approve", "同意", null));
    }

    @Test
    @DisplayName("批量审批")
    void batchApprove_success() {
        when(flowTaskMapper.selectById(any())).thenReturn(testTask);
        when(flowInstanceMapper.selectById(1L)).thenReturn(testInstance);
        when(flowDefinitionMapper.selectById(1L)).thenReturn(testDefinition);
        when(flowTaskMapper.updateById(any())).thenReturn(1);
        when(flowInstanceMapper.updateById(any())).thenReturn(1);

        flowTaskService.batchApprove(List.of(1L, 2L));

        verify(flowTaskMapper, times(2)).updateById(any());
    }

    @Test
    @DisplayName("获取待办数量")
    void getPendingCount_success() {
        when(flowTaskMapper.selectCount(any())).thenReturn(5L);

        int count = flowTaskService.getPendingCount("user001");

        assertEquals(5, count);
    }
}