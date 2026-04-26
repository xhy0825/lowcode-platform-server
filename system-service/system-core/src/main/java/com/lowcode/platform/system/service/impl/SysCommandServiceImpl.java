package com.lowcode.platform.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.common.groovy.executor.GroovyExecutor;
import com.lowcode.platform.common.groovy.executor.GroovyResult;
import com.lowcode.platform.common.groovy.sandbox.GroovySandbox;
import com.lowcode.platform.system.entity.SysCommand;
import com.lowcode.platform.system.entity.SysCommandLog;
import com.lowcode.platform.system.mapper.SysCommandMapper;
import com.lowcode.platform.system.mapper.SysCommandLogMapper;
import com.lowcode.platform.system.service.SysCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysCommandServiceImpl extends ServiceImpl<SysCommandMapper, SysCommand> implements SysCommandService {

    private final SysCommandMapper commandMapper;
    private final SysCommandLogMapper commandLogMapper;
    private final GroovyExecutor groovyExecutor;
    private final GroovySandbox groovySandbox;

    @Override
    public Page<SysCommand> selectPage(Page<SysCommand> page, SysCommand query) {
        LambdaQueryWrapper<SysCommand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCommand::getDelFlag, 0);
        if (StringUtils.hasText(query.getCommandName())) {
            wrapper.like(SysCommand::getCommandName, query.getCommandName());
        }
        if (StringUtils.hasText(query.getCommandCode())) {
            wrapper.like(SysCommand::getCommandCode, query.getCommandCode());
        }
        if (StringUtils.hasText(query.getCommandType())) {
            wrapper.eq(SysCommand::getCommandType, query.getCommandType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysCommand::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysCommand::getCreatedTime);
        return commandMapper.selectPage(page, wrapper);
    }

    @Override
    public SysCommand selectByCommandCode(String commandCode) {
        return commandMapper.selectByCommandCode(commandCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCommand(SysCommand command) {
        // 检查编码是否存在
        SysCommand exist = commandMapper.selectByCommandCode(command.getCommandCode());
        if (exist != null) {
            throw new BusinessException("命令编码已存在");
        }
        // 检查脚本安全性
        if ("script".equals(command.getCommandType()) && StringUtils.hasText(command.getScriptContent())) {
            GroovySandbox.SecurityCheckResult checkResult = groovySandbox.checkScriptSecurity(command.getScriptContent());
            if (!checkResult.isSecure()) {
                throw new BusinessException("脚本包含不安全代码: " + checkResult.getErrors());
            }
        }
        command.setStatus(0);
        command.setDelFlag(0);
        return commandMapper.insert(command) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCommand(SysCommand command) {
        SysCommand exist = commandMapper.selectById(command.getId());
        if (exist == null) {
            throw new BusinessException("命令不存在");
        }
        // 检查脚本安全性
        if ("script".equals(command.getCommandType()) && StringUtils.hasText(command.getScriptContent())) {
            GroovySandbox.SecurityCheckResult checkResult = groovySandbox.checkScriptSecurity(command.getScriptContent());
            if (!checkResult.isSecure()) {
                throw new BusinessException("脚本包含不安全代码: " + checkResult.getErrors());
            }
        }
        return commandMapper.updateById(command) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCommand(Long commandId) {
        SysCommand command = commandMapper.selectById(commandId);
        if (command == null) {
            throw new BusinessException("命令不存在");
        }
        // 取消定时任务
        if ("cron".equals(command.getScheduleType())) {
            cancelSchedule(commandId);
        }
        command.setDelFlag(1);
        return commandMapper.updateById(command) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysCommandLog executeCommand(Long commandId, Map<String, Object> params, String triggerBy) {
        SysCommand command = commandMapper.selectById(commandId);
        if (command == null) {
            throw new BusinessException("命令不存在");
        }
        if (command.getStatus() != 0) {
            throw new BusinessException("命令已禁用");
        }

        // 创建执行日志
        SysCommandLog log = new SysCommandLog();
        log.setCommandId(commandId);
        log.setTenantId(command.getTenantId());
        log.setTriggerType(StringUtils.hasText(triggerBy) ? triggerBy : "manual");
        log.setParams(params != null ? JSON.toJSONString(params) : null);
        log.setStartTime(LocalDateTime.now());
        log.setStatus("running");
        commandLogMapper.insert(log);

        try {
            // 执行脚本
            int timeout = command.getTimeout() != null ? command.getTimeout() : 300;
            GroovyResult result = groovyExecutor.execute(command.getScriptContent(), params, timeout);

            // 更新日志
            log.setEndTime(LocalDateTime.now());
            log.setDuration((int) (result.getEndTime() - result.getStartTime()));
            log.setStatus(result.isSuccess() ? "success" : "failed");
            log.setResult(result.getResult());
            log.setErrorMessage(result.getError());

            if (!result.isSuccess()) {
                // 重试机制
                if (command.getRetryCount() != null && command.getRetryCount() > 0) {
                    for (int i = 0; i < command.getRetryCount(); i++) {
                        log.info("命令执行失败，第{}次重试", i + 1);
                        GroovyResult retryResult = groovyExecutor.execute(command.getScriptContent(), params, timeout);
                        if (retryResult.isSuccess()) {
                            log.setStatus("success");
                            log.setResult(retryResult.getResult());
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.setEndTime(LocalDateTime.now());
            log.setDuration(0);
            log.setStatus("failed");
            log.setErrorMessage(e.getMessage());
            log.error("命令执行异常: ", e);
        }

        commandLogMapper.updateById(log);
        return log;
    }

    @Override
    public List<SysCommandLog> selectLogsByCommandId(Long commandId) {
        return commandLogMapper.selectByCommandId(commandId);
    }

    @Override
    public Page<SysCommandLog> selectLogPage(Page<SysCommandLog> page, Long commandId) {
        LambdaQueryWrapper<SysCommandLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCommandLog::getCommandId, commandId);
        wrapper.orderByDesc(SysCommandLog::getStartTime);
        return commandLogMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean configSchedule(Long commandId, String cronExpression) {
        SysCommand command = commandMapper.selectById(commandId);
        if (command == null) {
            throw new BusinessException("命令不存在");
        }
        // TODO: 实现定时任务调度（使用 Quartz 或 XXL-JOB）
        command.setScheduleType("cron");
        command.setCronExpression(cronExpression);
        return commandMapper.updateById(command) > 0;
    }

    @Override
    public boolean cancelSchedule(Long commandId) {
        SysCommand command = commandMapper.selectById(commandId);
        if (command == null) {
            throw new BusinessException("命令不存在");
        }
        // TODO: 取消定时任务
        command.setScheduleType("manual");
        command.setCronExpression(null);
        return commandMapper.updateById(command) > 0;
    }

    @Override
    public Map<String, Object> checkScriptSecurity(String scriptContent) {
        Map<String, Object> result = new HashMap<>();
        GroovySandbox.SecurityCheckResult checkResult = groovySandbox.checkScriptSecurity(scriptContent);
        result.put("secure", checkResult.isSecure());
        result.put("errors", checkResult.getErrors());
        return result;
    }
}