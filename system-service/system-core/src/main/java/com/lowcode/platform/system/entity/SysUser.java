package com.lowcode.platform.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lowcode.platform.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 盐值 */
    private String salt;

    /** 真实姓名 */
    private String realName;

    /** 邵箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像 */
    private String avatar;

    /** 性别 0-未知 1-男 2-女 */
    private Integer gender;

    /** 状态 0-正常 1-禁用 */
    private Integer status;

    /** 部门ID */
    private Long deptId;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private java.time.LocalDateTime loginTime;
}