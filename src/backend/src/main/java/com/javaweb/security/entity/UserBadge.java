package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 用户徽章记录实体类 */
@Entity
@Table(name = "user_badges")
public class UserBadge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "badge_id", nullable = false)
  private Long badgeId;

  @Column(name = "earned_at")
  private LocalDateTime earnedAt;

  @Column(name = "is_displayed")
  private Boolean isDisplayed = true;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "badge_type")
  private String badgeType;

  @Column(name = "badge_name")
  private String badgeName;

  @Column(name = "badge_description", columnDefinition = "TEXT")
  private String badgeDescription;

  @Column(name = "badge_icon")
  private String badgeIcon;

  @Column(name = "badge_level")
  private String badgeLevel;

  @Column(name = "badge_category")
  private String badgeCategory;

  @Column(name = "earn_condition", columnDefinition = "TEXT")
  private String earnCondition;

  @Column(name = "bonus_points")
  private Integer bonusPoints;

  @Column(name = "rarity")
  private String rarity;

  // 构造函数
  public UserBadge() {}

  @Deprecated
  public UserBadge(Long userId, Long badgeId) {
    this.userId = userId;
    this.badgeId = badgeId;
    this.badgeType = "LEGACY_" + badgeId;
    this.badgeName = "LEGACY_BADGE_" + badgeId;
    this.badgeDescription = "Auto generated placeholder";
    this.badgeLevel = "bronze";
    this.badgeCategory = "LEGACY";
    this.earnCondition = "Auto generated placeholder";
    this.bonusPoints = 0;
    this.rarity = "common";
    this.earnedAt = LocalDateTime.now();
    this.createdAt = LocalDateTime.now();
  }

  public static UserBadge fromAchievementBadge(Long userId, AchievementBadge badge) {
    UserBadge userBadge = new UserBadge();
    userBadge.userId = userId;
    userBadge.badgeId = badge.getId();
    userBadge.badgeType = badge.getBadgeCode();
    userBadge.badgeName = badge.getBadgeName();
    userBadge.badgeDescription = badge.getBadgeDescription();
    userBadge.badgeIcon = badge.getBadgeIcon();
    userBadge.badgeCategory = badge.getBadgeCategory();
    userBadge.badgeLevel = mapBadgeLevel(badge.getBadgeRarity());
    userBadge.earnCondition =
        badge.getRequirements() != null ? badge.getRequirements() : badge.getBadgeDescription();
    userBadge.bonusPoints = badge.getPointsReward();
    userBadge.rarity = normalizeRarity(badge.getBadgeRarity());
    userBadge.earnedAt = LocalDateTime.now();
    userBadge.createdAt = LocalDateTime.now();
    userBadge.isDisplayed = true;
    return userBadge;
  }

  private static String mapBadgeLevel(String badgeRarity) {
    if (badgeRarity == null) {
      return "bronze";
    }
    switch (badgeRarity.toUpperCase()) {
      case "COMMON":
        return "bronze";
      case "RARE":
        return "silver";
      case "EPIC":
        return "gold";
      case "LEGENDARY":
        return "diamond";
      default:
        return "bronze";
    }
  }

  private static String normalizeRarity(String badgeRarity) {
    return badgeRarity != null ? badgeRarity.toLowerCase() : "common";
  }

  // Getter和Setter方法
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getBadgeId() {
    return badgeId;
  }

  public void setBadgeId(Long badgeId) {
    this.badgeId = badgeId;
  }

  public LocalDateTime getEarnedAt() {
    return earnedAt;
  }

  public void setEarnedAt(LocalDateTime earnedAt) {
    this.earnedAt = earnedAt;
  }

  public Boolean getIsDisplayed() {
    return isDisplayed;
  }

  public void setIsDisplayed(Boolean isDisplayed) {
    this.isDisplayed = isDisplayed;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getBadgeType() {
    return badgeType;
  }

  public void setBadgeType(String badgeType) {
    this.badgeType = badgeType;
  }

  public String getBadgeName() {
    return badgeName;
  }

  public void setBadgeName(String badgeName) {
    this.badgeName = badgeName;
  }

  public String getBadgeDescription() {
    return badgeDescription;
  }

  public void setBadgeDescription(String badgeDescription) {
    this.badgeDescription = badgeDescription;
  }

  public String getBadgeIcon() {
    return badgeIcon;
  }

  public void setBadgeIcon(String badgeIcon) {
    this.badgeIcon = badgeIcon;
  }

  public String getBadgeLevel() {
    return badgeLevel;
  }

  public void setBadgeLevel(String badgeLevel) {
    this.badgeLevel = badgeLevel;
  }

  public String getBadgeCategory() {
    return badgeCategory;
  }

  public void setBadgeCategory(String badgeCategory) {
    this.badgeCategory = badgeCategory;
  }

  public String getEarnCondition() {
    return earnCondition;
  }

  public void setEarnCondition(String earnCondition) {
    this.earnCondition = earnCondition;
  }

  public Integer getBonusPoints() {
    return bonusPoints;
  }

  public void setBonusPoints(Integer bonusPoints) {
    this.bonusPoints = bonusPoints;
  }

  public String getRarity() {
    return rarity;
  }

  public void setRarity(String rarity) {
    this.rarity = rarity;
  }
}
