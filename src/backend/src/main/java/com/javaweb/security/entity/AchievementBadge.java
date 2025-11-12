package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 成就徽章实体类 */
@Entity
@Table(name = "achievement_badges")
public class AchievementBadge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "badge_code", unique = true, nullable = false)
  private String badgeCode;

  @Column(name = "badge_name", nullable = false)
  private String badgeName;

  @Column(name = "badge_description", columnDefinition = "TEXT")
  private String badgeDescription;

  @Column(name = "badge_icon")
  private String badgeIcon;

  @Column(name = "badge_category", nullable = false)
  private String badgeCategory;

  @Column(name = "badge_rarity")
  private String badgeRarity = "COMMON";

  @Column(name = "requirements", columnDefinition = "JSON")
  private String requirements;

  @Column(name = "points_reward")
  private Integer pointsReward = 0;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 构造函数
  public AchievementBadge() {}

  public AchievementBadge(
      String badgeCode,
      String badgeName,
      String badgeDescription,
      String badgeIcon,
      String badgeCategory,
      String badgeRarity,
      Integer pointsReward) {
    this.badgeCode = badgeCode;
    this.badgeName = badgeName;
    this.badgeDescription = badgeDescription;
    this.badgeIcon = badgeIcon;
    this.badgeCategory = badgeCategory;
    this.badgeRarity = badgeRarity;
    this.pointsReward = pointsReward;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // Getter和Setter方法
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBadgeCode() {
    return badgeCode;
  }

  public void setBadgeCode(String badgeCode) {
    this.badgeCode = badgeCode;
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

  public String getBadgeCategory() {
    return badgeCategory;
  }

  public void setBadgeCategory(String badgeCategory) {
    this.badgeCategory = badgeCategory;
  }

  public String getBadgeRarity() {
    return badgeRarity;
  }

  public void setBadgeRarity(String badgeRarity) {
    this.badgeRarity = badgeRarity;
  }

  public String getRequirements() {
    return requirements;
  }

  public void setRequirements(String requirements) {
    this.requirements = requirements;
  }

  public Integer getPointsReward() {
    return pointsReward;
  }

  public void setPointsReward(Integer pointsReward) {
    this.pointsReward = pointsReward;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
