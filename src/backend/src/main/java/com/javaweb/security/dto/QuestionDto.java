package com.javaweb.security.dto;

import java.sql.Timestamp;
import lombok.Data;

/**
 * 题目DTO
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
public class QuestionDto {
  private Long id;
  private String categoryCode;
  private String questionType;
  private String difficulty;
  private String knowledgeSource;
  private String questionText;
  private String questionImage;
  private String options;
  private String correctAnswer;
  private String explanation;
  private Integer score;
  private String tags;
  private Long authorId;
  private String status;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
