package com.javaweb.security.service;

import com.javaweb.security.entity.AchievementBadge;
import com.javaweb.security.entity.UserBadge;
import java.util.Map;

/** 徽章通知服务接口 */
public interface BadgeNotificationService {

  /** 发送徽章获得通知 */
  void notifyBadgeEarned(Long userId, AchievementBadge badge);

  /** 发送徽章获得通知（通过用户徽章记录） */
  void notifyBadgeEarned(Long userId, UserBadge userBadge);

  /** 发送徽章进度更新通知 */
  void notifyBadgeProgressUpdate(Long userId, Long badgeId, Integer progress, Integer target);

  /** 发送徽章即将完成通知 */
  void notifyBadgeNearCompletion(Long userId, Long badgeId, Integer progress, Integer target);

  /** 发送徽章完成通知 */
  void notifyBadgeCompleted(Long userId, AchievementBadge badge);

  /** 发送徽章统计更新通知 */
  void notifyBadgeStatsUpdate(Long userId, Map<String, Object> stats);

  /** 发送徽章里程碑通知 */
  void notifyBadgeMilestone(Long userId, String milestone, Object value);
}
