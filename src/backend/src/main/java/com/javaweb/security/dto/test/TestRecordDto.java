package com.javaweb.security.dto.test;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 测试记录DTO
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class TestRecordDto {

  private Long id;
  private Long userId;
  private String testName;
  private String categoryCode;
  private String categoryName;
  private Integer totalQuestions;
  private Integer correctAnswers;
  private Integer wrongAnswers;
  private Integer score;
  private Integer maxScore;
  private Double percentage;
  private Integer timeTaken;
  private Integer timeLimit;
  private Boolean isPassed;
  private Integer passThreshold;
  private LocalDateTime startedAt;
  private LocalDateTime completedAt;
  private LocalDateTime createdAt;

  // 答题详情
  private List<TestAnswerDetailDto> answerDetails;
}
