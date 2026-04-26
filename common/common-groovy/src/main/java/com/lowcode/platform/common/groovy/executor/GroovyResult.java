package com.lowcode.platform.common.groovy.executor;

import lombok.Data;

/**
 * Groovy执行结果
 */
@Data
public class GroovyResult {

    /** 是否成功 */
    private boolean success;

    /** 执行结果 */
    private String result;

    /** 错误信息 */
    private String error;

    /** 开始时间(ms) */
    private long startTime;

    /** 结束时间(ms) */
    private long endTime;

    /** 执行耗时(ms) */
    private long duration;

    /** 输出日志 */
    private String output;
}