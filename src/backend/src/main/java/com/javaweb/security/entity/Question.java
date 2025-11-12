package com.javaweb.security.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Convert;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 题目实体类
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "vulnerability_questions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question {

  /** 主键ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 漏洞分类代码 */
  @Column(name = "category_code", nullable = false, length = 10)
  private String categoryCode;

  /** 题目类型 */
  @Convert(converter = com.javaweb.security.converter.QuestionTypeConverter.class)
  @Column(name = "question_type", nullable = false)
  private QuestionType questionType;

  /** 难度等级 */
  @Convert(converter = com.javaweb.security.converter.DifficultyConverter.class)
  @Column(name = "difficulty", nullable = false)
  private Difficulty difficulty;

  /** 知识点来源 */
  @Convert(converter = com.javaweb.security.converter.KnowledgeSourceConverter.class)
  @Column(name = "knowledge_source", nullable = false)
  private KnowledgeSource knowledgeSource;

  /** 题目内容 */
  @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
  private String questionText;

  /** 题目图片URL */
  @Column(name = "question_image", length = 500)
  private String questionImage;

  /** 选项内容（JSON格式） */
  @Column(name = "options", columnDefinition = "JSON")
  private String options;

  /** 正确答案 */
  @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
  private String correctAnswer;

  /** 题目解析 */
  @Column(name = "explanation", nullable = false, columnDefinition = "TEXT")
  private String explanation;

  /** 题目分值 */
  @Column(name = "score", nullable = false)
  private Integer score;

  /** 标签（JSON格式） */
  @Column(name = "tags", columnDefinition = "JSON")
  private String tags;

  /** 作者ID */
  @Column(name = "author_id")
  private Long authorId;

  /** 题目状态 */
  @Convert(converter = com.javaweb.security.converter.QuestionStatusConverter.class)
  @Column(name = "status", nullable = false)
  private QuestionStatus status = QuestionStatus.DRAFT;

  /** 创建时间 */
  @Column(name = "created_at")
  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  /** 更新时间 */
  @Column(name = "updated_at")
  @UpdateTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime updatedAt;

  /** 答题记录列表（一对多关系） */
  @OneToMany(mappedBy = "questionId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<TestAnswer> testAnswers;

  /** 题目类型枚举 */
  public enum QuestionType {
    SINGLE("single", "单选题"),
    MULTIPLE("multiple", "多选题"),
    JUDGE("judge", "判断题");

    private final String code;
    private final String description;

    QuestionType(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }

    /** 根据代码值获取枚举 */
    public static QuestionType fromCode(String code) {
      if (code == null) {
        return null;
      }
      for (QuestionType type : values()) {
        if (type.code.equalsIgnoreCase(code)) {
          return type;
        }
      }
      throw new IllegalArgumentException("未知的题目类型: " + code);
    }
  }

  /** 难度等级枚举 */
  public enum Difficulty {
    EASY("easy", "简单"),
    MEDIUM("medium", "中等"),
    HARD("hard", "困难");

    private final String code;
    private final String description;

    Difficulty(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }

    /** 根据代码值获取枚举 */
    public static Difficulty fromCode(String code) {
      if (code == null) {
        return null;
      }
      for (Difficulty difficulty : values()) {
        if (difficulty.code.equalsIgnoreCase(code)) {
          return difficulty;
        }
      }
      throw new IllegalArgumentException("未知的难度等级: " + code);
    }
  }

  /** 知识点来源枚举 */
  public enum KnowledgeSource {
    PRINCIPLE("principle", "理论知识基础"),
    HARM("harm", "危害场景分析"),
    EXPLOIT("exploit", "攻击演示"),
    VULNERABLE_CODE("vulnerable_code", "漏洞代码"),
    SECURE_CODE("secure_code", "安全代码"),
    REPAIR("repair", "修复建议"),
    DETECTION("detection", "检测监控");

    private final String code;
    private final String description;

    KnowledgeSource(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }

    /** 根据代码值获取枚举 */
    public static KnowledgeSource fromCode(String code) {
      if (code == null) {
        return null;
      }
      for (KnowledgeSource source : values()) {
        if (source.code.equalsIgnoreCase(code)) {
          return source;
        }
      }
      throw new IllegalArgumentException("未知的知识点来源: " + code);
    }
  }

  /** 题目状态枚举 */
  public enum QuestionStatus {
    DRAFT("draft", "草稿"),
    REVIEW("review", "审核中"),
    APPROVED("approved", "已通过");

    private final String code;
    private final String description;

    QuestionStatus(String code, String description) {
      this.code = code;
      this.description = description;
    }

    public String getCode() {
      return code;
    }

    public String getDescription() {
      return description;
    }

    /** 根据代码值获取枚举 */
    public static QuestionStatus fromCode(String code) {
      if (code == null) {
        return null;
      }
      for (QuestionStatus status : values()) {
        if (status.code.equalsIgnoreCase(code)) {
          return status;
        }
      }
      throw new IllegalArgumentException("未知的题目状态: " + code);
    }
  }

  /** 检查是否为单选题 */
  public boolean isSingleChoice() {
    return questionType == QuestionType.SINGLE;
  }

  /** 检查是否为多选题 */
  public boolean isMultipleChoice() {
    return questionType == QuestionType.MULTIPLE;
  }

  /** 检查是否为判断题 */
  public boolean isJudgment() {
    return questionType == QuestionType.JUDGE;
  }

  /** 检查是否已通过审核 */
  public boolean isApproved() {
    return status == QuestionStatus.APPROVED;
  }

  // 答题状态字段（临时字段，不存储到数据库）
  @Transient private Boolean isAnswered;

  @Transient private Boolean isCorrect;

  @Transient private String userAnswer;

  @Transient
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime answeredAt;
}
