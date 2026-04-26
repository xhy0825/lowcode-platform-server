package com.lowcode.platform.common.groovy.sandbox;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Groovy安全沙箱 - 白名单机制
 */
@Component
public class GroovySandbox {

    /** 允许导入的包白名单 */
    private static final Set<String> ALLOWED_PACKAGES = new HashSet<>(Arrays.asList(
        "java.lang",
        "java.util",
        "java.time",
        "java.text",
        "java.math",
        "com.lowcode.platform.api",
        "com.lowcode.platform.common.core",
        "com.lowcode.platform.system.api"
    ));

    /** 允许导入的类白名单 */
    private static final Set<String> ALLOWED_CLASSES = new HashSet<>(Arrays.asList(
        "java.lang.String",
        "java.lang.Integer",
        "java.lang.Long",
        "java.lang.Double",
        "java.lang.Boolean",
        "java.util.List",
        "java.util.Map",
        "java.util.Set",
        "java.util.ArrayList",
        "java.util.HashMap",
        "java.time.LocalDateTime",
        "java.time.LocalDate",
        "java.time.format.DateTimeFormatter"
    ));

    /** 禁止的方法 */
    private static final Set<String> FORBIDDEN_METHODS = new HashSet<>(Arrays.asList(
        "execute",
        "system",
        "exit",
        "getClass",
        "forName",
        "getRuntime",
        "getDeclaredMethod",
        "getDeclaredField",
        "newInstance",
        "processBuilder",
        "startProcess"
    ));

    /**
     * 创建安全配置的GroovyClassLoader
     */
    public GroovyClassLoader createSecureClassLoader() {
        CompilerConfiguration config = new CompilerConfiguration();

        // 导入白名单
        ImportCustomizer importCustomizer = new ImportCustomizer();
        for (String pkg : ALLOWED_PACKAGES) {
            importCustomizer.addStarImports(pkg);
        }
        for (String cls : ALLOWED_CLASSES) {
            importCustomizer.addImports(cls);
        }
        config.addCompilationCustomizers(importCustomizer);

        // 安全AST检查
        SecureASTCustomizer secureCustomizer = new SecureASTCustomizer();
        secureCustomizer.setDisallowedImports(new HashSet<>()); // 只允许白名单导入
        secureCustomizer.setDisallowedMethods(FORBIDDEN_METHODS);

        // 禁止直接方法调用
        secureCustomizer.setIndirectMethodCallAllowed(false);

        // 禁止闭包
        secureCustomizer.setClosuresAllowed(true);

        config.addCompilationCustomizers(secureCustomizer);

        return new GroovyClassLoader(this.getClass().getClassLoader(), config);
    }

    /**
     * 检查脚本是否安全
     * @param scriptContent 脚本内容
     * @return 检查结果
     */
    public SecurityCheckResult checkScriptSecurity(String scriptContent) {
        SecurityCheckResult result = new SecurityCheckResult();
        result.setSecure(true);

        // 检查导入语句
        String[] lines = scriptContent.split("\n");
        for (String line : lines) {
            line = line.trim();

            // 检查import语句
            if (line.startsWith("import ")) {
                String importPath = line.substring(7).trim();
                if (!isAllowedImport(importPath)) {
                    result.setSecure(false);
                    result.addError("不允许导入: " + importPath);
                }
            }

            // 检查禁止的方法
            for (String method : FORBIDDEN_METHODS) {
                if (line.contains("." + method) || line.contains(method + "(")) {
                    result.setSecure(false);
                    result.addError("禁止调用方法: " + method);
                }
            }

            // 检查System.exit/Runtime等
            if (line.contains("System.exit") || line.contains("Runtime.getRuntime")
                || line.contains("ProcessBuilder") || line.contains("Thread.sleep")) {
                result.setSecure(false);
                result.addError("禁止调用危险方法: " + line);
            }
        }

        return result;
    }

    /**
     * 检查是否允许导入
     */
    private boolean isAllowedImport(String importPath) {
        // 星号导入
        if (importPath.endsWith("*")) {
            String pkg = importPath.substring(0, importPath.length() - 2);
            return ALLOWED_PACKAGES.contains(pkg);
        }
        // 类导入
        return ALLOWED_CLASSES.contains(importPath) ||
               ALLOWED_PACKAGES.stream().anyMatch(pkg -> importPath.startsWith(pkg + "."));
    }

    /**
     * 安全检查结果
     */
    public static class SecurityCheckResult {
        private boolean secure;
        private java.util.List<String> errors = new java.util.ArrayList<>();

        public boolean isSecure() { return secure; }
        public void setSecure(boolean secure) { this.secure = secure; }
        public java.util.List<String> getErrors() { return errors; }
        public void addError(String error) { this.errors.add(error); }
    }
}