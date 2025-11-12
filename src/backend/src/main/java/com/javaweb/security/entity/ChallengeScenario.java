package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 挑战场景配置实体类
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Entity
@Table(name = "challenge_scenarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChallengeScenario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "scenario_name", unique = true, nullable = false, length = 100)
  private String scenarioName;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "vulnerability_chain", columnDefinition = "JSON", nullable = false)
  private String vulnerabilityChain;

  @Column(name = "difficulty_level", nullable = false, length = 20)
  private String difficultyLevel;

  @Column(name = "estimated_time", nullable = false)
  private Integer estimatedTime;

  @Column(name = "points", nullable = false)
  private Integer points;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
