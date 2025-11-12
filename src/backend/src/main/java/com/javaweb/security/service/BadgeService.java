package com.javaweb.security.service;

import com.javaweb.security.entity.AchievementBadge;
import com.javaweb.security.entity.BadgeProgress;
import com.javaweb.security.entity.UserBadge;
import java.util.List;
import java.util.Map;

/** 徽章服务接口 */
public interface BadgeService {

  /** 获取所有徽章 */
  List<AchievementBadge> getAllBadges();

  /** 根据分类获取徽章 */
  List<AchievementBadge> getBadgesByCategory(String category);

  /** 根据徽章代码获取徽章 */
  AchievementBadge getBadgeByCode(String badgeCode);

  /** 获取用户徽章 */
  List<UserBadge> getUserBadges(Long userId);

  /** 根据分类获取用户徽章 */
  List<UserBadge> getUserBadgesByCategory(Long userId, String category);

  /** 检查用户是否已获得徽章 */
  boolean hasUserEarnedBadge(Long userId, Long badgeId);

  /** 检查用户是否已获得徽章（通过徽章代码） */
  boolean hasUserEarnedBadgeByCode(Long userId, String badgeCode);

  /** 颁发徽章给用户 */
  UserBadge awardBadgeToUser(Long userId, Long badgeId);

  /** 颁发徽章给用户（通过徽章代码） */
  UserBadge awardBadgeToUserByCode(Long userId, String badgeCode);

  /** 获取用户徽章进度 */
  List<BadgeProgress> getUserBadgeProgress(Long userId);

  /** 根据分类获取用户徽章进度 */
  List<BadgeProgress> getUserBadgeProgressByCategory(Long userId, String category);

  /** 更新用户徽章进度 */
  BadgeProgress updateBadgeProgress(Long userId, Long badgeId, Integer progress);

  /** 检查并颁发徽章 */
  void checkAndAwardBadges(Long userId, String activityType, Map<String, Object> data);

  /** 获取用户徽章统计 */
  Map<String, Object> getUserBadgeStats(Long userId);

  /** 获取用户最近获得的徽章 */
  List<UserBadge> getUserRecentBadges(Long userId, int limit);
}
