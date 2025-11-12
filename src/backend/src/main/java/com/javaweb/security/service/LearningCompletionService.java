package com.javaweb.security.service;

import java.util.Map;

/**
 * 学习完成判断服务接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
public interface LearningCompletionService {

  /**
   * 判断用户是否完成漏洞学习
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @return 是否完成学习
   */
  boolean isVulnerabilityLearningCompleted(Long userId, String vulnerabilityCode);

  /**
   * 记录页面访问
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @param pageType 页面类型
   * @param duration 停留时长(秒)
   */
  void recordPageVisit(Long userId, String vulnerabilityCode, String pageType, Integer duration);

  /**
   * 记录用户交互
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @param interactionType 交互类型
   * @param data 交互数据
   */
  void recordUserInteraction(
      Long userId, String vulnerabilityCode, String interactionType, Map<String, Object> data);
}
