package com.javaweb.security.dto.test;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 测试答题详情DTO
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class TestAnswerDetailDto {

  private Long id;
  private Long testRecordId;
  private Long questionId;
  private String userAnswer;
  private String correctAnswer;
  private Boolean isCorrect;
  private Integer pointsEarned;
  private Integer timeSpent;
  private LocalDateTime createdAt;

  // 题目信息（用于显示）
  private String questionText;
  private String questionType;
  private String explanation;
}
