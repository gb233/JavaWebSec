package com.javaweb.security.service.impl;

import com.javaweb.security.entity.UserActivity;
import com.javaweb.security.entity.UserBadge;
import com.javaweb.security.entity.UserProfile;
import com.javaweb.security.repository.AchievementBadgeRepository;
import com.javaweb.security.repository.ChallengeProgressRepository;
import com.javaweb.security.repository.ChallengeScenarioRepository;
import com.javaweb.security.repository.CollectionItemRepository;
import com.javaweb.security.repository.LearningNoteRepository;
import com.javaweb.security.repository.UserActivityRepository;
import com.javaweb.security.repository.UserBadgeRepository;
import com.javaweb.security.repository.UserProfileRepository;
import com.javaweb.security.repository.UserRepository;
import com.javaweb.security.repository.UserTestRecordRepository;
import com.javaweb.security.service.BadgeDetectionService;
import com.javaweb.security.service.BadgeProgressService;
import com.javaweb.security.service.UserService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/** 徽章检测服务实现类 */
@Slf4j
@Service
public class BadgeDetectionServiceImpl implements BadgeDetectionService {

  @Autowired private AchievementBadgeRepository badgeRepository;
  @Autowired private UserBadgeRepository userBadgeRepository;
  @Autowired private BadgeProgressService badgeProgressService;
  @Autowired private UserProfileRepository userProfileRepository;
  @Autowired private ChallengeProgressRepository challengeProgressRepository;
  @Autowired private ChallengeScenarioRepository challengeScenarioRepository;
  @Autowired private UserActivityRepository userActivityRepository;
  @Autowired private LearningNoteRepository learningNoteRepository;
  @Autowired private CollectionItemRepository collectionItemRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private UserTestRecordRepository userTestRecordRepository;
  @Autowired private UserService userService;

  @Override
  public void checkLearningBadges(Long userId, String vulnerabilityCode) {
    try {
      // 检测学习相关徽章
      UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
      if (profile == null) {
        log.warn("用户档案不存在，跳过徽章检测: userId={}", userId);
        return;
      }

      int completedVulnerabilities = profile.getCompletedVulnerabilities();

      // 更新漏洞大师徽章进度
      try {
        updateBadgeProgress(userId, "VULNERABILITY_MASTER", completedVulnerabilities, 10);
      } catch (Exception e) {
        log.error("更新漏洞大师徽章进度失败: userId={}, error={}", userId, e.getMessage(), e);
      }

      // 检测漏洞大师徽章（完成所有A01-A10）
      try {
        if (completedVulnerabilities >= 10
            && !hasUserEarnedBadgeByCode(userId, "VULNERABILITY_MASTER")) {
          awardBadgeToUserByCode(userId, "VULNERABILITY_MASTER");
        }
      } catch (Exception e) {
        log.error("检测漏洞大师徽章失败: userId={}, error={}", userId, e.getMessage(), e);
      }

      // 检测时间相关徽章（夜间学习和早起鸟）
      try {
        checkTimeBasedBadges(userId);
      } catch (Exception e) {
        log.error("检测时间相关徽章失败: userId={}, error={}", userId, e.getMessage(), e);
      }
    } catch (Exception e) {
      log.error(
          "检测学习类徽章失败: userId={}, vulnerabilityCode={}, error={}",
          userId,
          vulnerabilityCode,
          e.getMessage(),
          e);
      // 不抛出异常，避免影响主流程
    }
  }

  @Override
  public void checkTestBadges(Long userId, Integer score, Double accuracy) {
    // 检测测试相关徽章
    UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
    if (profile == null) return;

    int passedTests = profile.getPassedTests();

    // 更新测试大师徽章进度
    updateBadgeProgress(userId, "TEST_MASTER", passedTests, 10);

    // 检测测试大师徽章
    if (passedTests >= 10 && !hasUserEarnedBadgeByCode(userId, "TEST_MASTER")) {
      awardBadgeToUserByCode(userId, "TEST_MASTER");
    }

    // 检测满分达人徽章
    if (score != null && score == 100 && !hasUserEarnedBadgeByCode(userId, "PERFECT_SCORE")) {
      updateBadgeProgress(userId, "PERFECT_SCORE", 1, 1);
      awardBadgeToUserByCode(userId, "PERFECT_SCORE");
    }

    // 检测速度恶魔徽章（快速完成测试，例如：在5分钟内完成）
    // 注意：这里需要从data中获取测试开始和结束时间，暂时先检查是否有快速完成的记录
    checkSpeedDemonBadge(userId, score);
  }

  /** 检测速度恶魔徽章 */
  private void checkSpeedDemonBadge(Long userId, Integer score) {
    // 获取用户的测试记录（从UserTestRecord表获取，包含timeSpent字段）
    // 使用无限制的Pageable获取所有记录
    Pageable unlimitedPageable = PageRequest.of(0, Integer.MAX_VALUE);
    List<com.javaweb.security.entity.UserTestRecord> testRecords =
        userTestRecordRepository
            .findByUserIdOrderByCompletedAtDesc(userId, unlimitedPageable)
            .getContent();

    int fastTestCount = 0;
    for (com.javaweb.security.entity.UserTestRecord record : testRecords) {
      // 检查测试是否在5分钟内完成（300秒）
      // 同时要求正确率>=80%才算快速完成
      if (record.getTimeSpent() != null
          && record.getTimeSpent() > 0
          && record.getTimeSpent() <= 300) { // 5分钟 = 300秒
        // 检查正确率是否>=80%
        if (record.getCompletionRate() != null
            && record.getCompletionRate().doubleValue() >= 80.0) {
          fastTestCount++;
        }
      }
    }

    // 更新速度恶魔徽章进度（快速完成测试次数，目标：5次）
    updateBadgeProgress(userId, "SPEED_DEMON", fastTestCount, 5);
    if (fastTestCount >= 5 && !hasUserEarnedBadgeByCode(userId, "SPEED_DEMON")) {
      awardBadgeToUserByCode(userId, "SPEED_DEMON");
      log.info("用户获得速度恶魔徽章: userId={}, fastTestCount={}", userId, fastTestCount);
    }
  }

  @Override
  public void checkChallengeBadges(Long userId, Long challengeId) {
    // 检测挑战相关徽章
    UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
    if (profile == null) return;

    // 获取用户已完成的挑战数量
    long completedChallenges = challengeProgressRepository.countCompletedByUserId(userId);

    // 获取系统中总挑战数（活跃的挑战场景）
    long totalChallenges = challengeScenarioRepository.findByIsActiveTrue().size();
    if (totalChallenges == 0) {
      totalChallenges = 10; // 默认值，防止除零
    }

    // 更新挑战大师徽章进度（基于总挑战数）
    updateBadgeProgress(
        userId, "CHALLENGE_MASTER", (int) completedChallenges, (int) totalChallenges);

    // 检测挑战大师徽章（完成所有挑战）
    if (completedChallenges >= totalChallenges
        && !hasUserEarnedBadgeByCode(userId, "CHALLENGE_MASTER")) {
      awardBadgeToUserByCode(userId, "CHALLENGE_MASTER");
    }

    // 检测首杀徽章（首次完成挑战）
    if (completedChallenges == 1 && !hasUserEarnedBadgeByCode(userId, "FIRST_BLOOD")) {
      updateBadgeProgress(userId, "FIRST_BLOOD", 1, 1);
      awardBadgeToUserByCode(userId, "FIRST_BLOOD");
      log.info("用户获得首杀徽章: userId={}, challengeId={}", userId, challengeId);
    }

    // 检测完美挑战徽章（检查挑战是否完美完成，例如：所有步骤都一次通过）
    checkPerfectChallengeBadge(userId, challengeId);
  }

  @Override
  public void checkStreakBadges(Long userId) {
    // 检测连续学习徽章
    if (userId == null) {
      log.warn("checkStreakBadges: userId为null，跳过");
      return;
    }

    UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
    if (profile == null) {
      log.warn("用户档案不存在，跳过连续学习徽章检测: userId={}", userId);
      return;
    }

    int currentStreak = profile.getCurrentStreak();

    // 更新连续学习3天徽章进度
    updateBadgeProgress(userId, "LEARNING_STREAK_3", currentStreak, 3);
    if (currentStreak >= 3 && !hasUserEarnedBadgeByCode(userId, "LEARNING_STREAK_3")) {
      awardBadgeToUserByCode(userId, "LEARNING_STREAK_3");
    }

    // 更新连续学习7天徽章进度
    updateBadgeProgress(userId, "LEARNING_STREAK_7", currentStreak, 7);
    if (currentStreak >= 7 && !hasUserEarnedBadgeByCode(userId, "LEARNING_STREAK_7")) {
      awardBadgeToUserByCode(userId, "LEARNING_STREAK_7");
    }

    // 更新连续学习30天徽章进度
    updateBadgeProgress(userId, "LEARNING_STREAK_30", currentStreak, 30);
    if (currentStreak >= 30 && !hasUserEarnedBadgeByCode(userId, "LEARNING_STREAK_30")) {
      awardBadgeToUserByCode(userId, "LEARNING_STREAK_30");
    }
  }

  @Override
  public void checkStudyTimeBadges(Long userId) {
    // 检测学习时长徽章
    if (userId == null) {
      log.warn("checkStudyTimeBadges: userId为null，跳过");
      return;
    }

    UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
    if (profile == null) {
      log.warn("用户档案不存在，跳过学习时长徽章检测: userId={}", userId);
      return;
    }

    long totalStudyTime = profile.getTotalStudyTime(); // 单位：秒

    // 更新学习10小时徽章进度
    updateBadgeProgress(userId, "STUDY_TIME_10", (int) (totalStudyTime / 3600), 10);
    if (totalStudyTime >= 10 * 3600 && !hasUserEarnedBadgeByCode(userId, "STUDY_TIME_10")) {
      awardBadgeToUserByCode(userId, "STUDY_TIME_10");
    }

    // 更新学习50小时徽章进度
    updateBadgeProgress(userId, "STUDY_TIME_50", (int) (totalStudyTime / 3600), 50);
    if (totalStudyTime >= 50 * 3600 && !hasUserEarnedBadgeByCode(userId, "STUDY_TIME_50")) {
      awardBadgeToUserByCode(userId, "STUDY_TIME_50");
    }
  }

  @Override
  public void checkVulnerabilityBadges(Long userId, String vulnerabilityCode) {
    // 检测漏洞学习徽章
    UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
    if (profile == null) return;

    int completedVulnerabilities = profile.getCompletedVulnerabilities();

    // 更新漏洞大师徽章进度
    updateBadgeProgress(userId, "VULNERABILITY_MASTER", completedVulnerabilities, 10);

    // 检测漏洞大师徽章（完成所有A01-A10）
    if (completedVulnerabilities >= 10
        && !hasUserEarnedBadgeByCode(userId, "VULNERABILITY_MASTER")) {
      awardBadgeToUserByCode(userId, "VULNERABILITY_MASTER");
    }
  }

  @Override
  public void checkTestPerformanceBadges(Long userId, Integer score, Double accuracy) {
    // 检测测试表现徽章
    UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
    if (profile == null) return;

    int passedTests = profile.getPassedTests();

    // 更新测试大师徽章进度
    updateBadgeProgress(userId, "TEST_MASTER", passedTests, 10);

    // 检测测试大师徽章
    if (passedTests >= 10 && !hasUserEarnedBadgeByCode(userId, "TEST_MASTER")) {
      awardBadgeToUserByCode(userId, "TEST_MASTER");
    }

    // 检测满分达人徽章
    if (score != null && score == 100 && !hasUserEarnedBadgeByCode(userId, "PERFECT_SCORE")) {
      updateBadgeProgress(userId, "PERFECT_SCORE", 1, 1);
      awardBadgeToUserByCode(userId, "PERFECT_SCORE");
    }
  }

  @Override
  public void checkChallengePerformanceBadges(Long userId, Long challengeId) {
    // 检测挑战表现徽章（与checkChallengeBadges相同逻辑）
    checkChallengeBadges(userId, challengeId);
  }

  @Override
  public void checkSpecialBadges(Long userId, String activityType, Map<String, Object> data) {
    // 检测特殊徽章
    // 根据活动类型和数据检测特殊徽章
    if ("LOGIN".equals(activityType)) {
      // 检测首次登录徽章
      checkFirstLoginBadge(userId);
    } else if ("NOTE_CREATED".equals(activityType)) {
      // 检测笔记达人徽章
      checkNoteTakerBadge(userId);
    } else if ("COLLECTION_ADDED".equals(activityType)) {
      // 检测收藏家徽章
      checkCollectorBadge(userId);
    }
  }

  @Override
  public void checkTimeBasedBadges(Long userId) {
    // 检测时间相关徽章（关键操作，必须成功）
    if (userId == null) {
      throw new IllegalArgumentException("用户ID不能为空");
    }

    // 确保UserProfile存在（关键操作，必须成功）
    UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);
    if (profile == null) {
      log.warn("用户档案不存在，尝试创建: userId={}", userId);
      try {
        // 尝试创建UserProfile
        profile = userService.createUserProfile(userId);
        log.info("用户档案创建成功: userId={}", userId);
      } catch (Exception e) {
        log.error("创建用户档案失败: userId={}, error={}", userId, e.getMessage(), e);
        throw new RuntimeException("创建用户档案失败: userId=" + userId + ", error=" + e.getMessage(), e);
      }
    }

    try {
      checkStreakBadges(userId);
      checkStudyTimeBadges(userId);
      // 检测夜间学习和早起鸟徽章
      checkNightStudyBadges(userId);
      checkEarlyBirdBadge(userId);
      log.debug("检测时间相关徽章成功: userId={}", userId);
    } catch (Exception e) {
      log.error("检测时间相关徽章失败: userId={}, error={}", userId, e.getMessage(), e);
      // 徽章检测是系统功能，失败必须抛出异常
      throw new RuntimeException("检测时间相关徽章失败: userId=" + userId + ", error=" + e.getMessage(), e);
    }
  }

  @Override
  public void checkBehaviorBadges(Long userId, String activityType, Map<String, Object> data) {
    // 检测基于行为的特殊徽章
    // 例如：连续登录、分享、评论等
    checkSpecialBadges(userId, activityType, data);
  }

  /** 检查用户是否已获得徽章（通过徽章代码） */
  private boolean hasUserEarnedBadgeByCode(Long userId, String badgeCode) {
    return badgeRepository
        .findByBadgeCode(badgeCode)
        .map(badge -> userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId()).isPresent())
        .orElse(false);
  }

  /** 颁发徽章给用户（通过徽章代码） */
  private void awardBadgeToUserByCode(Long userId, String badgeCode) {
    try {
      badgeRepository
          .findByBadgeCode(badgeCode)
          .ifPresent(
              badge -> {
                if (!userBadgeRepository
                    .findByUserIdAndBadgeId(userId, badge.getId())
                    .isPresent()) {
                  try {
                    UserBadge userBadge = UserBadge.fromAchievementBadge(userId, badge);
                    userBadgeRepository.save(userBadge);
                    log.info("用户获得徽章: userId={}, badgeCode={}", userId, badgeCode);
                  } catch (Exception e) {
                    log.error(
                        "保存用户徽章失败: userId={}, badgeCode={}, error={}",
                        userId,
                        badgeCode,
                        e.getMessage(),
                        e);
                    // 不抛出异常，避免影响主流程
                  }
                }
              });
    } catch (Exception e) {
      log.error("颁发徽章失败: userId={}, badgeCode={}, error={}", userId, badgeCode, e.getMessage(), e);
      // 不抛出异常，避免影响主流程
    }
  }

  /** 更新徽章进度 */
  private void updateBadgeProgress(
      Long userId, String badgeCode, int currentProgress, int targetProgress) {
    badgeRepository
        .findByBadgeCode(badgeCode)
        .ifPresent(
            badge -> {
              badgeProgressService.updateProgress(
                  userId, badge.getId(), currentProgress, targetProgress);
            });
  }

  /** 检测首次登录徽章 */
  public void checkFirstLoginBadge(Long userId) {
    // 检查用户是否已获得首次登录徽章
    if (hasUserEarnedBadgeByCode(userId, "FIRST_LOGIN")) {
      log.debug("用户已获得首次登录徽章，跳过检测: userId={}", userId);
      return; // 已经获得，不需要再次检测
    }

    // 注意：这个方法应该在updateLastLoginInfo之前调用
    // 调用者已经检查了isFirstLogin，所以这里直接颁发徽章
    // 为了确保正确性，再次检查lastLoginAt（使用数据库查询，避免缓存问题）
    userRepository
        .findById(userId)
        .ifPresent(
            user -> {
              // 如果lastLoginAt为null，说明是首次登录
              if (user.getLastLoginAt() == null) {
                updateBadgeProgress(userId, "FIRST_LOGIN", 1, 1);
                awardBadgeToUserByCode(userId, "FIRST_LOGIN");
                log.info("用户获得首次登录徽章: userId={}", userId);
              } else {
                log.warn(
                    "检测首次登录徽章时，lastLoginAt不为null: userId={}, lastLoginAt={}",
                    userId,
                    user.getLastLoginAt());
              }
            });
  }

  /** 检测夜间学习徽章（22:00-06:00） */
  private void checkNightStudyBadges(Long userId) {
    // 获取用户的所有学习活动
    List<UserActivity> learningActivities =
        userActivityRepository.findByUserIdAndActivityType(
            userId, UserActivity.ActivityType.LEARNING);

    int nightStudyCount = 0;
    for (UserActivity activity : learningActivities) {
      LocalDateTime createdAt = activity.getCreatedAt();
      if (createdAt != null) {
        LocalTime time = createdAt.toLocalTime();
        int hour = time.getHour();
        // 夜间时间：22:00-23:59 或 00:00-05:59
        if (hour >= 22 || hour < 6) {
          nightStudyCount++;
        }
      }
    }

    // 更新夜猫子徽章进度（累计夜间学习次数，目标：10次）
    updateBadgeProgress(userId, "NIGHT_OWL", nightStudyCount, 10);
    if (nightStudyCount >= 10 && !hasUserEarnedBadgeByCode(userId, "NIGHT_OWL")) {
      awardBadgeToUserByCode(userId, "NIGHT_OWL");
      log.info("用户获得夜猫子徽章: userId={}, nightStudyCount={}", userId, nightStudyCount);
    }
  }

  /** 检测早起鸟徽章（06:00-08:00） */
  private void checkEarlyBirdBadge(Long userId) {
    // 获取用户的所有学习活动
    List<UserActivity> learningActivities =
        userActivityRepository.findByUserIdAndActivityType(
            userId, UserActivity.ActivityType.LEARNING);

    int earlyBirdCount = 0;
    for (UserActivity activity : learningActivities) {
      LocalDateTime createdAt = activity.getCreatedAt();
      if (createdAt != null) {
        LocalTime time = createdAt.toLocalTime();
        int hour = time.getHour();
        // 早起时间：06:00-08:00
        if (hour >= 6 && hour < 8) {
          earlyBirdCount++;
        }
      }
    }

    // 更新早起鸟徽章进度（累计早起学习次数，目标：10次）
    updateBadgeProgress(userId, "EARLY_BIRD", earlyBirdCount, 10);
    if (earlyBirdCount >= 10 && !hasUserEarnedBadgeByCode(userId, "EARLY_BIRD")) {
      awardBadgeToUserByCode(userId, "EARLY_BIRD");
      log.info("用户获得早起鸟徽章: userId={}, earlyBirdCount={}", userId, earlyBirdCount);
    }
  }

  /** 检测笔记达人徽章 */
  private void checkNoteTakerBadge(Long userId) {
    long noteCount = learningNoteRepository.countByUserId(userId);

    // 更新笔记达人徽章进度（创建笔记数量，目标：10篇）
    updateBadgeProgress(userId, "NOTE_TAKER", (int) noteCount, 10);
    if (noteCount >= 10 && !hasUserEarnedBadgeByCode(userId, "NOTE_TAKER")) {
      awardBadgeToUserByCode(userId, "NOTE_TAKER");
      log.info("用户获得笔记达人徽章: userId={}, noteCount={}", userId, noteCount);
    }
  }

  /** 检测收藏家徽章 */
  private void checkCollectorBadge(Long userId) {
    long collectionCount = collectionItemRepository.findByUserId(userId).size();

    // 更新收藏家徽章进度（收藏内容数量，目标：20个）
    updateBadgeProgress(userId, "COLLECTOR", (int) collectionCount, 20);
    if (collectionCount >= 20 && !hasUserEarnedBadgeByCode(userId, "COLLECTOR")) {
      awardBadgeToUserByCode(userId, "COLLECTOR");
      log.info("用户获得收藏家徽章: userId={}, collectionCount={}", userId, collectionCount);
    }
  }

  /** 检测完美挑战徽章 */
  private void checkPerfectChallengeBadge(Long userId, Long challengeId) {
    // 检查挑战是否完美完成（例如：没有重置，一次完成所有步骤）
    challengeProgressRepository
        .findByUserIdAndScenarioId(userId, challengeId)
        .ifPresent(
            progress -> {
              if (progress.getIsCompleted() != null && progress.getIsCompleted()) {
                // 简单判断：如果挑战完成且进度为100%，认为是完美完成
                if (progress.getProgressPercentage() != null
                    && progress.getProgressPercentage().compareTo(java.math.BigDecimal.valueOf(100))
                        == 0) {
                  // 检查是否已获得完美挑战徽章
                  if (!hasUserEarnedBadgeByCode(userId, "PERFECT_CHALLENGE")) {
                    // 统计完美完成的挑战数量
                    long perfectChallenges =
                        challengeProgressRepository.findByUserIdAndIsCompletedTrue(userId).stream()
                            .filter(
                                p ->
                                    p.getProgressPercentage() != null
                                        && p.getProgressPercentage()
                                                .compareTo(java.math.BigDecimal.valueOf(100))
                                            == 0)
                            .count();

                    updateBadgeProgress(userId, "PERFECT_CHALLENGE", (int) perfectChallenges, 5);
                    if (perfectChallenges >= 5) {
                      awardBadgeToUserByCode(userId, "PERFECT_CHALLENGE");
                      log.info(
                          "用户获得完美挑战徽章: userId={}, perfectChallenges={}", userId, perfectChallenges);
                    }
                  }
                }
              }
            });
  }
}
