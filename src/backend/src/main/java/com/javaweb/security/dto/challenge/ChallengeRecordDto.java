package com.javaweb.security.dto.challenge;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 挑战记录DTO
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class ChallengeRecordDto {

  private Long id;
  private Long userId;
  private Long taskId;
  private String submittedFlag;
  private Boolean isCorrect;
  private Integer pointsEarned;
  private Integer attempts;
  private LocalDateTime submittedAt;
  private LocalDateTime createdAt;

  // 任务信息
  private String taskTitle;
  private String categoryName;
  private String difficultyLevel;
}
