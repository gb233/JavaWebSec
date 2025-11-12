package com.javaweb.security.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 用户配置文件实体类
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(
    name = "user_profiles",
    indexes = {
      @Index(name = "idx_user_id", columnList = "user_id", unique = true),
      @Index(name = "idx_skill_level", columnList = "skill_level"),
      @Index(name = "idx_created_at", columnList = "created_at")
    })
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {

  /** 配置文件ID（主键，自增） */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  /** 关联的用户ID */
  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

  /** 关联的用户实体（一对一关系） */
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  /** 技能水平 */
  @Column(name = "skill_level", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private SkillLevel skillLevel = SkillLevel.BEGINNER;

  /** 学习目标 */
  @Column(name = "learning_goals", length = 1000)
  private String learningGoals;

  /** 职业背景 */
  @Column(name = "professional_background", length = 50)
  private String professionalBackground;

  /** 工作经验年限 */
  @Column(name = "years_of_experience")
  private Integer yearsOfExperience;

  /** 生日 */
  @Column(name = "birth_date")
  private LocalDate birthDate;

  /** 性别 */
  @Column(name = "gender", length = 10)
  @Enumerated(EnumType.STRING)
  private Gender gender;

  /** 所在国家 */
  @Column(name = "country", length = 50)
  private String country;

  /** 所在城市 */
  @Column(name = "city", length = 50)
  private String city;

  /** 时区 */
  @Column(name = "timezone", length = 50)
  private String timezone = "Asia/Shanghai";

  /** 首选语言 */
  @Column(name = "preferred_language", length = 20)
  private String preferredLanguage = "zh-CN";

  /** 是否接收邮件通知 */
  @Column(name = "email_notifications", nullable = false)
  private Boolean emailNotifications = true;

  /** 是否接收学习提醒 */
  @Column(name = "learning_reminders", nullable = false)
  private Boolean learningReminders = true;

  /** 总学习时间（分钟） */
  @Column(name = "total_study_time", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private Long totalStudyTime = 0L;

  /** 总获得积分 */
  @Column(name = "total_points", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer totalPoints = 0;

  /** 完成的漏洞数量 */
  @Column(name = "completed_vulnerabilities", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer completedVulnerabilities = 0;

  /** 通过的测试数量 */
  @Column(name = "passed_tests", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer passedTests = 0;

  /** 完成的挑战数量 */
  @Column(name = "completed_challenges", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer completedChallenges = 0;

  /** 获得的徽章数量 */
  @Column(name = "earned_badges", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer earnedBadges = 0;

  /** 当前学习连续天数 */
  @Column(name = "current_streak", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer currentStreak = 0;

  /** 最长学习连续天数 */
  @Column(name = "longest_streak", nullable = false, columnDefinition = "INT DEFAULT 0")
  private Integer longestStreak = 0;

  /** 创建时间（自动设置） */
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** 更新时间（自动更新） */
  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /** 技能水平枚举 */
  public enum SkillLevel {
    BEGINNER("初学者"),
    INTERMEDIATE("中级"),
    ADVANCED("高级"),
    EXPERT("专家");

    private final String description;

    SkillLevel(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  /** 性别枚举 */
  public enum Gender {
    MALE("男"),
    FEMALE("女"),
    OTHER("其他");

    private final String description;

    Gender(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  /** 增加学习时间 */
  public void addStudyTime(long minutes) {
    this.totalStudyTime += minutes;
  }

  /** 增加积分 */
  public void addPoints(int points) {
    this.totalPoints += points;
  }

  /** 增加完成的漏洞数量 */
  public void incrementCompletedVulnerabilities() {
    this.completedVulnerabilities++;
  }

  /** 增加通过的测试数量 */
  public void incrementPassedTests() {
    this.passedTests++;
  }

  /** 增加完成的挑战数量 */
  public void incrementCompletedChallenges() {
    this.completedChallenges++;
  }

  /** 增加获得的徽章数量 */
  public void incrementEarnedBadges() {
    this.earnedBadges++;
  }

  /** 更新学习连续天数 */
  public void updateStreak(int streak) {
    this.currentStreak = streak;
    if (streak > this.longestStreak) {
      this.longestStreak = streak;
    }
  }
}
