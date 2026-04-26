package com.lowcode.platform.common.security.annotation;

import java.lang.annotation.*;

/**
 * 角色校验注解
 * 标注在方法或类上，表示需要指定角色才能访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRoles {

    /**
     * 角色编码数组
     */
    String[] value();

    /**
     * 逻辑关系：AND必须全部拥有，OR拥有其一即可
     */
    Logical logical() default Logical.AND;
}