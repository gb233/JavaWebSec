package com.javaweb.security.service;

import com.javaweb.security.entity.BadgeProgress;
import java.util.List;
import java.util.Map;

/** 徽章进度服务接口 */
public interface BadgeProgressService {

  /** 获取用户徽章进度 */
  List<BadgeProgress> getUserBadgeProgress(Long userId);

  /** 根据分类获取用户徽章进度 */
  List<BadgeProgress> getUserBadgeProgressByCategory(Long userId, String category);

  /** 获取用户未完成的徽章进度 */
  List<BadgeProgress> getUserUncompletedBadgeProgress(Long userId);

  /** 获取用户已完成的徽章进度 */
  List<BadgeProgress> getUserCompletedBadgeProgress(Long userId);

  /** 创建或更新徽章进度 */
  BadgeProgress createOrUpdateBadgeProgress(
      Long userId, Long badgeId, Integer progress, Integer target);

  /** 更新徽章进度 */
  BadgeProgress updateBadgeProgress(Long userId, Long badgeId, Integer progress);

  /** 更新徽章进度（带目标值） */
  BadgeProgress updateProgress(
      Long userId, Long badgeId, Integer currentProgress, Integer targetProgress);

  /** 增加徽章进度 */
  BadgeProgress incrementBadgeProgress(Long userId, Long badgeId, Integer increment);

  /** 设置徽章进度 */
  BadgeProgress setBadgeProgress(Long userId, Long badgeId, Integer progress);

  /** 检查徽章是否完成 */
  boolean isBadgeCompleted(Long userId, Long badgeId);

  /** 获取徽章进度百分比 */
  Double getBadgeProgressPercentage(Long userId, Long badgeId);

  /** 获取用户徽章进度统计 */
  Map<String, Object> getUserBadgeProgressStats(Long userId);

  /** 清理已完成的徽章进度 */
  void cleanupCompletedBadgeProgress(Long userId);
}
