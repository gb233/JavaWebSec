package com.javaweb.security.service;

import com.javaweb.security.entity.UserActivity;
import java.util.List;
import java.util.Map;

/**
 * 用户活动记录服务接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
public interface UserActivityService {

  /**
   * 记录用户活动
   *
   * @param userId 用户ID
   * @param activityType 活动类型
   * @param vulnerabilityCode 漏洞代码
   * @param title 活动标题
   * @param description 活动描述
   * @param metadata 活动元数据
   */
  void recordActivity(
      Long userId,
      UserActivity.ActivityType activityType,
      String vulnerabilityCode,
      String title,
      String description,
      Map<String, Object> metadata);

  /**
   * 获取用户最近活动
   *
   * @param userId 用户ID
   * @param limit 限制数量
   * @return 最近活动列表
   */
  List<UserActivity> getRecentActivities(Long userId, int limit);

  /**
   * 获取用户活动统计
   *
   * @param userId 用户ID
   * @return 活动统计信息
   */
  Map<String, Object> getActivityStatistics(Long userId);

  /**
   * 记录学习完成活动
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @param studyTime 学习时长
   * @param score 得分
   */
  void recordLearningCompleted(
      Long userId, String vulnerabilityCode, Integer studyTime, Integer score);

  /**
   * 记录测试通过活动
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @param score 得分
   * @param accuracy 正确率
   */
  void recordTestPassed(Long userId, String vulnerabilityCode, Integer score, Double accuracy);

  /**
   * 记录挑战完成活动
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @param score 得分
   * @param badge 徽章
   */
  void recordChallengeCompleted(Long userId, String vulnerabilityCode, Integer score, String badge);
}
