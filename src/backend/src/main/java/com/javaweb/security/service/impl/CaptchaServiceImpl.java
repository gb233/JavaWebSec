package com.javaweb.security.service.impl;

import com.javaweb.security.config.SecurityFeaturesConfig;
import com.javaweb.security.service.CaptchaService;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 验证码服务实现类
 *
 * <p>使用数学验证码，无法被OCR识别，只能通过人工计算 验证码存储在服务端内存中，前端无法绕过
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int CAPTCHA_CLEANUP_INTERVAL_MINUTES = 10; // 每10分钟清理一次过期验证码

  @Autowired private SecurityFeaturesConfig securityFeaturesConfig;

  /** 验证码存储：key为captchaId，value为验证码信息 */
  private final Map<String, CaptchaInfo> captchaStore = new ConcurrentHashMap<>();

  /** 客户端验证码映射：key为clientId，value为captchaId（防止同一客户端生成过多验证码） */
  private final Map<String, String> clientCaptchaMap = new ConcurrentHashMap<>();

  /** 定时清理任务 */
  private final ScheduledExecutorService cleanupExecutor =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "captcha-cleanup");
            t.setDaemon(true);
            return t;
          });

  public CaptchaServiceImpl() {
    // 启动定时清理任务
    cleanupExecutor.scheduleAtFixedRate(
        this::cleanExpiredCaptchas,
        CAPTCHA_CLEANUP_INTERVAL_MINUTES,
        CAPTCHA_CLEANUP_INTERVAL_MINUTES,
        TimeUnit.MINUTES);
  }

  @Override
  public Map<String, String> generateCaptcha(String clientId) {
    // 如果验证码未启用，返回空验证码
    if (!securityFeaturesConfig.getCaptcha().isEnabled()) {
      log.debug("验证码功能已禁用，返回空验证码");
      Map<String, String> result = new HashMap<>();
      result.put("captchaId", "");
      result.put("question", "");
      return result;
    }

    // 清理客户端之前的验证码
    String oldCaptchaId = clientCaptchaMap.get(clientId);
    if (oldCaptchaId != null) {
      captchaStore.remove(oldCaptchaId);
    }

    // 生成新的验证码
    String captchaId = generateCaptchaId();
    CaptchaQuestion question = generateQuestion();

    // 存储验证码信息
    CaptchaInfo captchaInfo =
        new CaptchaInfo(question.getAnswer(), System.currentTimeMillis(), clientId);
    captchaStore.put(captchaId, captchaInfo);
    clientCaptchaMap.put(clientId, captchaId);

    log.debug(
        "生成验证码：captchaId={}, clientId={}, question={}",
        captchaId,
        clientId,
        question.getQuestion());

    Map<String, String> result = new HashMap<>();
    result.put("captchaId", captchaId);
    result.put("question", question.getQuestion());
    return result;
  }

  @Override
  public boolean verifyCaptcha(String captchaId, String answer, String clientId) {
    // 如果验证码未启用，直接返回true
    if (!securityFeaturesConfig.getCaptcha().isEnabled()) {
      log.debug("验证码功能已禁用，跳过验证");
      return true;
    }

    if (captchaId == null || answer == null || clientId == null) {
      log.warn("验证码验证失败：参数为空");
      return false;
    }

    CaptchaInfo captchaInfo = captchaStore.get(captchaId);
    if (captchaInfo == null) {
      log.warn("验证码验证失败：验证码不存在，captchaId={}", captchaId);
      return false;
    }

    // 检查是否过期
    long now = System.currentTimeMillis();
    int expiryMinutes = securityFeaturesConfig.getCaptcha().getExpiryMinutes();
    if (now - captchaInfo.getCreatedAt() > expiryMinutes * 60 * 1000L) {
      log.warn("验证码验证失败：验证码已过期，captchaId={}", captchaId);
      captchaStore.remove(captchaId);
      clientCaptchaMap.remove(captchaInfo.getClientId());
      return false;
    }

    // 检查客户端ID是否匹配（防止验证码被其他客户端使用）
    if (!clientId.equals(captchaInfo.getClientId())) {
      log.warn(
          "验证码验证失败：客户端ID不匹配，captchaId={}, expected={}, actual={}",
          captchaId,
          captchaInfo.getClientId(),
          clientId);
      return false;
    }

    // 验证答案
    try {
      int expectedAnswer = Integer.parseInt(captchaInfo.getAnswer());
      int userAnswer = Integer.parseInt(answer.trim());
      boolean isValid = expectedAnswer == userAnswer;

      if (isValid) {
        log.debug("验证码验证成功：captchaId={}", captchaId);
        // 验证成功后立即删除验证码（一次性使用）
        captchaStore.remove(captchaId);
        clientCaptchaMap.remove(captchaInfo.getClientId());
      } else {
        log.warn(
            "验证码验证失败：答案错误，captchaId={}, expected={}, actual={}",
            captchaId,
            expectedAnswer,
            userAnswer);
      }

      return isValid;
    } catch (NumberFormatException e) {
      log.warn("验证码验证失败：答案格式错误，captchaId={}, answer={}", captchaId, answer);
      return false;
    }
  }

  @Override
  public void cleanExpiredCaptchas() {
    long now = System.currentTimeMillis();
    int expiryMinutes = securityFeaturesConfig.getCaptcha().getExpiryMinutes();
    long expiryTime = expiryMinutes * 60 * 1000L;

    captchaStore
        .entrySet()
        .removeIf(
            entry -> {
              CaptchaInfo info = entry.getValue();
              boolean expired = now - info.getCreatedAt() > expiryTime;
              if (expired) {
                clientCaptchaMap.remove(info.getClientId());
                log.debug("清理过期验证码：captchaId={}", entry.getKey());
              }
              return expired;
            });

    log.debug("验证码清理完成，当前验证码数量：{}", captchaStore.size());
  }

  /** 生成验证码ID */
  private String generateCaptchaId() {
    return "captcha_" + System.currentTimeMillis() + "_" + RANDOM.nextInt(10000);
  }

  /** 生成数学验证码问题 */
  private CaptchaQuestion generateQuestion() {
    int num1 = RANDOM.nextInt(20) + 1; // 1-20
    int num2 = RANDOM.nextInt(20) + 1; // 1-20
    int answer = num1 + num2;
    String question = num1 + " + " + num2 + " = ?";
    return new CaptchaQuestion(question, String.valueOf(answer));
  }

  /** 验证码信息内部类 */
  private static class CaptchaInfo {
    private final String answer;
    private final long createdAt;
    private final String clientId;

    public CaptchaInfo(String answer, long createdAt, String clientId) {
      this.answer = answer;
      this.createdAt = createdAt;
      this.clientId = clientId;
    }

    public String getAnswer() {
      return answer;
    }

    public long getCreatedAt() {
      return createdAt;
    }

    public String getClientId() {
      return clientId;
    }
  }

  /** 验证码问题内部类 */
  private static class CaptchaQuestion {
    private final String question;
    private final String answer;

    public CaptchaQuestion(String question, String answer) {
      this.question = question;
      this.answer = answer;
    }

    public String getQuestion() {
      return question;
    }

    public String getAnswer() {
      return answer;
    }
  }
}
