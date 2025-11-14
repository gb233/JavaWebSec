package com.javaweb.security.service.impl;

import com.javaweb.security.config.SecurityFeaturesConfig;
import com.javaweb.security.service.ReplayAttackPreventionService;
import java.security.MessageDigest;
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
 * 防重放攻击服务实现类
 *
 * <p>使用一次性nonce token + 时间戳机制防止重放攻击： 1. 页面加载时生成nonce token 2. 提交时携带nonce token和时间戳 3. 后端验证nonce
 * token是否使用过，使用后立即失效 4. 验证时间戳，防止过期请求
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@Service
public class ReplayAttackPreventionServiceImpl implements ReplayAttackPreventionService {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int NONCE_CLEANUP_INTERVAL_MINUTES = 10; // 每10分钟清理一次过期nonce

  @Autowired private SecurityFeaturesConfig securityFeaturesConfig;

  /** nonce token存储：key为nonce，value为nonce信息 */
  private final Map<String, NonceInfo> nonceStore = new ConcurrentHashMap<>();

  /** 客户端nonce映射：key为clientId，value为nonce（防止同一客户端生成过多nonce） */
  private final Map<String, String> clientNonceMap = new ConcurrentHashMap<>();

  /** 定时清理任务 */
  private final ScheduledExecutorService cleanupExecutor =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "nonce-cleanup");
            t.setDaemon(true);
            return t;
          });

  public ReplayAttackPreventionServiceImpl() {
    // 启动定时清理任务
    cleanupExecutor.scheduleAtFixedRate(
        this::cleanExpiredNonces,
        NONCE_CLEANUP_INTERVAL_MINUTES,
        NONCE_CLEANUP_INTERVAL_MINUTES,
        TimeUnit.MINUTES);
  }

  @Override
  public Map<String, String> generateNonce(String clientId) {
    // 如果防重放攻击未启用，返回空nonce
    if (!securityFeaturesConfig.getReplayPrevention().isEnabled()) {
      log.debug("防重放攻击功能已禁用，返回空nonce");
      Map<String, String> result = new HashMap<>();
      result.put("nonce", "");
      result.put("timestamp", String.valueOf(System.currentTimeMillis()));
      return result;
    }

    // 清理客户端之前的nonce
    String oldNonce = clientNonceMap.get(clientId);
    if (oldNonce != null) {
      nonceStore.remove(oldNonce);
    }

    // 生成新的nonce token
    String nonce = generateNonceToken();
    long timestamp = System.currentTimeMillis();

    // 存储nonce信息
    NonceInfo nonceInfo = new NonceInfo(clientId, timestamp, false);
    nonceStore.put(nonce, nonceInfo);
    clientNonceMap.put(clientId, nonce);

    log.debug("生成nonce token：nonce={}, clientId={}, timestamp={}", nonce, clientId, timestamp);

    Map<String, String> result = new HashMap<>();
    result.put("nonce", nonce);
    result.put("timestamp", String.valueOf(timestamp));
    return result;
  }

  @Override
  public boolean verifyNonce(String nonce, String timestamp, String clientId) {
    // 如果防重放攻击未启用，直接返回true
    if (!securityFeaturesConfig.getReplayPrevention().isEnabled()) {
      log.debug("防重放攻击功能已禁用，跳过验证");
      return true;
    }

    if (nonce == null || timestamp == null || clientId == null) {
      log.warn("nonce验证失败：参数为空");
      return false;
    }

    NonceInfo nonceInfo = nonceStore.get(nonce);
    if (nonceInfo == null) {
      log.warn("nonce验证失败：nonce不存在，nonce={}", nonce);
      return false;
    }

    // 检查是否已使用（一次性使用）
    if (nonceInfo.isUsed()) {
      log.warn("nonce验证失败：nonce已使用，nonce={}", nonce);
      // 删除已使用的nonce
      nonceStore.remove(nonce);
      clientNonceMap.remove(nonceInfo.getClientId());
      return false;
    }

    // 检查是否过期
    long now = System.currentTimeMillis();
    int nonceExpirySeconds = securityFeaturesConfig.getReplayPrevention().getNonceExpirySeconds();
    if (now - nonceInfo.getCreatedAt() > nonceExpirySeconds * 1000L) {
      log.warn("nonce验证失败：nonce已过期，nonce={}", nonce);
      nonceStore.remove(nonce);
      clientNonceMap.remove(nonceInfo.getClientId());
      return false;
    }

    // 检查客户端ID是否匹配
    if (!clientId.equals(nonceInfo.getClientId())) {
      log.warn(
          "nonce验证失败：客户端ID不匹配，nonce={}, expected={}, actual={}",
          nonce,
          nonceInfo.getClientId(),
          clientId);
      return false;
    }

    // 验证时间戳
    try {
      long requestTimestamp = Long.parseLong(timestamp);
      long timeDiff = Math.abs(now - requestTimestamp);
      int toleranceSeconds =
          securityFeaturesConfig.getReplayPrevention().getTimestampToleranceSeconds();
      if (timeDiff > toleranceSeconds * 1000L) {
        log.warn("nonce验证失败：时间戳超出容差范围，nonce={}, timeDiff={}ms", nonce, timeDiff);
        return false;
      }
    } catch (NumberFormatException e) {
      log.warn("nonce验证失败：时间戳格式错误，nonce={}, timestamp={}", nonce, timestamp);
      return false;
    }

    // 验证通过，标记为已使用
    nonceInfo.setUsed(true);
    log.debug("nonce验证成功：nonce={}", nonce);

    // 延迟删除，防止并发问题
    nonceStore.remove(nonce);
    clientNonceMap.remove(nonceInfo.getClientId());

    return true;
  }

  @Override
  public void cleanExpiredNonces() {
    long now = System.currentTimeMillis();
    int nonceExpirySeconds = securityFeaturesConfig.getReplayPrevention().getNonceExpirySeconds();
    long expiryTime = nonceExpirySeconds * 1000L;

    nonceStore
        .entrySet()
        .removeIf(
            entry -> {
              NonceInfo info = entry.getValue();
              boolean expired = now - info.getCreatedAt() > expiryTime;
              if (expired) {
                clientNonceMap.remove(info.getClientId());
                log.debug("清理过期nonce：nonce={}", entry.getKey());
              }
              return expired;
            });

    log.debug("nonce清理完成，当前nonce数量：{}", nonceStore.size());
  }

  /** 生成nonce token */
  private String generateNonceToken() {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      String input =
          System.currentTimeMillis() + "_" + RANDOM.nextLong() + "_" + RANDOM.nextInt(10000);
      byte[] hash = md.digest(input.getBytes());
      StringBuilder sb = new StringBuilder();
      for (byte b : hash) {
        sb.append(String.format("%02x", b));
      }
      return "nonce_" + sb.substring(0, 32); // 取前32位
    } catch (Exception e) {
      // 如果SHA-256不可用，使用简单随机字符串
      return "nonce_" + System.currentTimeMillis() + "_" + RANDOM.nextInt(1000000);
    }
  }

  /** nonce信息内部类 */
  private static class NonceInfo {
    private final String clientId;
    private final long createdAt;
    private volatile boolean used;

    public NonceInfo(String clientId, long createdAt, boolean used) {
      this.clientId = clientId;
      this.createdAt = createdAt;
      this.used = used;
    }

    public String getClientId() {
      return clientId;
    }

    public long getCreatedAt() {
      return createdAt;
    }

    public boolean isUsed() {
      return used;
    }

    public void setUsed(boolean used) {
      this.used = used;
    }
  }
}
