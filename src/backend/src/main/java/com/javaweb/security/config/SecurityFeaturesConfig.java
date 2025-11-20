package com.javaweb.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 安全功能配置
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityFeaturesConfig {

  private CaptchaConfig captcha = new CaptchaConfig();
  private ReplayPreventionConfig replayPrevention = new ReplayPreventionConfig();

  @Data
  public static class CaptchaConfig {
    private boolean enabled = true;
    private int expirySeconds = 120;
  }

  @Data
  public static class ReplayPreventionConfig {
    private boolean enabled = true;
    private int nonceExpirySeconds = 300;
    private int timestampToleranceSeconds = 60;
  }
}
