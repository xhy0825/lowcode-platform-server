package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 命令定义实体
 */
@Data
@TableName("sys_command")
public class SysCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 命令ID */
    private Long id;

    /** 租户ID */
    private String tenantId;

    /** 命令名称 */
    private String commandName;

    /** 命令编码 */
    private String commandCode;

    /** 命令类型 script/sql/http/shell */
    private String commandType;

    /** Groovy脚本内容 */
    private String scriptContent;

    /** 参数定义(JSON) */
    private String paramsSchema;

    /** 触发类型 manual/cron/event */
    private String scheduleType;

    /** 定时表达式 */
    private String cronExpression;

    /** 超时时间(秒) */
    private Integer timeout;

    /** 重试次数 */
    private Integer retryCount;

    /** 状态 */
    private Integer status;

    /** 删除标记 */
    private Integer delFlag;

    /** 创建人 */
    private String createdBy;

    /** 创建时间 */
    private LocalDateTime createdTime;

    /** 更新人 */
    private String updatedBy;

    /** 更新时间 */
    private LocalDateTime updatedTime;

    /** 备注 */
    private String remark;
}