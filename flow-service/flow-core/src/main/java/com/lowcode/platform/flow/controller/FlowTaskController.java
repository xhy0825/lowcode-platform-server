package com.lowcode.platform.flow.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.flow.entity.FlowTask;
import com.lowcode.platform.flow.service.FlowTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 流程任务控制器
 */
@RestController
@RequestMapping("/flow")
@RequiredArgsConstructor
public class FlowTaskController {

    private final FlowTaskService flowTaskService;

    /**
     * 分页查询任务
     */
    @GetMapping("/tasks")
    public R<Page<FlowTask>> getTasks(@RequestParam Map<String, Object> params) {
        return R.ok(flowTaskService.listPage(params));
    }

    /**
     * 处理任务
     */
    @PostMapping("/task/{id}/handle")
    public R<Void> handleTask(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String action = (String) params.get("action");
        String comment = (String) params.get("comment");
        String delegateUser = (String) params.get("delegateUser");
        flowTaskService.handle(id, action, comment, delegateUser);
        return R.ok();
    }

    /**
     * 批量审批
     */
    @PostMapping("/task/batch-approve")
    public R<Void> batchApprove(@RequestBody List<Long> taskIds) {
        flowTaskService.batchApprove(taskIds);
        return R.ok();
    }

    /**
     * 获取待办任务数
     */
    @GetMapping("/task/pending-count")
    public R<Integer> getPendingCount(@RequestParam String userId) {
        return R.ok(flowTaskService.getPendingCount(userId));
    }
}