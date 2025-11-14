package com.javaweb.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 安全功能配置类
 *
 * <p>配置验证码、防重放攻击等安全功能的开关和参数
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityFeaturesConfig {

  /** 验证码配置 */
  private CaptchaConfig captcha = new CaptchaConfig();

  /** 防重放攻击配置 */
  private ReplayPreventionConfig replayPrevention = new ReplayPreventionConfig();

  @Data
  public static class CaptchaConfig {
    /** 是否启用验证码 */
    private boolean enabled = true;

    /** 验证码过期时间（分钟） */
    private int expiryMinutes = 5;
  }

  @Data
  public static class ReplayPreventionConfig {
    /** 是否启用防重放攻击 */
    private boolean enabled = true;

    /** nonce token过期时间（秒） */
    private int nonceExpirySeconds = 300;

    /** 时间戳容差（秒） */
    private int timestampToleranceSeconds = 60;
  }
}
