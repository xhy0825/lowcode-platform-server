package com.lowcode.platform.common.groovy.executor;

/**
 * Groovy执行回调接口
 */
public interface GroovyCallback {

    /** 执行完成回调 */
    void onComplete(GroovyResult result);

    /** 执行异常回调 */
    void onError(Exception e);
}