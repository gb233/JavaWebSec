package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 徽章要求实体类 */
@Entity
@Table(name = "badge_requirements")
public class BadgeRequirement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "badge_id", nullable = false)
  private Long badgeId;

  @Column(name = "requirement_type", nullable = false)
  private String requirementType;

  @Column(name = "requirement_value", nullable = false)
  private String requirementValue;

  @Column(name = "requirement_description", columnDefinition = "TEXT")
  private String requirementDescription;

  @Column(name = "sort_order")
  private Integer sortOrder = 0;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // 构造函数
  public BadgeRequirement() {}

  public BadgeRequirement(
      Long badgeId,
      String requirementType,
      String requirementValue,
      String requirementDescription,
      Integer sortOrder) {
    this.badgeId = badgeId;
    this.requirementType = requirementType;
    this.requirementValue = requirementValue;
    this.requirementDescription = requirementDescription;
    this.sortOrder = sortOrder;
    this.createdAt = LocalDateTime.now();
  }

  // Getter和Setter方法
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getBadgeId() {
    return badgeId;
  }

  public void setBadgeId(Long badgeId) {
    this.badgeId = badgeId;
  }

  public String getRequirementType() {
    return requirementType;
  }

  public void setRequirementType(String requirementType) {
    this.requirementType = requirementType;
  }

  public String getRequirementValue() {
    return requirementValue;
  }

  public void setRequirementValue(String requirementValue) {
    this.requirementValue = requirementValue;
  }

  public String getRequirementDescription() {
    return requirementDescription;
  }

  public void setRequirementDescription(String requirementDescription) {
    this.requirementDescription = requirementDescription;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
