package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 测试答题详情实体
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "test_answer_details")
public class TestAnswerDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_record_id", nullable = false)
  private Long testRecordId;

  @Column(name = "question_id", nullable = false)
  private Long questionId;

  @Column(name = "question_order", nullable = false)
  private Integer questionOrder;

  @Column(name = "user_answer", columnDefinition = "TEXT")
  private String userAnswer;

  @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
  private String correctAnswer;

  @Column(name = "is_correct", nullable = false)
  private Boolean isCorrect;

  @Column(name = "points_earned", nullable = false)
  private Integer pointsEarned;

  @Column(name = "time_spent")
  private Integer timeSpent; // 该题用时（秒）

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
