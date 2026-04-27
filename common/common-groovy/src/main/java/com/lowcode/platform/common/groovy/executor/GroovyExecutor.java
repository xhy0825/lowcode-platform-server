package com.lowcode.platform.common.groovy.executor;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Groovy脚本执行器
 */
@Slf4j
@Component
public class GroovyExecutor {

    private final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    /** 执行超时时间(秒) */
    private static final int DEFAULT_TIMEOUT = 300;

    /** 执行线程池 */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * 执行Groovy脚本
     * @param scriptContent 脚本内容
     * @param params 参数
     * @param timeout 超时时间(秒)
     * @return 执行结果
     */
    public GroovyResult execute(String scriptContent, Map<String, Object> params, int timeout) {
        return execute(scriptContent, params, timeout, null);
    }

    /**
     * 执行Groovy脚本（带绑定变量）
     */
    public GroovyResult execute(String scriptContent, Map<String, Object> params,
                                 int timeout, Map<String, Object> bindings) {
        GroovyResult result = new GroovyResult();
        result.setStartTime(System.currentTimeMillis());

        try {
            // 解析脚本
            Class<?> groovyClass = groovyClassLoader.parseClass(scriptContent);

            // 创建实例
            GroovyObject groovyObject = (GroovyObject) groovyClass.getDeclaredConstructor().newInstance();

            // 设置参数
            if (params != null) {
                groovyObject.setProperty("params", params);
            }

            // 设置绑定变量（如服务注入）
            if (bindings != null) {
                for (Map.Entry<String, Object> entry : bindings.entrySet()) {
                    groovyObject.setProperty(entry.getKey(), entry.getValue());
                }
            }

            // 在独立线程中执行（支持超时）
            Future<Object> future = executorService.submit(() -> {
                return groovyObject.invokeMethod("run", new Object[]{});
            });

            // 等待执行结果
            Object returnValue = future.get(timeout > 0 ? timeout : DEFAULT_TIMEOUT, TimeUnit.SECONDS);

            result.setSuccess(true);
            result.setResult(returnValue != null ? returnValue.toString() : "执行成功");

        } catch (TimeoutException e) {
            result.setSuccess(false);
            result.setError("脚本执行超时");
            log.error("Groovy脚本执行超时: {}", e.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(e.getMessage());
            log.error("Groovy脚本执行异常: ", e);
        }

        result.setEndTime(System.currentTimeMillis());
        result.setDuration(result.getEndTime() - result.getStartTime());

        return result;
    }

    /**
     * 异步执行Groovy脚本
     */
    public void executeAsync(String scriptContent, Map<String, Object> params,
                             GroovyCallback callback) {
        executorService.submit(() -> {
            GroovyResult result = execute(scriptContent, params, DEFAULT_TIMEOUT);
            if (callback != null) {
                callback.onComplete(result);
            }
        });
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            groovyClassLoader.close();
        } catch (Exception e) {
            log.warn("关闭GroovyClassLoader失败: {}", e.getMessage());
        }
    }
}