package com.javaweb.security.dto.test;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 测试题目DTO
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class TestQuestionDto {

  private Long id;
  private String categoryCode;
  private String categoryName;
  private String questionText;
  private String questionType;
  private List<String> options;
  private String correctAnswer;
  private String explanation;
  private String difficultyLevel;
  private Integer points;
  private Integer timeLimit;
  private Boolean isActive;
  private Long createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
