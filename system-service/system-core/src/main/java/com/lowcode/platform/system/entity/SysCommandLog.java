package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 命令执行日志实体
 */
@Data
@TableName("sys_command_log")
public class SysCommandLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long id;

    /** 命令ID */
    private Long commandId;

    /** 租户ID */
    private String tenantId;

    /** 触发类型 manual/schedule/event */
    private String triggerType;

    /** 执行参数(JSON) */
    private String params;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 执行耗时(ms) */
    private Integer duration;

    /** 状态 running/success/failed */
    private String status;

    /** 执行结果 */
    private String result;

    /** 异常信息 */
    private String errorMessage;
}