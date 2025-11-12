package com.javaweb.security.dto;

import java.sql.Timestamp;
import lombok.Data;

/**
 * 测试答案DTO
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
public class TestAnswerDto {
  private Long id;
  private Long sessionId;
  private Long questionId;
  private Integer questionIndex;
  private String userAnswer;
  private Boolean isCorrect;
  private Integer score;
  private Boolean feedbackShown;
  private Timestamp answeredAt;
}
