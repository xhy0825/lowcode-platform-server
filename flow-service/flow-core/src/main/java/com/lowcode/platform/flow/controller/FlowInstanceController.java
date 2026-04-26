package com.lowcode.platform.flow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.result.R;
import com.lowcode.platform.flow.entity.FlowInstance;
import com.lowcode.platform.flow.service.FlowInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 流程实例控制器
 */
@RestController
@RequestMapping("/flow")
@RequiredArgsConstructor
public class FlowInstanceController {

    private final FlowInstanceService flowInstanceService;

    /**
     * 分页查询流程实例
     */
    @GetMapping("/instances")
    public R<Page<FlowInstance>> getInstances(@RequestParam Map<String, Object> params) {
        return R.ok(flowInstanceService.listPage(params));
    }

    /**
     * 获取流程实例详情
     */
    @GetMapping("/instance/{id}")
    public R<Map<String, Object>> getInstanceDetail(@PathVariable Long id) {
        return R.ok(flowInstanceService.getDetail(id));
    }

    /**
     * 发起流程
     */
    @PostMapping("/start")
    public R<Long> start(@RequestBody Map<String, Object> params) {
        Long flowDefinitionId = Long.valueOf(params.get("flowDefinitionId").toString());
        Map<String, Object> formData = (Map<String, Object>) params.get("formData");
        return R.ok(flowInstanceService.start(flowDefinitionId, formData));
    }

    /**
     * 取消流程实例
     */
    @PostMapping("/instance/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        flowInstanceService.cancel(id);
        return R.ok();
    }

    /**
     * 获取表单数据
     */
    @GetMapping("/instance/{instanceId}/form-data")
    public R<Map<String, Object>> getFormData(@PathVariable Long instanceId) {
        return R.ok(flowInstanceService.getFormData(instanceId));
    }
}