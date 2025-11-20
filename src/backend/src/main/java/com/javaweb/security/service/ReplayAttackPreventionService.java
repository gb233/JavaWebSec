package com.javaweb.security.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 防重放攻击服务
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class ReplayAttackPreventionService {

  @Value("${app.security.replay-prevention.enabled:true}")
  private boolean replayPreventionEnabled;

  @Value("${app.security.replay-prevention.nonce-expiry-seconds:300}")
  private int nonceExpirySeconds;

  @Value("${app.security.replay-prevention.timestamp-tolerance-seconds:60}")
  private int timestampToleranceSeconds;

  private final Map<String, Long> nonceStore = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public ReplayAttackPreventionService() {
    // 定期清理过期的nonce
    scheduler.scheduleAtFixedRate(this::cleanExpiredNonces, 1, 1, TimeUnit.MINUTES);
  }

  /**
   * 生成nonce token
   *
   * @param clientId 客户端ID
   * @return nonce信息
   */
  public Map<String, String> generateNonce(String clientId) {
    if (!replayPreventionEnabled) {
      return Map.of(
          "nonce", "", "timestamp", String.valueOf(System.currentTimeMillis()), "enabled", "false");
    }

    String nonce = clientId + "_" + System.currentTimeMillis() + "_" + Math.random();
    long expiryTime = System.currentTimeMillis() + nonceExpirySeconds * 1000L;
    nonceStore.put(nonce, expiryTime);

    return Map.of(
        "nonce",
        nonce,
        "timestamp",
        String.valueOf(System.currentTimeMillis()),
        "expiryTime",
        String.valueOf(expiryTime),
        "enabled",
        "true");
  }

  /**
   * 验证nonce token
   *
   * @param nonce nonce token
   * @param timestamp 时间戳（可以是String或Long）
   * @param clientId 客户端ID
   * @return 验证结果
   */
  public boolean verifyNonce(String nonce, Object timestamp, String clientId) {
    if (!replayPreventionEnabled) {
      return true;
    }

    if (nonce == null || timestamp == null) {
      return false;
    }

    // 转换时间戳
    long timestampValue;
    if (timestamp instanceof Long) {
      timestampValue = (Long) timestamp;
    } else if (timestamp instanceof String) {
      try {
        timestampValue = Long.parseLong((String) timestamp);
      } catch (NumberFormatException e) {
        log.warn("时间戳格式错误：timestamp={}", timestamp);
        return false;
      }
    } else {
      log.warn("时间戳类型不支持：timestamp={}", timestamp);
      return false;
    }

    // 检查时间戳是否在容差范围内
    long now = System.currentTimeMillis();
    long timeDiff = Math.abs(now - timestampValue);
    if (timeDiff > timestampToleranceSeconds * 1000L) {
      log.warn("时间戳超出容差范围：timestamp={}, now={}, diff={}ms", timestampValue, now, timeDiff);
      return false;
    }

    // 检查nonce是否存在且未过期
    Long expiryTime = nonceStore.get(nonce);
    if (expiryTime == null) {
      log.warn("nonce不存在或已使用：nonce={}", nonce);
      return false;
    }

    if (now > expiryTime) {
      nonceStore.remove(nonce);
      log.warn("nonce已过期：nonce={}", nonce);
      return false;
    }

    // 验证后删除nonce（一次性使用）
    nonceStore.remove(nonce);
    return true;
  }

  private void cleanExpiredNonces() {
    long now = System.currentTimeMillis();
    nonceStore.entrySet().removeIf(entry -> now > entry.getValue());
  }
}
