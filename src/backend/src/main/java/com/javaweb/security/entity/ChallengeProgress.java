package com.javaweb.security.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 挑战进度实体类
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Entity
@Table(name = "challenge_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChallengeProgress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "scenario_id", nullable = false)
  private Long scenarioId;

  @Column(name = "current_step")
  private Integer currentStep = 0;

  @Column(name = "completed_steps", columnDefinition = "JSON")
  private String completedSteps;

  @Column(name = "progress_percentage", precision = 5, scale = 2)
  private BigDecimal progressPercentage = BigDecimal.ZERO;

  @Column(name = "is_completed")
  private Boolean isCompleted = false;

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @PrePersist
  protected void onCreate() {
    if (startedAt == null) {
      startedAt = LocalDateTime.now();
    }
  }
}
