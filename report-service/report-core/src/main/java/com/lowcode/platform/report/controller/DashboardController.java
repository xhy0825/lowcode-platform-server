package com.lowcode.platform.report.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.report.entity.DashboardDefinition;
import com.lowcode.platform.report.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 仪表盘管理控制器
 */
@Tag(name = "仪表盘管理")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "仪表盘列表")
    @GetMapping("/list")
    public R<Page<DashboardDefinition>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(dashboardService.listPage(pageNum, pageSize));
    }

    @Operation(summary = "仪表盘详情")
    @GetMapping("/{id}")
    public R<DashboardDefinition> getInfo(@PathVariable Long id) {
        return R.ok(dashboardService.getById(id));
    }

    @Operation(summary = "创建仪表盘")
    @PostMapping
    public R<Long> create(@RequestBody DashboardDefinition dashboard) {
        return R.ok(dashboardService.create(dashboard));
    }

    @Operation(summary = "更新仪表盘")
    @PutMapping
    public R<Void> update(@RequestBody DashboardDefinition dashboard) {
        dashboardService.update(dashboard);
        return R.ok();
    }

    @Operation(summary = "发布仪表盘")
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        dashboardService.publish(id);
        return R.ok();
    }

    @Operation(summary = "删除仪表盘")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        dashboardService.delete(id);
        return R.ok();
    }

    @Operation(summary = "获取仪表盘数据")
    @GetMapping("/{id}/data")
    public R<Map<String, Object>> getData(
            @PathVariable Long id,
            @RequestParam(required = false) Map<String, Object> params) {
        return R.ok(dashboardService.getDashboardData(id, params));
    }

    @Operation(summary = "更新图表布局")
    @PutMapping("/{id}/layout")
    public R<Void> updateLayout(@PathVariable Long id, @RequestBody String layoutConfig) {
        dashboardService.updateLayout(id, layoutConfig);
        return R.ok();
    }
}