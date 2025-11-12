package com.javaweb.security.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

/**
 * 答题记录实体类
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "test_answers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TestAnswer {

  /** 主键ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 会话ID */
  @Column(name = "session_id", nullable = false)
  private Long sessionId;

  /** 题目ID */
  @Column(name = "question_id", nullable = false)
  private Long questionId;

  /** 题目在会话中的索引 */
  @Column(name = "question_index", nullable = false)
  private Integer questionIndex;

  /** 用户答案 */
  @Column(name = "user_answer", columnDefinition = "TEXT")
  private String userAnswer;

  /** 是否正确 */
  @Column(name = "is_correct")
  private Boolean isCorrect;

  /** 得分 */
  @Column(name = "score")
  private Integer score = 0;

  /** 是否已显示反馈 */
  @Column(name = "feedback_shown")
  private Boolean feedbackShown = false;

  /** 答题时间 */
  @Column(name = "answered_at")
  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime answeredAt;

  /** 关联的答题会话 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", insertable = false, updatable = false)
  @JsonIgnore
  private TestSession testSession;

  /** 关联的题目 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", insertable = false, updatable = false)
  @JsonIgnore
  private Question question;

  /** 检查是否已答题 */
  public boolean isAnswered() {
    return userAnswer != null && !userAnswer.trim().isEmpty();
  }

  /** 检查是否已显示反馈 */
  public boolean hasFeedbackShown() {
    return feedbackShown != null && feedbackShown;
  }

  /** 检查答案是否正确 */
  public boolean isCorrectAnswer() {
    return isCorrect != null && isCorrect;
  }

  /** 获取得分，如果未答题则返回0 */
  public Integer getScore() {
    if (!isAnswered() || !isCorrectAnswer()) {
      return 0;
    }
    return score != null ? score : 0;
  }
}
