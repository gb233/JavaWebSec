package com.javaweb.security.service;

import java.util.Map;

/**
 * 防重放攻击服务接口
 *
 * <p>使用一次性nonce token机制防止重放攻击
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
public interface ReplayAttackPreventionService {

  /**
   * 生成一次性nonce token
   *
   * @param clientId 客户端唯一标识（IP地址）
   * @return 包含nonce token和时间戳的Map
   */
  Map<String, String> generateNonce(String clientId);

  /**
   * 验证nonce token是否有效
   *
   * @param nonce nonce token
   * @param timestamp 时间戳
   * @param clientId 客户端唯一标识
   * @return 验证是否通过
   */
  boolean verifyNonce(String nonce, String timestamp, String clientId);

  /** 清理过期的nonce token */
  void cleanExpiredNonces();
}
