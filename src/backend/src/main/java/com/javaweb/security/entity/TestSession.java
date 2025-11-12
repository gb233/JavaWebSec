package com.javaweb.security.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

/**
 * 答题会话实体类
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "test_sessions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TestSession {

  /** 主键ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 用户ID */
  @Column(name = "user_id", nullable = false)
  private Long userId;

  /** 答题模式代码 */
  @Column(name = "mode_code", nullable = false, length = 20)
  private String modeCode;

  /** 漏洞分类代码（单类型模式时使用） */
  @Column(name = "category_code", length = 10)
  private String categoryCode;

  /** 会话代码（唯一标识） */
  @Column(name = "session_code", nullable = false, unique = true, length = 20)
  private String sessionCode;

  /** 会话状态 */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private SessionStatus status = SessionStatus.ACTIVE;

  /** 当前题目索引 */
  @Column(name = "current_question_index")
  private Integer currentQuestionIndex = 0;

  /** 总题目数量 */
  @Column(name = "total_questions", nullable = false)
  private Integer totalQuestions;

  /** 已答题数量 */
  @Column(name = "answered_questions")
  private Integer answeredQuestions = 0;

  /** 正确答案数量 */
  @Column(name = "correct_answers")
  private Integer correctAnswers = 0;

  /** 总得分 */
  @Column(name = "total_score")
  private Integer totalScore = 0;

  /** 开始时间 */
  @Column(name = "start_time")
  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startTime;

  /** 结束时间 */
  @Column(name = "end_time")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime endTime;

  /** 创建时间 */
  @Column(name = "created_at")
  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  /** 答题记录列表（一对多关系） */
  @OneToMany(mappedBy = "sessionId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<TestAnswer> testAnswers;

  /** 会话状态枚举 */
  public enum SessionStatus {
    ACTIVE("active", "进行中"),
    COMPLETED("completed", "已完成"),
    ABANDONED("abandoned", "已放弃");

    private final String code;
    private final String description;

    SessionStatus(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }
  }

  /** 计算完成率 */
  public Double getCompletionRate() {
    if (totalQuestions == null || totalQuestions == 0) {
      return 0.0;
    }
    return (double) answeredQuestions / totalQuestions * 100;
  }

  /** 计算正确率 */
  public Double getAccuracyRate() {
    if (answeredQuestions == null || answeredQuestions == 0) {
      return 0.0;
    }
    return (double) correctAnswers / answeredQuestions * 100;
  }

  /** 检查是否已完成 */
  public boolean isCompleted() {
    return status == SessionStatus.COMPLETED;
  }

  /** 检查是否已放弃 */
  public boolean isAbandoned() {
    return status == SessionStatus.ABANDONED;
  }
}
