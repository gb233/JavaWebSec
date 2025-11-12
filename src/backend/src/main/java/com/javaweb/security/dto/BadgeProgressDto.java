package com.javaweb.security.dto;

import com.javaweb.security.entity.BadgeProgress;
import lombok.Data;

/** 徽章进度DTO 包含徽章进度信息和徽章详细信息 */
@Data
public class BadgeProgressDto {

  // 徽章进度信息
  private Long id;
  private Long userId;
  private Long badgeId;
  private Integer currentProgress;
  private Integer targetProgress;
  private Double progressPercentage;
  private Boolean isCompleted;
  private String createdAt;
  private String updatedAt;

  // 徽章详细信息
  private String badgeCode;
  private String badgeName;
  private String badgeDescription;
  private String badgeIcon;
  private String badgeCategory;
  private String badgeRarity;
  private Integer pointsReward;

  /** 从BadgeProgress实体和徽章信息创建DTO */
  public static BadgeProgressDto from(
      BadgeProgress progress,
      String badgeCode,
      String badgeName,
      String badgeDescription,
      String badgeIcon,
      String badgeCategory,
      String badgeRarity,
      Integer pointsReward) {
    BadgeProgressDto dto = new BadgeProgressDto();

    // 复制进度信息
    dto.setId(progress.getId());
    dto.setUserId(progress.getUserId());
    dto.setBadgeId(progress.getBadgeId());
    dto.setCurrentProgress(progress.getCurrentProgress());
    dto.setTargetProgress(progress.getTargetProgress());
    dto.setProgressPercentage(progress.getProgressPercentage().doubleValue());
    dto.setIsCompleted(progress.getIsCompleted());
    dto.setCreatedAt(progress.getCreatedAt() != null ? progress.getCreatedAt().toString() : null);
    dto.setUpdatedAt(progress.getUpdatedAt() != null ? progress.getUpdatedAt().toString() : null);

    // 设置徽章信息
    dto.setBadgeCode(badgeCode);
    dto.setBadgeName(badgeName);
    dto.setBadgeDescription(badgeDescription);
    dto.setBadgeIcon(badgeIcon);
    dto.setBadgeCategory(badgeCategory);
    dto.setBadgeRarity(badgeRarity);
    dto.setPointsReward(pointsReward);

    return dto;
  }
}
