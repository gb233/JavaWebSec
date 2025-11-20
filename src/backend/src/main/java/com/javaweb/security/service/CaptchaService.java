package com.javaweb.security.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 验证码服务
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class CaptchaService {

  @Value("${app.security.captcha.enabled:true}")
  private boolean captchaEnabled;

  @Value("${app.security.captcha.expiry-seconds:120}")
  private int expirySeconds;

  private final Map<String, CaptchaInfo> captchaStore = new ConcurrentHashMap<>();
  private final Random random = new Random();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public CaptchaService() {
    // 定期清理过期的验证码
    scheduler.scheduleAtFixedRate(this::cleanExpiredCaptchas, 1, 1, TimeUnit.MINUTES);
  }

  /**
   * 生成验证码
   *
   * @param clientId 客户端ID
   * @return 验证码信息
   */
  public Map<String, String> generateCaptcha(String clientId) {
    if (!captchaEnabled) {
      return Map.of("captchaId", "", "captchaQuestion", "", "enabled", "false");
    }

    int num1 = random.nextInt(10) + 1;
    int num2 = random.nextInt(10) + 1;
    int answer = num1 + num2;
    String question = num1 + " + " + num2 + " = ?";

    String captchaId = clientId + "_" + System.currentTimeMillis();
    long expiryTime = System.currentTimeMillis() + expirySeconds * 1000L;
    captchaStore.put(captchaId, new CaptchaInfo(String.valueOf(answer), expiryTime));

    return Map.of(
        "captchaId",
        captchaId,
        "captchaQuestion",
        question,
        "expiryTime",
        String.valueOf(expiryTime),
        "enabled",
        "true");
  }

  /**
   * 验证验证码
   *
   * @param captchaId 验证码ID
   * @param answer 用户答案
   * @param clientId 客户端ID
   * @return 验证结果
   */
  public boolean verifyCaptcha(String captchaId, String answer, String clientId) {
    if (!captchaEnabled) {
      return true;
    }

    if (captchaId == null || answer == null) {
      return false;
    }

    CaptchaInfo info = captchaStore.get(captchaId);
    if (info == null) {
      log.warn("验证码不存在：captchaId={}", captchaId);
      return false;
    }

    if (System.currentTimeMillis() > info.expiryTime) {
      captchaStore.remove(captchaId);
      log.warn("验证码已过期：captchaId={}", captchaId);
      return false;
    }

    boolean isValid = info.answer.equals(answer.trim());
    if (isValid) {
      captchaStore.remove(captchaId);
    }

    return isValid;
  }

  private void cleanExpiredCaptchas() {
    long now = System.currentTimeMillis();
    captchaStore.entrySet().removeIf(entry -> now > entry.getValue().expiryTime);
  }

  private static class CaptchaInfo {
    String answer;
    long expiryTime;

    CaptchaInfo(String answer, long expiryTime) {
      this.answer = answer;
      this.expiryTime = expiryTime;
    }
  }
}
