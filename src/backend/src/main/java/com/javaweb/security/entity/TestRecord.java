package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 测试记录实体
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "test_records")
public class TestRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "test_name", nullable = false, length = 100)
  private String testName;

  @Column(name = "category_code", length = 20)
  private String categoryCode;

  @Column(name = "category_name", length = 100)
  private String categoryName;

  @Column(name = "total_questions", nullable = false)
  private Integer totalQuestions;

  @Column(name = "correct_answers", nullable = false)
  private Integer correctAnswers;

  @Column(name = "wrong_answers", nullable = false)
  private Integer wrongAnswers;

  @Column(name = "score", nullable = false)
  private Integer score;

  @Column(name = "max_score", nullable = false)
  private Integer maxScore;

  @Column(name = "percentage", nullable = false)
  private Double percentage;

  @Column(name = "time_taken", nullable = false)
  private Integer timeTaken; // 答题用时（秒）

  @Column(name = "time_limit")
  private Integer timeLimit; // 时间限制（秒）

  @Column(name = "is_passed", nullable = false)
  private Boolean isPassed;

  @Column(name = "pass_threshold", nullable = false)
  private Integer passThreshold = 60; // 及格线（百分比）

  @Column(name = "started_at", nullable = false)
  private LocalDateTime startedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
