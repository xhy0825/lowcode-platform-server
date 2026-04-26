package com.lowcode.platform.common.security.annotation;

/**
 * 逻辑关系枚举
 */
public enum Logical {
    /**
     * 必须全部拥有
     */
    AND,
    /**
     * 拥有其一即可
     */
    OR
}