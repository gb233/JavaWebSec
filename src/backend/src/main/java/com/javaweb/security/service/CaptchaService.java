package com.javaweb.security.service;

import java.util.Map;

/**
 * 验证码服务接口
 *
 * <p>提供验证码生成和验证功能，确保无法被绕过
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
public interface CaptchaService {

  /**
   * 生成验证码
   *
   * @param clientId 客户端唯一标识（可以是IP或sessionId）
   * @return 包含验证码ID和问题的Map，格式：{"captchaId": "xxx", "question": "3 + 5 = ?"}
   */
  Map<String, String> generateCaptcha(String clientId);

  /**
   * 验证验证码答案
   *
   * @param captchaId 验证码ID
   * @param answer 用户输入的答案
   * @param clientId 客户端唯一标识
   * @return 验证是否通过
   */
  boolean verifyCaptcha(String captchaId, String answer, String clientId);

  /** 清理过期的验证码 */
  void cleanExpiredCaptchas();
}
