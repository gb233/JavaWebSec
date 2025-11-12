package com.javaweb.security.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.entity.UserActivity;
import com.javaweb.security.entity.UserActivity.ActivityType;
import com.javaweb.security.entity.UserProfile;
import com.javaweb.security.repository.UserActivityRepository;
import com.javaweb.security.repository.UserProfileRepository;
import com.javaweb.security.service.BadgeDetectionService;
import com.javaweb.security.service.UserActivityService;
import com.javaweb.security.service.UserStatsUpdateService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户活动记录服务实现
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {

  private final UserActivityRepository userActivityRepository;
  private final UserProfileRepository userProfileRepository;
  private final ObjectMapper objectMapper;
  private final BadgeDetectionService badgeDetectionService;
  private final UserStatsUpdateService userStatsUpdateService;

  @Override
  @Transactional
  public void recordActivity(
      Long userId,
      ActivityType activityType,
      String vulnerabilityCode,
      String title,
      String description,
      Map<String, Object> metadata) {
    log.info(
        "记录用户活动: userId={}, activityType={}, vulnerabilityCode={}, title={}",
        userId,
        activityType,
        vulnerabilityCode,
        title);

    UserActivity activity = new UserActivity();
    activity.setUserId(userId);
    activity.setActivityType(activityType);
    activity.setVulnerabilityCode(vulnerabilityCode);
    activity.setTitle(title);
    activity.setDescription(description);
    activity.setCreatedAt(LocalDateTime.now());

    try {
      activity.setMetadata(objectMapper.writeValueAsString(metadata));
    } catch (JsonProcessingException e) {
      log.error("序列化活动元数据失败", e);
    }

    userActivityRepository.save(activity);

    // 如果是学习活动，检测时间相关徽章（夜间学习和早起鸟）
    if (activityType == ActivityType.LEARNING) {
      try {
        badgeDetectionService.checkTimeBasedBadges(userId);
        log.debug("检测时间相关徽章: userId={}, activityType={}", userId, activityType);
      } catch (Exception e) {
        log.error("检测时间相关徽章失败: userId={}, error={}", userId, e.getMessage(), e);
        // 不抛出异常，避免影响活动记录流程
      }
    }

    // 记录活动后，更新连续学习天数
    try {
      userStatsUpdateService.updateStreakStats(userId);
      log.debug("更新连续学习天数: userId={}", userId);
    } catch (Exception e) {
      log.error("更新连续学习天数失败: userId={}, error={}", userId, e.getMessage(), e);
      // 不抛出异常，避免影响活动记录流程
    }
  }

  @Override
  public List<UserActivity> getRecentActivities(Long userId, int limit) {
    log.info("获取用户最近活动: userId={}, limit={}", userId, limit);

    Pageable pageable = PageRequest.of(0, limit);
    return userActivityRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
  }

  @Override
  public Map<String, Object> getActivityStatistics(Long userId) {
    log.info("获取用户活动统计: userId={}", userId);

    Map<String, Object> statistics = new HashMap<>();

    // 获取各类型活动数量
    List<Object[]> activityCounts = userActivityRepository.countByUserIdAndActivityType(userId);
    for (Object[] count : activityCounts) {
      ActivityType activityType = (ActivityType) count[0];
      Long countValue = (Long) count[1];
      statistics.put(activityType.name().toLowerCase() + "Count", countValue);
    }

    // 获取最近7天活动
    LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
    List<UserActivity> recentActivities =
        userActivityRepository.findByUserIdAndCreatedAtAfter(userId, sevenDaysAgo);
    statistics.put("recentActivities", recentActivities);
    statistics.put("recentActivityCount", recentActivities.size());

    // 动态计算用户学习时长
    int totalStudyTime = calculateTotalStudyTime(userId);
    statistics.put("totalStudyTime", totalStudyTime);

    // 获取用户档案信息
    Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
    if (profileOpt.isPresent()) {
      UserProfile profile = profileOpt.get();
      statistics.put("totalPoints", profile.getTotalPoints());
      statistics.put("currentStreak", profile.getCurrentStreak());
      statistics.put("longestStreak", profile.getLongestStreak());

      // 更新UserProfile中的学习时长以保持数据一致性
      if (profile.getTotalStudyTime() != (long) totalStudyTime) {
        profile.setTotalStudyTime((long) totalStudyTime);
        userProfileRepository.save(profile);
        log.info("更新用户学习时长: userId={}, 新时长={}分钟", userId, totalStudyTime);
      }
    }

    return statistics;
  }

  @Override
  @Transactional
  public void recordLearningCompleted(
      Long userId, String vulnerabilityCode, Integer studyTime, Integer score) {
    log.info(
        "记录学习完成活动: userId={}, vulnerabilityCode={}, studyTime={}, score={}",
        userId,
        vulnerabilityCode,
        studyTime,
        score);

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("studyTime", studyTime);
    metadata.put("score", score);
    metadata.put("vulnerabilityCode", vulnerabilityCode);

    recordActivity(
        userId,
        ActivityType.LEARNING,
        vulnerabilityCode,
        "完成漏洞学习: " + vulnerabilityCode,
        "成功完成了 " + vulnerabilityCode + " 漏洞的学习，学习时长: " + studyTime + " 分钟，得分: " + score,
        metadata);

    // 检测时间相关徽章（夜间学习和早起鸟）
    try {
      badgeDetectionService.checkTimeBasedBadges(userId);
      log.debug("检测学习时间相关徽章: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);
    } catch (Exception e) {
      log.error("检测学习时间相关徽章失败: userId={}, error={}", userId, e.getMessage(), e);
      // 不抛出异常，避免影响活动记录流程
    }
  }

  @Override
  @Transactional
  public void recordTestPassed(
      Long userId, String vulnerabilityCode, Integer score, Double accuracy) {
    log.info(
        "记录测试通过活动: userId={}, vulnerabilityCode={}, score={}, accuracy={}",
        userId,
        vulnerabilityCode,
        score,
        accuracy);

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("score", score);
    metadata.put("accuracy", accuracy);
    metadata.put("vulnerabilityCode", vulnerabilityCode);

    recordActivity(
        userId,
        ActivityType.TEST,
        vulnerabilityCode,
        "通过漏洞测试: " + vulnerabilityCode,
        "成功通过了 "
            + vulnerabilityCode
            + " 漏洞的测试，得分: "
            + score
            + "，正确率: "
            + String.format("%.1f%%", accuracy * 100),
        metadata);
  }

  @Override
  @Transactional
  public void recordChallengeCompleted(
      Long userId, String vulnerabilityCode, Integer score, String badge) {
    log.info(
        "记录挑战完成活动: userId={}, vulnerabilityCode={}, score={}, badge={}",
        userId,
        vulnerabilityCode,
        score,
        badge);

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("score", score);
    metadata.put("badge", badge);
    metadata.put("vulnerabilityCode", vulnerabilityCode);

    String title = "完成漏洞挑战: " + vulnerabilityCode;
    String description = "成功完成了 " + vulnerabilityCode + " 漏洞的挑战，得分: " + score;
    if (badge != null && !badge.isEmpty()) {
      description += "，获得徽章: " + badge;
    }

    recordActivity(userId, ActivityType.CHALLENGE, vulnerabilityCode, title, description, metadata);
  }

  /** 动态计算用户总学习时长 基于user_activities表中的学习活动记录 */
  private int calculateTotalStudyTime(Long userId) {
    log.info("计算用户学习时长: userId={}", userId);

    // 获取用户的所有学习活动
    List<UserActivity> learningActivities =
        userActivityRepository.findByUserIdAndActivityType(userId, ActivityType.LEARNING);

    int totalStudyTime = 0;
    for (UserActivity activity : learningActivities) {
      try {
        // 从metadata中提取studyTime
        String metadataJson = activity.getMetadata();
        if (metadataJson != null && !metadataJson.isEmpty()) {
          @SuppressWarnings("unchecked")
          Map<String, Object> metadata = objectMapper.readValue(metadataJson, Map.class);
          Object studyTimeObj = metadata.get("studyTime");
          if (studyTimeObj != null) {
            int studyTime = Integer.parseInt(studyTimeObj.toString());
            totalStudyTime += studyTime;
            log.debug("活动ID={}, 学习时长={}分钟", activity.getId(), studyTime);
          }
        }
      } catch (Exception e) {
        log.warn("解析学习活动metadata失败: activityId={}, error={}", activity.getId(), e.getMessage());
      }
    }

    log.info("用户总学习时长: userId={}, totalStudyTime={}分钟", userId, totalStudyTime);
    return totalStudyTime;
  }
}
