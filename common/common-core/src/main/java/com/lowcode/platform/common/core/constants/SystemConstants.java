package com.lowcode.platform.common.core.constants;

/**
 * 系统常量
 */
public interface SystemConstants {

    /** 成功标记 */
    String SUCCESS = "success";

    /** 失败标记 */
    String FAIL = "fail";

    /** 登录成功 */
    String LOGIN_SUCCESS = "login.success";

    /** 登录失败 */
    String LOGIN_FAIL = "login.fail";

    /** 登录用户缓存key */
    String LOGIN_TOKEN_KEY = "login_tokens:";

    /** 验证码缓存key */
    String CAPTCHA_CODE_KEY = "captcha_codes:";

    /** 租户ID请求头 */
    String TENANT_ID_HEADER = "X-Tenant-Id";

    /** Token请求头 */
    String TOKEN_HEADER = "Authorization";

    /** 默认密码 */
    String DEFAULT_PASSWORD = "123456";

    /** 正常状态 */
    int STATUS_NORMAL = 0;

    /** 禁用状态 */
    int STATUS_DISABLE = 1;

    /** 删除标记 */
    int DEL_FLAG_NORMAL = 0;

    int DEL_FLAG_DELETED = 1;
}