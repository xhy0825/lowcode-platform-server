package com.lowcode.platform.common.security.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * 验证码结果
 */
@Data
@AllArgsConstructor
public class CaptchaResult {

    /**
     * 验证码Key（用于验证）
     */
    private String captchaKey;

    /**
     * 验证码图片（Base64）
     */
    private String captchaImage;

    /**
     * 验证码值（不返回给前端）
     */
    private String code;
}