package com.javaweb.security.service;

import java.util.Map;

/** 徽章检测服务接口 */
public interface BadgeDetectionService {

  /** 检测学习类徽章 */
  void checkLearningBadges(Long userId, String vulnerabilityCode);

  /** 检测测试类徽章 */
  void checkTestBadges(Long userId, Integer score, Double accuracy);

  /** 检测挑战类徽章 */
  void checkChallengeBadges(Long userId, Long challengeId);

  /** 检测特殊类徽章 */
  void checkSpecialBadges(Long userId, String activityType, Map<String, Object> data);

  /** 检测连续学习徽章 */
  void checkStreakBadges(Long userId);

  /** 检测学习时长徽章 */
  void checkStudyTimeBadges(Long userId);

  /** 检测漏洞学习徽章 */
  void checkVulnerabilityBadges(Long userId, String vulnerabilityCode);

  /** 检测测试表现徽章 */
  void checkTestPerformanceBadges(Long userId, Integer score, Double accuracy);

  /** 检测挑战表现徽章 */
  void checkChallengePerformanceBadges(Long userId, Long challengeId);

  /** 检测时间相关徽章 */
  void checkTimeBasedBadges(Long userId);

  /** 检测行为相关徽章 */
  void checkBehaviorBadges(Long userId, String activityType, Map<String, Object> data);

  /** 检测首次登录徽章 */
  void checkFirstLoginBadge(Long userId);
}
