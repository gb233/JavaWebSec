package com.javaweb.security.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Data;

/**
 * 用户测试记录DTO
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
public class UserTestRecordDto {
  private Long id;
  private Long userId;
  private Long sessionId;
  private String modeCode;
  private String categoryCode;
  private Integer totalScore;
  private Integer correctCount;
  private Integer totalQuestions;
  private BigDecimal completionRate;
  private Integer timeSpent;
  private Timestamp startedAt;
  private Timestamp completedAt;
}
