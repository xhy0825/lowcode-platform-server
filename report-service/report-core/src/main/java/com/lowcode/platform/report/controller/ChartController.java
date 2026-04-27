package com.lowcode.platform.report.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.report.entity.ChartDefinition;
import com.lowcode.platform.report.entity.DashboardDefinition;
import com.lowcode.platform.report.service.ChartService;
import com.lowcode.platform.report.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 图表管理控制器
 */
@Tag(name = "图表管理")
@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
public class ChartController {

    private final ChartService chartService;

    @Operation(summary = "图表列表")
    @GetMapping("/list")
    public R<Page<ChartDefinition>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String chartType) {
        return R.ok(chartService.listPage(pageNum, pageSize, chartType));
    }

    @Operation(summary = "图表详情")
    @GetMapping("/{id}")
    public R<ChartDefinition> getInfo(@PathVariable Long id) {
        return R.ok(chartService.getById(id));
    }

    @Operation(summary = "创建图表")
    @PostMapping
    public R<Long> create(@RequestBody ChartDefinition chart) {
        return R.ok(chartService.create(chart));
    }

    @Operation(summary = "更新图表")
    @PutMapping
    public R<Void> update(@RequestBody ChartDefinition chart) {
        chartService.update(chart);
        return R.ok();
    }

    @Operation(summary = "发布图表")
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        chartService.publish(id);
        return R.ok();
    }

    @Operation(summary = "删除图表")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        chartService.delete(id);
        return R.ok();
    }

    @Operation(summary = "获取图表数据")
    @GetMapping("/{id}/data")
    public R<Map<String, Object>> getData(
            @PathVariable Long id,
            @RequestParam(required = false) Map<String, Object> params) {
        return R.ok(chartService.getChartData(id, params));
    }

    @Operation(summary = "预览图表数据")
    @PostMapping("/preview")
    public R<Map<String, Object>> preview(
            @RequestBody ChartDefinition chart,
            @RequestParam(required = false) Map<String, Object> params) {
        return R.ok(chartService.previewData(chart, params));
    }
}