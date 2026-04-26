package com.lowcode.platform.common.security.captcha;

import com.lowcode.platform.common.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final RedisService redisService;

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE = 300L; // 5分钟

    /**
     * 生成验证码
     */
    public CaptchaResult generateCaptcha() {
        String captchaKey = UUID.randomUUID().toString();
        String code = generateRandomCode(4);

        // 存入Redis
        redisService.set(CAPTCHA_KEY_PREFIX + captchaKey, code.toLowerCase(), CAPTCHA_EXPIRE, TimeUnit.SECONDS);

        // 生成图片
        BufferedImage image = createCaptchaImage(code);

        // 转换为Base64
        String base64Image = imageToBase64(image);

        return new CaptchaResult(captchaKey, base64Image, code);
    }

    /**
     * 验证验证码
     */
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }

        String key = CAPTCHA_KEY_PREFIX + captchaKey;
        Object storedCode = redisService.get(key);

        if (storedCode == null) {
            log.warn("验证码不存在或已过期: {}", captchaKey);
            return false;
        }

        // 删除已使用的验证码
        redisService.delete(key);

        // 忽略大小写比较
        boolean result = captchaCode.toLowerCase().equals(storedCode.toString().toLowerCase());
        if (!result) {
            log.warn("验证码错误: 输入={}, 存储={}", captchaCode, storedCode);
        }
        return result;
    }

    /**
     * 生成随机验证码
     */
    private String generateRandomCode(int length) {
        // 排除容易混淆的字符：0O1lI
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 创建验证码图片
     */
    private BufferedImage createCaptchaImage(String code) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 干扰线
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height));
        }

        // 干扰点
        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillOval(random.nextInt(width), random.nextInt(height), 2, 2);
        }

        // 验证码文字
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 28));

        // 每个字符单独绘制，添加旋转效果
        int x = 20;
        for (char c : code.toCharArray()) {
            // 随机颜色
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            // 随机旋转角度
            double angle = (random.nextDouble() - 0.5) * 0.3;
            g.rotate(angle, x, 20);
            g.drawString(String.valueOf(c), x, 28);
            g.rotate(-angle, x, 20);
            x += 25;
        }

        g.dispose();
        return image;
    }

    /**
     * 图片转Base64
     */
    private String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            log.error("图片转Base64失败", e);
            return "";
        }
    }
}