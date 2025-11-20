package com.javaweb.security.service.impl;

import com.javaweb.security.entity.UserActivity;
import com.javaweb.security.entity.UserProfile;
import com.javaweb.security.entity.UserVulnerabilityProgress;
import com.javaweb.security.enums.VulnerabilityStatus;
import com.javaweb.security.repository.UserActivityRepository;
import com.javaweb.security.repository.UserProfileRepository;
import com.javaweb.security.repository.UserVulnerabilityProgressRepository;
import com.javaweb.security.service.BadgeDetectionService;
import com.javaweb.security.service.UserService;
import com.javaweb.security.service.UserStatsUpdateService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户统计更新服务实现
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatsUpdateServiceImpl implements UserStatsUpdateService {

  private final UserProfileRepository userProfileRepository;
  private final UserVulnerabilityProgressRepository userVulnerabilityProgressRepository;
  private final UserActivityRepository userActivityRepository;
  private final BadgeDetectionService badgeDetectionService;
  private final UserService userService;

  @Override
  @Transactional
  public void updateVulnerabilityStats(
      Long userId, String vulnerabilityCode, Integer studyTime, Integer points) {
    log.info(
        "更新用户漏洞学习统计: userId={}, vulnerabilityCode={}, studyTime={}, points={}",
        userId,
        vulnerabilityCode,
        studyTime,
        points);

    // 更新用户档案（确保UserProfile存在，关键操作，必须成功）
    UserProfile profile;
    Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
    if (profileOpt.isPresent()) {
      profile = profileOpt.get();
    } else {
      // UserProfile不存在，必须创建成功
      log.warn("用户档案不存在，尝试创建: userId={}", userId);
      try {
        profile = userService.createUserProfile(userId);
        log.info("用户档案创建成功: userId={}", userId);
        // 验证创建是否成功
        Optional<UserProfile> createdProfile = userProfileRepository.findByUserId(userId);
        if (!createdProfile.isPresent()) {
          throw new RuntimeException("UserProfile创建后验证失败: userId=" + userId);
        }
      } catch (Exception e) {
        log.error("创建用户档案失败: userId={}, error={}", userId, e.getMessage(), e);
        // 创建失败必须抛出异常，这是关键操作
        throw new RuntimeException("创建用户档案失败: userId=" + userId + ", error=" + e.getMessage(), e);
      }
    }

    // 更新UserProfile统计（关键操作，必须成功）
    try {
      profile.addStudyTime(studyTime);
      profile.addPoints(points);
      userProfileRepository.save(profile);
      log.debug("用户档案统计更新成功: userId={}, studyTime={}, points={}", userId, studyTime, points);
    } catch (Exception e) {
      log.error("更新用户档案失败: userId={}, error={}", userId, e.getMessage(), e);
      // 更新失败必须抛出异常，这是关键操作
      throw new RuntimeException("更新用户档案失败: userId=" + userId + ", error=" + e.getMessage(), e);
    }

    // 更新漏洞学习进度（关键操作，必须成功）
    try {
      Optional<UserVulnerabilityProgress> progressOpt =
          userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
              userId, vulnerabilityCode);
      if (progressOpt.isPresent()) {
        UserVulnerabilityProgress progress = progressOpt.get();
        progress.setTotalStudyTime(progress.getTotalStudyTime() + studyTime);
        progress.setLearningScore(progress.getLearningScore() + points);
        progress.setStatus(VulnerabilityStatus.IN_PROGRESS);
        if (progress.getStartedAt() == null) {
          progress.setStartedAt(LocalDateTime.now());
        }
        userVulnerabilityProgressRepository.save(progress);
      } else {
        // 创建新的学习进度记录
        UserVulnerabilityProgress progress = new UserVulnerabilityProgress();
        progress.setUserId(userId);
        progress.setVulnerabilityCode(vulnerabilityCode);
        progress.setStatus(VulnerabilityStatus.IN_PROGRESS);
        progress.setTotalStudyTime(studyTime);
        progress.setLearningScore(points);
        progress.setStartedAt(LocalDateTime.now());
        userVulnerabilityProgressRepository.save(progress);
      }
    } catch (Exception e) {
      log.error(
          "更新漏洞学习进度失败: userId={}, vulnerabilityCode={}, error={}",
          userId,
          vulnerabilityCode,
          e.getMessage(),
          e);
      // 更新失败必须抛出异常，这是关键操作
      throw new RuntimeException(
          "更新漏洞学习进度失败: userId="
              + userId
              + ", vulnerabilityCode="
              + vulnerabilityCode
              + ", error="
              + e.getMessage(),
          e);
    }

    // 检测学习类徽章（关键操作，必须成功）
    try {
      badgeDetectionService.checkLearningBadges(userId, vulnerabilityCode);
      log.debug("检测学习类徽章成功: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);
    } catch (Exception e) {
      log.error(
          "检测学习类徽章失败: userId={}, vulnerabilityCode={}, error={}",
          userId,
          vulnerabilityCode,
          e.getMessage(),
          e);
      // 徽章检测是系统功能，失败必须抛出异常
      throw new RuntimeException(
          "检测学习类徽章失败: userId="
              + userId
              + ", vulnerabilityCode="
              + vulnerabilityCode
              + ", error="
              + e.getMessage(),
          e);
    }
  }

  @Override
  @Transactional
  public void updateTestStats(
      Long userId, Long testId, Boolean passed, Integer score, Integer points) {
    log.info(
        "更新用户测试统计: userId={}, testId={}, passed={}, score={}, points={}",
        userId,
        testId,
        passed,
        score,
        points);

    // 更新用户档案
    Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
    if (profileOpt.isPresent()) {
      UserProfile profile = profileOpt.get();
      if (passed) {
        profile.incrementPassedTests();
      }
      profile.addPoints(points);
      userProfileRepository.save(profile);
    }

    // 更新漏洞学习进度
    // 这里需要根据testId找到对应的vulnerabilityCode
    // 暂时使用通用逻辑
    updateVulnerabilityProgress(userId, "TEST", passed, score, points);

    // 检测测试类徽章
    double accuracy = passed ? 1.0 : 0.0;
    badgeDetectionService.checkTestBadges(userId, score, accuracy);
  }

  @Override
  @Transactional
  public void updateChallengeStats(
      Long userId, Long challengeId, Boolean completed, Integer points, String badge) {
    log.info(
        "更新用户挑战统计: userId={}, challengeId={}, completed={}, points={}, badge={}",
        userId,
        challengeId,
        completed,
        points,
        badge);

    // 更新用户档案
    Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
    if (profileOpt.isPresent()) {
      UserProfile profile = profileOpt.get();
      if (completed) {
        profile.incrementCompletedChallenges();
      }
      profile.addPoints(points);
      if (badge != null && !badge.isEmpty()) {
        profile.incrementEarnedBadges();
      }
      userProfileRepository.save(profile);
    }

    // 更新漏洞学习进度
    // 这里需要根据challengeId找到对应的vulnerabilityCode
    // 暂时使用通用逻辑
    updateVulnerabilityProgress(userId, "CHALLENGE", completed, points, points);

    // 检测挑战类徽章
    if (completed) {
      try {
        badgeDetectionService.checkChallengeBadges(userId, challengeId);
      } catch (Exception e) {
        log.error(
            "检测挑战类徽章失败: userId={}, challengeId={}, error={}",
            userId,
            challengeId,
            e.getMessage(),
            e);
        // 不抛出异常，避免影响挑战统计更新
      }
    }
  }

  @Override
  @Transactional
  public void updateStudyTimeStats(Long userId, Integer additionalTime) {
    log.info("更新用户学习时长统计: userId={}, additionalTime={}", userId, additionalTime);

    Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
    if (profileOpt.isPresent()) {
      UserProfile profile = profileOpt.get();
      profile.addStudyTime(additionalTime);
      userProfileRepository.save(profile);
    }
  }

  @Override
  @Transactional
  public void updateStreakStats(Long userId) {
    log.info("更新用户连续学习天数: userId={}", userId);
    updateLearningStreak(userId);
  }

  /** 每日凌晨执行，更新所有用户的连续学习天数 */
  @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨0点执行
  @Transactional
  public void dailyUpdateAllUserStreaks() {
    log.info("开始每日连续学习天数更新任务...");

    List<UserProfile> allProfiles = userProfileRepository.findAll();
    for (UserProfile profile : allProfiles) {
      try {
        updateLearningStreak(profile.getUserId());
      } catch (Exception e) {
        log.error("更新用户 {} 的连续学习天数失败: {}", profile.getUserId(), e.getMessage(), e);
      }
    }

    log.info("每日连续学习天数更新任务完成，共处理 {} 个用户", allProfiles.size());
  }

  /**
   * 更新用户的连续学习天数
   *
   * @param userId 用户ID
   */
  @Transactional
  public void updateLearningStreak(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("用户ID不能为空");
    }

    log.debug("更新用户连续学习天数: userId={}", userId);

    // 确保UserProfile存在，如果不存在则创建（关键操作，必须成功）
    UserProfile profile;
    Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
    if (profileOpt.isPresent()) {
      profile = profileOpt.get();
    } else {
      // UserProfile不存在，必须创建成功
      log.warn("用户档案不存在，自动创建: userId={}", userId);
      try {
        profile = userService.createUserProfile(userId);
        log.info("用户档案创建成功: userId={}", userId);
        // 验证创建是否成功
        Optional<UserProfile> createdProfile = userProfileRepository.findByUserId(userId);
        if (!createdProfile.isPresent()) {
          throw new RuntimeException("UserProfile创建后验证失败: userId=" + userId);
        }
      } catch (Exception e) {
        log.error("创建用户档案失败: userId={}, error={}", userId, e.getMessage(), e);
        throw new RuntimeException("创建用户档案失败: userId=" + userId + ", error=" + e.getMessage(), e);
      }
    }

    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);

    // 获取用户最近的活动记录（最近7天）（关键操作，必须成功）
    List<UserActivity> recentActivities;
    try {
      recentActivities =
          userActivityRepository.findByUserIdAndCreatedAtAfter(
              userId, LocalDateTime.now().minusDays(7));
    } catch (Exception e) {
      log.error("查询用户活动记录失败: userId={}, error={}", userId, e.getMessage(), e);
      // 查询失败必须抛出异常，这是关键操作
      throw new RuntimeException("查询用户活动记录失败: userId=" + userId + ", error=" + e.getMessage(), e);
    }

    if (recentActivities.isEmpty()) {
      // 用户没有任何活动记录，重置连续天数（关键操作，必须成功）
      try {
        profile.updateStreak(0);
        userProfileRepository.save(profile);
        log.debug("用户 {} 无活动记录，连续天数重置为0", userId);
        return;
      } catch (Exception e) {
        log.error("保存用户档案失败: userId={}, error={}", userId, e.getMessage(), e);
        // 保存失败必须抛出异常，这是关键操作
        throw new RuntimeException("保存用户档案失败: userId=" + userId + ", error=" + e.getMessage(), e);
      }
    }

    // 检查今天是否有活动
    boolean hasTodayActivity =
        recentActivities.stream()
            .anyMatch(
                activity ->
                    activity.getCreatedAt() != null
                        && activity.getCreatedAt().toLocalDate().isEqual(today));

    // 检查昨天是否有活动
    boolean hasYesterdayActivity =
        recentActivities.stream()
            .anyMatch(
                activity ->
                    activity.getCreatedAt() != null
                        && activity.getCreatedAt().toLocalDate().isEqual(yesterday));

    int currentStreak = profile.getCurrentStreak();

    if (hasTodayActivity) {
      // 今天有活动
      if (hasYesterdayActivity) {
        // 昨天也有活动，连续天数+1
        profile.updateStreak(currentStreak + 1);
        log.info("用户 {} 今天和昨天都有活动，连续天数从 {} 增加到 {}", userId, currentStreak, currentStreak + 1);
      } else if (currentStreak == 0) {
        // 今天有活动但昨天没有，且当前连续天数为0，则重置为1
        profile.updateStreak(1);
        log.info("用户 {} 今天有活动但昨天没有，连续天数重置为1", userId);
      } else {
        // 今天有活动但昨天没有，且当前连续天数不为0，说明中断了，重置为1
        profile.updateStreak(1);
        log.info("用户 {} 今天有活动但昨天没有，连续天数从 {} 重置为1", userId, currentStreak);
      }
    } else {
      // 今天没有活动
      if (hasYesterdayActivity) {
        // 昨天有活动但今天没有，连续天数重置为0
        profile.updateStreak(0);
        log.info("用户 {} 昨天有活动但今天没有，连续天数重置为0", userId);
      } else {
        // 今天和昨天都没有活动，保持当前连续天数不变（或重置为0）
        if (currentStreak > 0) {
          profile.updateStreak(0);
          log.info("用户 {} 今天和昨天都没有活动，连续天数重置为0", userId);
        }
      }
    }

    // 保存用户档案（关键操作，必须成功）
    try {
      userProfileRepository.save(profile);
      log.debug("用户连续学习天数更新成功: userId={}, currentStreak={}", userId, profile.getCurrentStreak());
    } catch (Exception e) {
      log.error("保存用户档案失败: userId={}, error={}", userId, e.getMessage(), e);
      // 保存失败必须抛出异常，这是关键操作
      throw new RuntimeException("保存用户档案失败: userId=" + userId + ", error=" + e.getMessage(), e);
    }
  }

  /** 更新漏洞学习进度 */
  private void updateVulnerabilityProgress(
      Long userId, String vulnerabilityCode, Boolean completed, Integer score, Integer points) {
    Optional<UserVulnerabilityProgress> progressOpt =
        userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
            userId, vulnerabilityCode);
    if (progressOpt.isPresent()) {
      UserVulnerabilityProgress progress = progressOpt.get();
      if (completed) {
        progress.setStatus(VulnerabilityStatus.COMPLETED);
        progress.setCompletedAt(LocalDateTime.now());
      }
      progress.setTestScore(progress.getTestScore() + score);
      userVulnerabilityProgressRepository.save(progress);
    }
  }
}
