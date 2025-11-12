package com.javaweb.security.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * 用户统计数据更新服务接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
public interface UserStatsUpdateService {

  /**
   * 更新漏洞学习统计
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码 (A01, A02, etc.)
   * @param studyTime 学习时长(分钟)
   * @param points 获得积分
   */
  @Transactional
  void updateVulnerabilityStats(
      Long userId, String vulnerabilityCode, Integer studyTime, Integer points);

  /**
   * 更新测试统计
   *
   * @param userId 用户ID
   * @param testId 测试ID
   * @param passed 是否通过
   * @param score 得分
   * @param points 获得积分
   */
  @Transactional
  void updateTestStats(Long userId, Long testId, Boolean passed, Integer score, Integer points);

  /**
   * 更新挑战统计
   *
   * @param userId 用户ID
   * @param challengeId 挑战ID
   * @param completed 是否完成
   * @param points 获得积分
   * @param badge 获得徽章
   */
  @Transactional
  void updateChallengeStats(
      Long userId, Long challengeId, Boolean completed, Integer points, String badge);

  /**
   * 更新学习时长统计
   *
   * @param userId 用户ID
   * @param additionalTime 额外学习时长(分钟)
   */
  @Transactional
  void updateStudyTimeStats(Long userId, Integer additionalTime);

  /**
   * 更新连续学习天数
   *
   * @param userId 用户ID
   */
  @Transactional
  void updateStreakStats(Long userId);
}
