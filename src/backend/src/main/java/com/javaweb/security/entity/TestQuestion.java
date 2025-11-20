package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 测试题目实体
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "test_questions")
public class TestQuestion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 漏洞分类ID（外键关联到 vulnerability_categories 表） */
  @Column(name = "vulnerability_category_id", nullable = false)
  private Long vulnerabilityCategoryId;

  @Column(name = "category_code", nullable = false, length = 20)
  private String categoryCode;

  @Column(name = "category_name", nullable = false, length = 100)
  private String categoryName;

  /** 题目标题 */
  @Column(name = "question_title", nullable = false, length = 500)
  private String questionTitle;

  /** 题目内容 */
  @Column(name = "question_content", nullable = false, columnDefinition = "TEXT")
  private String questionContent;

  /** 代码示例 */
  @Column(name = "question_code", columnDefinition = "LONGTEXT")
  private String questionCode;

  @Column(name = "question_type", nullable = false, length = 20)
  private String questionType; // single_choice, multiple_choice, true_false, fill_blank

  @Column(name = "options", columnDefinition = "JSON")
  private String options; // JSON格式存储选项

  @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
  private String correctAnswer;

  @Column(name = "explanation", columnDefinition = "TEXT")
  private String explanation;

  @Column(name = "difficulty_level", nullable = false, length = 20)
  private String difficultyLevel; // easy, medium, hard

  @Column(name = "points", nullable = false)
  private Integer points = 1;

  @Column(name = "time_limit")
  private Integer timeLimit; // 答题时间限制（秒）

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Column(name = "usage_count")
  private Integer usageCount = 0;

  @Column(name = "correct_rate", precision = 5, scale = 2)
  private java.math.BigDecimal correctRate = java.math.BigDecimal.ZERO;

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
