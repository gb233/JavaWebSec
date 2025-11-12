package com.javaweb.security.service.impl;

import com.javaweb.security.dto.UserBadgeDto;
import com.javaweb.security.entity.AchievementBadge;
import com.javaweb.security.entity.BadgeProgress;
import com.javaweb.security.entity.UserBadge;
import com.javaweb.security.entity.UserProfile;
import com.javaweb.security.repository.AchievementBadgeRepository;
import com.javaweb.security.repository.BadgeProgressRepository;
import com.javaweb.security.repository.UserBadgeRepository;
import com.javaweb.security.repository.UserProfileRepository;
import com.javaweb.security.service.BadgeDetectionService;
import com.javaweb.security.service.BadgeNotificationService;
import com.javaweb.security.service.BadgeProgressService;
import com.javaweb.security.service.BadgeService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 徽章服务实现类 */
@Service
@Transactional
public class BadgeServiceImpl implements BadgeService {

  @Autowired private AchievementBadgeRepository badgeRepository;

  @Autowired private UserBadgeRepository userBadgeRepository;

  @Autowired private BadgeProgressRepository badgeProgressRepository;

  @Autowired private UserProfileRepository userProfileRepository;

  @Autowired private BadgeNotificationService notificationService;

  @Autowired private BadgeProgressService badgeProgressService;

  @Autowired private BadgeDetectionService badgeDetectionService;

  @Override
  public List<AchievementBadge> getAllBadges() {
    return badgeRepository.findByIsActiveTrue();
  }

  @Override
  public List<AchievementBadge> getBadgesByCategory(String category) {
    return badgeRepository.findByBadgeCategoryAndIsActiveTrue(category);
  }

  @Override
  public AchievementBadge getBadgeByCode(String badgeCode) {
    return badgeRepository.findByBadgeCode(badgeCode).orElse(null);
  }

  @Override
  public List<UserBadge> getUserBadges(Long userId) {
    return userBadgeRepository.findByUserId(userId);
  }

  @Override
  public List<UserBadge> getUserBadgesByCategory(Long userId, String category) {
    return userBadgeRepository.findByUserIdAndCategory(userId, category);
  }

  @Override
  public boolean hasUserEarnedBadge(Long userId, Long badgeId) {
    return userBadgeRepository.existsByUserIdAndBadgeId(userId, badgeId);
  }

  @Override
  public boolean hasUserEarnedBadgeByCode(Long userId, String badgeCode) {
    AchievementBadge badge = getBadgeByCode(badgeCode);
    if (badge == null) {
      return false;
    }
    return hasUserEarnedBadge(userId, badge.getId());
  }

  @Override
  public UserBadge awardBadgeToUser(Long userId, Long badgeId) {
    if (hasUserEarnedBadge(userId, badgeId)) {
      return userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId).orElse(null);
    }

    AchievementBadge badge = badgeRepository.findById(badgeId).orElse(null);
    if (badge == null) {
      return null;
    }

    UserBadge userBadge = UserBadge.fromAchievementBadge(userId, badge);
    userBadge = userBadgeRepository.save(userBadge);

    // 获取徽章信息并更新用户积分
    updateUserPointsFromBadge(userId, badge.getPointsReward());

    // 发送通知
    notificationService.notifyBadgeEarned(userId, badge);

    return userBadge;
  }

  @Override
  public UserBadge awardBadgeToUserByCode(Long userId, String badgeCode) {
    AchievementBadge badge = getBadgeByCode(badgeCode);
    if (badge == null) {
      return null;
    }
    return awardBadgeToUser(userId, badge.getId());
  }

  @Override
  public List<BadgeProgress> getUserBadgeProgress(Long userId) {
    return badgeProgressService.getUserBadgeProgress(userId);
  }

  @Override
  public List<BadgeProgress> getUserBadgeProgressByCategory(Long userId, String category) {
    return badgeProgressService.getUserBadgeProgressByCategory(userId, category);
  }

  @Override
  public BadgeProgress updateBadgeProgress(Long userId, Long badgeId, Integer progress) {
    return badgeProgressService.updateBadgeProgress(userId, badgeId, progress);
  }

  @Override
  public void checkAndAwardBadges(Long userId, String activityType, Map<String, Object> data) {
    // 根据活动类型调用相应的徽章检测服务
    switch (activityType) {
      case "LEARNING":
        String vulnerabilityCode = (String) data.get("vulnerabilityCode");
        badgeDetectionService.checkLearningBadges(userId, vulnerabilityCode);
        break;
      case "TEST":
        Integer score = (Integer) data.get("score");
        Double accuracy = (Double) data.get("accuracy");
        badgeDetectionService.checkTestBadges(userId, score, accuracy);
        break;
      case "CHALLENGE":
        Long challengeId = (Long) data.get("challengeId");
        badgeDetectionService.checkChallengeBadges(userId, challengeId);
        break;
      default:
        badgeDetectionService.checkSpecialBadges(userId, activityType, data);
        break;
    }
  }

  @Override
  public Map<String, Object> getUserBadgeStats(Long userId) {
    Map<String, Object> stats = new HashMap<>();

    // 总徽章数
    long totalBadges = userBadgeRepository.countByUserId(userId);
    stats.put("totalBadges", totalBadges);

    // 按分类统计
    stats.put("learningBadges", userBadgeRepository.countByUserIdAndCategory(userId, "LEARNING"));
    stats.put("testBadges", userBadgeRepository.countByUserIdAndCategory(userId, "TEST"));
    stats.put("challengeBadges", userBadgeRepository.countByUserIdAndCategory(userId, "CHALLENGE"));
    stats.put("specialBadges", userBadgeRepository.countByUserIdAndCategory(userId, "SPECIAL"));

    // 最近获得的徽章（包含详细信息）
    List<UserBadge> recentUserBadges = userBadgeRepository.findByUserIdOrderByEarnedAtDesc(userId);
    List<UserBadgeDto> recentBadges = new ArrayList<>();

    for (UserBadge userBadge : recentUserBadges) {
      Optional<AchievementBadge> badgeOpt = badgeRepository.findById(userBadge.getBadgeId());
      if (badgeOpt.isPresent()) {
        AchievementBadge badge = badgeOpt.get();
        UserBadgeDto dto =
            UserBadgeDto.from(
                userBadge,
                badge.getBadgeCode(),
                badge.getBadgeName(),
                badge.getBadgeDescription(),
                badge.getBadgeIcon(),
                badge.getBadgeCategory(),
                badge.getBadgeRarity(),
                badge.getPointsReward());
        recentBadges.add(dto);
      }
    }

    stats.put("recentBadges", recentBadges);

    // 计算徽章积分总和
    int totalBadgePoints = calculateUserBadgePoints(userId);
    stats.put("totalBadgePoints", totalBadgePoints);

    // 进度统计
    Map<String, Object> progressStats = badgeProgressService.getUserBadgeProgressStats(userId);
    stats.put("progressStats", progressStats);

    return stats;
  }

  @Override
  public List<UserBadge> getUserRecentBadges(Long userId, int limit) {
    List<UserBadge> recentBadges = userBadgeRepository.findByUserIdOrderByEarnedAtDesc(userId);
    if (recentBadges.size() > limit) {
      return recentBadges.subList(0, limit);
    }
    return recentBadges;
  }

  /**
   * 更新用户积分（从徽章获得）
   *
   * @param userId 用户ID
   * @param points 积分数量
   */
  private void updateUserPointsFromBadge(Long userId, Integer points) {
    if (points == null || points <= 0) {
      return;
    }

    Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);
    if (profileOpt.isPresent()) {
      UserProfile profile = profileOpt.get();
      profile.addPoints(points);
      userProfileRepository.save(profile);
    }
  }

  /**
   * 计算用户所有徽章的积分总和
   *
   * @param userId 用户ID
   * @return 徽章积分总和
   */
  private int calculateUserBadgePoints(Long userId) {
    List<UserBadge> userBadges = userBadgeRepository.findByUserId(userId);
    int totalPoints = 0;

    for (UserBadge userBadge : userBadges) {
      Optional<AchievementBadge> badgeOpt = badgeRepository.findById(userBadge.getBadgeId());
      if (badgeOpt.isPresent()) {
        AchievementBadge badge = badgeOpt.get();
        if (badge.getPointsReward() != null) {
          totalPoints += badge.getPointsReward();
        }
      }
    }

    return totalPoints;
  }
}
