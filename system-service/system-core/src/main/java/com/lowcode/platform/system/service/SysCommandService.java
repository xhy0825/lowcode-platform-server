package com.lowcode.platform.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.system.entity.SysCommand;
import com.lowcode.platform.system.entity.SysCommandLog;

import java.util.List;
import java.util.Map;

/**
 * 命令服务接口
 */
public interface SysCommandService extends IService<SysCommand> {

    /** 分页查询 */
    Page<SysCommand> selectPage(Page<SysCommand> page, SysCommand query);

    /** 根据编码查询 */
    SysCommand selectByCommandCode(String commandCode);

    /** 创建命令 */
    boolean createCommand(SysCommand command);

    /** 更新命令 */
    boolean updateCommand(SysCommand command);

    /** 删除命令 */
    boolean deleteCommand(Long commandId);

    /** 手动执行命令 */
    SysCommandLog executeCommand(Long commandId, Map<String, Object> params, String triggerBy);

    /** 查询执行日志 */
    List<SysCommandLog> selectLogsByCommandId(Long commandId);

    /** 分页查询日志 */
    Page<SysCommandLog> selectLogPage(Page<SysCommandLog> page, Long commandId);

    /** 配置定时任务 */
    boolean configSchedule(Long commandId, String cronExpression);

    /** 取消定时任务 */
    boolean cancelSchedule(Long commandId);

    /** 检查脚本安全性 */
    Map<String, Object> checkScriptSecurity(String scriptContent);
}