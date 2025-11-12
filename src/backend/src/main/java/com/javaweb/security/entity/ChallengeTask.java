package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 挑战任务实体
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "challenge_tasks")
public class ChallengeTask {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "challenge_name", nullable = false, length = 100, unique = true)
  private String challengeName;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "category_code", nullable = false, length = 20)
  private String categoryCode;

  @Column(name = "category_name", nullable = false, length = 100)
  private String categoryName;

  @Column(name = "difficulty_level", nullable = false, length = 20)
  private String difficultyLevel; // easy, medium, hard, expert

  @Column(name = "points", nullable = false)
  private Integer points;

  @Column(name = "flag", nullable = false, length = 100)
  private String flag; // 挑战的Flag

  @Column(name = "hint", columnDefinition = "TEXT")
  private String hint; // 提示信息

  @Column(name = "solution", columnDefinition = "TEXT")
  private String solution; // 解题思路

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
