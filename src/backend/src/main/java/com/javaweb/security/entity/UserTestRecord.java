package com.javaweb.security.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

/**
 * 用户测试记录实体类
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "user_test_records")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserTestRecord {

  /** 主键ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 用户ID */
  @Column(name = "user_id", nullable = false)
  private Long userId;

  /** 会话ID */
  @Column(name = "session_id", nullable = false)
  private Long sessionId;

  /** 答题模式代码 */
  @Column(name = "mode_code", nullable = false, length = 20)
  private String modeCode;

  /** 漏洞分类代码 */
  @Column(name = "category_code", length = 10)
  private String categoryCode;

  /** 总得分 */
  @Column(name = "total_score")
  private Integer totalScore = 0;

  /** 正确题数 */
  @Column(name = "correct_count")
  private Integer correctCount = 0;

  /** 总题数 */
  @Column(name = "total_questions", nullable = false)
  private Integer totalQuestions;

  /** 完成率 */
  @Column(name = "completion_rate", precision = 5, scale = 2)
  private BigDecimal completionRate = BigDecimal.ZERO;

  /** 用时（秒） */
  @Column(name = "time_spent")
  private Integer timeSpent = 0;

  /** 开始时间 */
  @Column(name = "started_at")
  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime startedAt;

  /** 完成时间 */
  @Column(name = "completed_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime completedAt;

  /** 关联的用户 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  /** 关联的答题会话 */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", insertable = false, updatable = false)
  private TestSession testSession;

  /** 计算正确率 */
  public BigDecimal getAccuracyRate() {
    if (totalQuestions == null || totalQuestions == 0) {
      return BigDecimal.ZERO;
    }
    return BigDecimal.valueOf(correctCount)
        .divide(BigDecimal.valueOf(totalQuestions), 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
  }

  /** 计算平均每题用时 */
  public BigDecimal getAverageTimePerQuestion() {
    if (totalQuestions == null || totalQuestions == 0 || timeSpent == null || timeSpent == 0) {
      return BigDecimal.ZERO;
    }
    return BigDecimal.valueOf(timeSpent)
        .divide(BigDecimal.valueOf(totalQuestions), 2, RoundingMode.HALF_UP);
  }

  /** 检查是否已完成 */
  public boolean isCompleted() {
    return completedAt != null;
  }

  /** 检查是否已超时（假设超时时间为2小时） */
  public boolean isTimeout() {
    if (startedAt == null) {
      return false;
    }
    LocalDateTime timeoutTime = startedAt.plusHours(2);
    return LocalDateTime.now().isAfter(timeoutTime);
  }

  /** 获取测试等级 */
  public String getTestLevel() {
    if (totalScore == null || totalScore == 0) {
      return "未完成";
    }

    BigDecimal accuracyRate = getAccuracyRate();
    if (accuracyRate.compareTo(BigDecimal.valueOf(90)) >= 0) {
      return "优秀";
    } else if (accuracyRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
      return "良好";
    } else if (accuracyRate.compareTo(BigDecimal.valueOf(70)) >= 0) {
      return "中等";
    } else if (accuracyRate.compareTo(BigDecimal.valueOf(60)) >= 0) {
      return "及格";
    } else {
      return "不及格";
    }
  }
}
