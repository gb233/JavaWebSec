package com.javaweb.security.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.persistence.*;

/** 徽章进度实体类 */
@Entity
@Table(name = "badge_progress")
public class BadgeProgress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "badge_id", nullable = false)
  private Long badgeId;

  @Column(name = "current_progress")
  private Integer currentProgress = 0;

  @Column(name = "target_progress", nullable = false)
  private Integer targetProgress;

  @Column(name = "progress_percentage", precision = 5, scale = 2)
  private BigDecimal progressPercentage = BigDecimal.ZERO;

  @Column(name = "is_completed")
  private Boolean isCompleted = false;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // 构造函数
  public BadgeProgress() {}

  public BadgeProgress(Long userId, Long badgeId, Integer targetProgress) {
    this.userId = userId;
    this.badgeId = badgeId;
    this.targetProgress = targetProgress;
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

  public Integer getCurrentProgress() {
    return currentProgress;
  }

  public void setCurrentProgress(Integer currentProgress) {
    this.currentProgress = currentProgress;
    updateProgressPercentage();
  }

  public Integer getTargetProgress() {
    return targetProgress;
  }

  public void setTargetProgress(Integer targetProgress) {
    this.targetProgress = targetProgress;
    updateProgressPercentage();
  }

  public BigDecimal getProgressPercentage() {
    return progressPercentage;
  }

  public void setProgressPercentage(BigDecimal progressPercentage) {
    this.progressPercentage = progressPercentage;
  }

  public Boolean getIsCompleted() {
    return isCompleted;
  }

  public void setIsCompleted(Boolean isCompleted) {
    this.isCompleted = isCompleted;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /** 更新进度百分比 */
  private void updateProgressPercentage() {
    if (targetProgress != null && targetProgress > 0) {
      BigDecimal percentage =
          BigDecimal.valueOf(currentProgress)
              .divide(BigDecimal.valueOf(targetProgress), 2, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));

      // 确保百分比不超过100，避免数据库字段溢出（DECIMAL(5,2)最大值为999.99，但百分比应该限制在100以内）
      if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
        this.progressPercentage = BigDecimal.valueOf(100);
      } else {
        this.progressPercentage = percentage;
      }

      // 检查是否完成
      this.isCompleted = currentProgress >= targetProgress;
    }
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
    updateProgressPercentage();
  }
}
