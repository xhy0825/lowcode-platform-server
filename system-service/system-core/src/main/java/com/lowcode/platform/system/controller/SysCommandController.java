package com.lowcode.platform.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.result.R;
import com.lowcode.platform.system.entity.SysCommand;
import com.lowcode.platform.system.entity.SysCommandLog;
import com.lowcode.platform.system.service.SysCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 命令管理控制器
 */
@Tag(name = "命令管理")
@RestController
@RequestMapping("/system/command")
@RequiredArgsConstructor
public class SysCommandController {

    private final SysCommandService commandService;

    @Operation(summary = "命令列表")
    @GetMapping("/list")
    public R<Page<SysCommand>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SysCommand query) {
        Page<SysCommand> page = new Page<>(pageNum, pageSize);
        return R.ok(commandService.selectPage(page, query));
    }

    @Operation(summary = "命令详情")
    @GetMapping("/{id}")
    public R<SysCommand> getInfo(@PathVariable Long id) {
        return R.ok(commandService.getById(id));
    }

    @Operation(summary = "创建命令")
    @PostMapping
    public R<Void> add(@RequestBody SysCommand command) {
        commandService.createCommand(command);
        return R.ok();
    }

    @Operation(summary = "修改命令")
    @PutMapping
    public R<Void> edit(@RequestBody SysCommand command) {
        commandService.updateCommand(command);
        return R.ok();
    }

    @Operation(summary = "删除命令")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        commandService.deleteCommand(id);
        return R.ok();
    }

    @Operation(summary = "手动执行命令")
    @PostMapping("/{id}/execute")
    public R<SysCommandLog> execute(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> params) {
        SysCommandLog log = commandService.executeCommand(id, params, "manual");
        return R.ok(log);
    }

    @Operation(summary = "查询执行日志")
    @GetMapping("/{id}/logs")
    public R<List<SysCommandLog>> getLogs(@PathVariable Long id) {
        return R.ok(commandService.selectLogsByCommandId(id));
    }

    @Operation(summary = "执行日志分页")
    @GetMapping("/{id}/logs/page")
    public R<Page<SysCommandLog>> getLogPage(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysCommandLog> page = new Page<>(pageNum, pageSize);
        return R.ok(commandService.selectLogPage(page, id));
    }

    @Operation(summary = "配置定时任务")
    @PutMapping("/{id}/schedule")
    public R<Void> configSchedule(@PathVariable Long id, @RequestParam String cronExpression) {
        commandService.configSchedule(id, cronExpression);
        return R.ok();
    }

    @Operation(summary = "取消定时任务")
    @DeleteMapping("/{id}/schedule")
    public R<Void> cancelSchedule(@PathVariable Long id) {
        commandService.cancelSchedule(id);
        return R.ok();
    }

    @Operation(summary = "检查脚本安全性")
    @PostMapping("/check-security")
    public R<Map<String, Object>> checkSecurity(@RequestBody String scriptContent) {
        return R.ok(commandService.checkScriptSecurity(scriptContent));
    }

    @Operation(summary = "脚本模板示例")
    @GetMapping("/templates")
    public R<Map<String, String>> getTemplates() {
        Map<String, String> templates = new java.util.HashMap<>();
        templates.put("数据清理", """
// 数据清理脚本模板
import com.lowcode.platform.common.groovy.api.ServiceLocator

def dataService = ServiceLocator.getInstance().getBean('dataService')
def tenantId = params.tenantId ?: '000000'
def days = params.days ?: 30

def result = dataService.deleteOldData(tenantId, days)
return "清理完成，删除 ${result} 条数据"
""");
        templates.put("数据导出", """
// 数据导出脚本模板
import com.lowcode.platform.common.groovy.api.ServiceLocator

def exportService = ServiceLocator.getInstance().getBean('exportService')
def formId = params.formId
def format = params.format ?: 'xlsx'

def file = exportService.exportData(formId, format)
return "导出完成，文件路径: ${file}"
""");
        templates.put("消息通知", """
// 消息通知脚本模板
import com.lowcode.platform.common.groovy.api.ServiceLocator

def notifyService = ServiceLocator.getInstance().getBean('notifyService')
def userIds = params.userIds
def message = params.message

def result = notifyService.sendNotification(userIds, message)
return "通知发送成功，接收人数: ${result}"
""");
        return R.ok(templates);
    }
}