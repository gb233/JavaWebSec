package com.javaweb.security.service.impl;

import com.javaweb.security.dto.BadgeProgressDto;
import com.javaweb.security.entity.AchievementBadge;
import com.javaweb.security.entity.BadgeProgress;
import com.javaweb.security.repository.AchievementBadgeRepository;
import com.javaweb.security.repository.BadgeProgressRepository;
import com.javaweb.security.service.BadgeNotificationService;
import com.javaweb.security.service.BadgeProgressService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 徽章进度服务实现类 */
@Service
@Transactional
public class BadgeProgressServiceImpl implements BadgeProgressService {

  @Autowired private BadgeProgressRepository badgeProgressRepository;

  @Autowired private BadgeNotificationService notificationService;

  @Autowired private AchievementBadgeRepository achievementBadgeRepository;

  @Override
  public List<BadgeProgress> getUserBadgeProgress(Long userId) {
    return badgeProgressRepository.findByUserId(userId);
  }

  /** 获取用户徽章进度（包含徽章详细信息） */
  public List<BadgeProgressDto> getUserBadgeProgressWithDetails(Long userId) {
    List<BadgeProgress> progressList = badgeProgressRepository.findByUserId(userId);
    List<BadgeProgressDto> result = new ArrayList<>();

    for (BadgeProgress progress : progressList) {
      Optional<AchievementBadge> badgeOpt =
          achievementBadgeRepository.findById(progress.getBadgeId());
      if (badgeOpt.isPresent()) {
        AchievementBadge badge = badgeOpt.get();
        BadgeProgressDto dto =
            BadgeProgressDto.from(
                progress,
                badge.getBadgeCode(),
                badge.getBadgeName(),
                badge.getBadgeDescription(),
                badge.getBadgeIcon(),
                badge.getBadgeCategory(),
                badge.getBadgeRarity(),
                badge.getPointsReward());
        result.add(dto);
      }
    }

    return result;
  }

  @Override
  public List<BadgeProgress> getUserBadgeProgressByCategory(Long userId, String category) {
    return badgeProgressRepository.findByUserIdAndCategory(userId, category);
  }

  @Override
  public List<BadgeProgress> getUserUncompletedBadgeProgress(Long userId) {
    return badgeProgressRepository.findByUserIdAndIsCompletedFalse(userId);
  }

  @Override
  public List<BadgeProgress> getUserCompletedBadgeProgress(Long userId) {
    return badgeProgressRepository.findByUserIdAndIsCompletedTrue(userId);
  }

  @Override
  public BadgeProgress createOrUpdateBadgeProgress(
      Long userId, Long badgeId, Integer progress, Integer target) {
    BadgeProgress badgeProgress =
        badgeProgressRepository
            .findByUserIdAndBadgeId(userId, badgeId)
            .orElse(new BadgeProgress(userId, badgeId, target));

    badgeProgress.setCurrentProgress(progress);
    badgeProgress.setTargetProgress(target);
    badgeProgress.setUpdatedAt(LocalDateTime.now());

    badgeProgress = badgeProgressRepository.save(badgeProgress);

    // 发送进度更新通知
    notificationService.notifyBadgeProgressUpdate(userId, badgeId, progress, target);

    // 检查是否即将完成
    if (progress >= target * 0.8 && progress < target) {
      notificationService.notifyBadgeNearCompletion(userId, badgeId, progress, target);
    }

    return badgeProgress;
  }

  @Override
  public BadgeProgress updateBadgeProgress(Long userId, Long badgeId, Integer progress) {
    BadgeProgress badgeProgress =
        badgeProgressRepository.findByUserIdAndBadgeId(userId, badgeId).orElse(null);

    if (badgeProgress != null) {
      badgeProgress.setCurrentProgress(progress);
      badgeProgress.setUpdatedAt(LocalDateTime.now());
      badgeProgress = badgeProgressRepository.save(badgeProgress);

      // 发送进度更新通知
      notificationService.notifyBadgeProgressUpdate(
          userId, badgeId, progress, badgeProgress.getTargetProgress());
    }

    return badgeProgress;
  }

  @Override
  public BadgeProgress incrementBadgeProgress(Long userId, Long badgeId, Integer increment) {
    BadgeProgress badgeProgress =
        badgeProgressRepository.findByUserIdAndBadgeId(userId, badgeId).orElse(null);

    if (badgeProgress != null) {
      int newProgress = badgeProgress.getCurrentProgress() + increment;
      badgeProgress.setCurrentProgress(newProgress);
      badgeProgress.setUpdatedAt(LocalDateTime.now());
      badgeProgress = badgeProgressRepository.save(badgeProgress);

      // 发送进度更新通知
      notificationService.notifyBadgeProgressUpdate(
          userId, badgeId, newProgress, badgeProgress.getTargetProgress());
    }

    return badgeProgress;
  }

  @Override
  public BadgeProgress setBadgeProgress(Long userId, Long badgeId, Integer progress) {
    return updateBadgeProgress(userId, badgeId, progress);
  }

  @Override
  public boolean isBadgeCompleted(Long userId, Long badgeId) {
    BadgeProgress badgeProgress =
        badgeProgressRepository.findByUserIdAndBadgeId(userId, badgeId).orElse(null);
    return badgeProgress != null && badgeProgress.getIsCompleted();
  }

  @Override
  public Double getBadgeProgressPercentage(Long userId, Long badgeId) {
    BadgeProgress badgeProgress =
        badgeProgressRepository.findByUserIdAndBadgeId(userId, badgeId).orElse(null);
    if (badgeProgress != null) {
      return badgeProgress.getProgressPercentage().doubleValue();
    }
    return 0.0;
  }

  @Override
  public Map<String, Object> getUserBadgeProgressStats(Long userId) {
    Map<String, Object> stats = new HashMap<>();

    // 总进度数
    long totalProgress = badgeProgressRepository.countByUserIdAndIsCompletedFalse(userId);
    stats.put("totalProgress", totalProgress);

    // 已完成进度数
    long completedProgress = badgeProgressRepository.countByUserIdAndIsCompletedTrue(userId);
    stats.put("completedProgress", completedProgress);

    // 按分类统计
    stats.put(
        "learningProgress",
        badgeProgressRepository.countUncompletedByUserIdAndCategory(userId, "LEARNING"));
    stats.put(
        "testProgress",
        badgeProgressRepository.countUncompletedByUserIdAndCategory(userId, "TEST"));
    stats.put(
        "challengeProgress",
        badgeProgressRepository.countUncompletedByUserIdAndCategory(userId, "CHALLENGE"));
    stats.put(
        "specialProgress",
        badgeProgressRepository.countUncompletedByUserIdAndCategory(userId, "SPECIAL"));

    return stats;
  }

  @Override
  public BadgeProgress updateProgress(
      Long userId, Long badgeId, Integer currentProgress, Integer targetProgress) {
    // 更新徽章进度（带目标值）
    BadgeProgress progress =
        badgeProgressRepository
            .findByUserIdAndBadgeId(userId, badgeId)
            .orElse(new BadgeProgress(userId, badgeId, targetProgress));

    progress.setCurrentProgress(currentProgress);
    progress.setTargetProgress(targetProgress);

    // 计算进度百分比
    if (targetProgress != null && targetProgress > 0) {
      BigDecimal percentage =
          BigDecimal.valueOf(currentProgress)
              .divide(BigDecimal.valueOf(targetProgress), 2, BigDecimal.ROUND_HALF_UP)
              .multiply(BigDecimal.valueOf(100));
      progress.setProgressPercentage(percentage);

      // 检查是否完成
      progress.setIsCompleted(currentProgress >= targetProgress);
    }

    return badgeProgressRepository.save(progress);
  }

  @Override
  public void cleanupCompletedBadgeProgress(Long userId) {
    // 清理已完成的徽章进度
    List<BadgeProgress> completedProgress =
        badgeProgressRepository.findByUserIdAndIsCompletedTrue(userId);
    badgeProgressRepository.deleteAll(completedProgress);
  }
}
