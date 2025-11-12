package com.javaweb.security.dto;

import java.sql.Timestamp;
import lombok.Data;

/**
 * 测试会话DTO
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
public class TestSessionDto {
  private Long id;
  private Long userId;
  private String modeCode;
  private String categoryCode;
  private String sessionCode;
  private String status;
  private Integer currentQuestionIndex;
  private Integer totalQuestions;
  private Integer answeredQuestions;
  private Integer correctAnswers;
  private Integer totalScore;
  private Timestamp startTime;
  private Timestamp endTime;
  private Timestamp createdAt;
}
