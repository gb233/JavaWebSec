package com.javaweb.security.dto;

import com.javaweb.security.entity.UserBadge;
import lombok.Data;

/** 用户徽章DTO 包含用户徽章记录和徽章详细信息 */
@Data
public class UserBadgeDto {

  // 用户徽章记录信息
  private Long id;
  private Long userId;
  private Long badgeId;
  private String earnedAt;
  private Boolean isDisplayed;
  private String createdAt;

  // 徽章详细信息
  private String badgeCode;
  private String badgeName;
  private String badgeDescription;
  private String badgeIcon;
  private String badgeCategory;
  private String badgeRarity;
  private Integer pointsReward;

  /** 从UserBadge实体和徽章信息创建DTO */
  public static UserBadgeDto from(
      UserBadge userBadge,
      String badgeCode,
      String badgeName,
      String badgeDescription,
      String badgeIcon,
      String badgeCategory,
      String badgeRarity,
      Integer pointsReward) {
    UserBadgeDto dto = new UserBadgeDto();

    // 复制用户徽章记录信息
    dto.setId(userBadge.getId());
    dto.setUserId(userBadge.getUserId());
    dto.setBadgeId(userBadge.getBadgeId());
    dto.setEarnedAt(userBadge.getEarnedAt() != null ? userBadge.getEarnedAt().toString() : null);
    dto.setIsDisplayed(userBadge.getIsDisplayed());
    dto.setCreatedAt(userBadge.getCreatedAt() != null ? userBadge.getCreatedAt().toString() : null);

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
