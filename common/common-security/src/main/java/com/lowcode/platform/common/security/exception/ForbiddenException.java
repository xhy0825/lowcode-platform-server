package com.lowcode.platform.common.security.exception;

import com.lowcode.platform.common.core.exception.BusinessException;

/**
 * 权限不足异常
 */
public class ForbiddenException extends BusinessException {

    public ForbiddenException() {
        super(403, "权限不足");
    }

    public ForbiddenException(String message) {
        super(403, message);
    }

    public static ForbiddenException fromPermission(String permission) {
        return new ForbiddenException("缺少权限: " + permission);
    }
}