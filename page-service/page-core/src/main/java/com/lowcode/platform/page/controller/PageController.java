package com.lowcode.platform.page.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.page.entity.PageComponent;
import com.lowcode.platform.page.entity.PageDefinition;
import com.lowcode.platform.page.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 页面管理控制器
 */
@Tag(name = "页面管理")
@RestController
@RequestMapping("/page")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    @Operation(summary = "页面列表")
    @GetMapping("/list")
    public R<Page<PageDefinition>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String pageType) {
        return R.ok(pageService.listPage(pageNum, pageSize, pageType));
    }

    @Operation(summary = "页面详情")
    @GetMapping("/{id}")
    public R<Map<String, Object>> getDetail(@PathVariable Long id) {
        return R.ok(pageService.getDetail(id));
    }

    @Operation(summary = "创建页面")
    @PostMapping
    public R<Long> create(@RequestBody PageDefinition page) {
        return R.ok(pageService.create(page));
    }

    @Operation(summary = "更新页面")
    @PutMapping
    public R<Void> update(@RequestBody PageDefinition page) {
        pageService.update(page);
        return R.ok();
    }

    @Operation(summary = "发布页面")
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        pageService.publish(id);
        return R.ok();
    }

    @Operation(summary = "删除页面")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        pageService.delete(id);
        return R.ok();
    }

    @Operation(summary = "获取页面组件")
    @GetMapping("/{id}/components")
    public R<List<PageComponent>> getComponents(@PathVariable Long id) {
        return R.ok(pageService.getComponents(id));
    }

    @Operation(summary = "更新页面组件")
    @PutMapping("/{id}/components")
    public R<Void> updateComponents(@PathVariable Long id, @RequestBody List<PageComponent> components) {
        pageService.updateComponents(id, components);
        return R.ok();
    }

    @Operation(summary = "更新页面布局")
    @PutMapping("/{id}/layout")
    public R<Void> updateLayout(@PathVariable Long id, @RequestBody String layoutConfig) {
        pageService.updateLayout(id, layoutConfig);
        return R.ok();
    }

    @Operation(summary = "根据编码查询页面")
    @GetMapping("/code/{code}")
    public R<PageDefinition> getByCode(@PathVariable String code) {
        return R.ok(pageService.getByCode(code));
    }
}