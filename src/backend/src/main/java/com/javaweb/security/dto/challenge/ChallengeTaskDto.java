package com.javaweb.security.dto.challenge;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 挑战任务DTO
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class ChallengeTaskDto {

  private Long id;
  private String title;
  private String description;
  private String categoryCode;
  private String categoryName;
  private String difficultyLevel;
  private Integer points;
  private String hint;
  private String solution;
  private Boolean isActive;
  private String createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // 用户相关状态
  private Boolean isCompleted;
  private Integer userAttempts;
  private LocalDateTime completedAt;
}
