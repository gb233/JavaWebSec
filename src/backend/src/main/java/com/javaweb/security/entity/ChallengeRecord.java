package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 挑战记录实体
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Entity
@Table(name = "challenge_records")
public class ChallengeRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "task_id", nullable = false)
  private Long taskId;

  @Column(name = "submitted_flag", nullable = false, length = 100)
  private String submittedFlag;

  @Column(name = "is_correct", nullable = false)
  private Boolean isCorrect;

  @Column(name = "points_earned", nullable = false)
  private Integer pointsEarned;

  @Column(name = "attempts", nullable = false)
  private Integer attempts = 1;

  @Column(name = "submitted_at", nullable = false)
  private LocalDateTime submittedAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
