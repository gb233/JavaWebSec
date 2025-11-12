package com.javaweb.security.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试答案响应DTO
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerResponseDto {

  /** 答案ID */
  private Long id;

  /** 会话ID */
  private Long sessionId;

  /** 题目ID */
  private Long questionId;

  /** 题目在会话中的索引 */
  private Integer questionIndex;

  /** 用户答案 */
  private String userAnswer;

  /** 是否正确 */
  private Boolean isCorrect;

  /** 得分 */
  private Integer score;

  /** 是否已显示反馈 */
  private Boolean feedbackShown;

  /** 答题时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime answeredAt;

  /** 正确答案 */
  private String correctAnswer;

  /** 解析 */
  private String explanation;

  /** 是否已答题 */
  private Boolean answered;
}
