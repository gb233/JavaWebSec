package com.javaweb.security.dto.user;

import com.javaweb.security.entity.User;
import com.javaweb.security.entity.UserProfile;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户响应DTO（返回给前端的用户信息）
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
public class UserResponseDto {

  /** 用户ID */
  private Long id;

  /** 用户名 */
  private String username;

  /** 邮箱地址 */
  private String email;

  /** 真实姓名 */
  private String fullName;

  /** 用户角色 */
  private String userRole;

  /** 用户角色描述 */
  private String userRoleDescription;

  /** 用户状态 */
  private String userStatus;

  /** 用户状态描述 */
  private String userStatusDescription;

  /** 邮箱是否已验证 */
  private Boolean isEmailVerified;

  /** 头像URL */
  private String avatarUrl;

  /** 个人简介 */
  private String bio;

  /** 最后登录时间 */
  private LocalDateTime lastLoginAt;

  /** 创建时间 */
  private LocalDateTime createdAt;

  /** 用户配置文件信息 */
  private UserProfileDto profile;

  /** 用户配置文件DTO */
  @Data
  public static class UserProfileDto {
    /** 技能水平 */
    private String skillLevel;

    /** 技能水平描述 */
    private String skillLevelDescription;

    /** 学习目标 */
    private String learningGoals;

    /** 职业背景 */
    private String professionalBackground;

    /** 工作经验年限 */
    private Integer yearsOfExperience;

    /** 所在国家 */
    private String country;

    /** 所在城市 */
    private String city;

    /** 首选语言 */
    private String preferredLanguage;

    /** 总学习时间（分钟） */
    private Long totalStudyTime;

    /** 总获得积分 */
    private Integer totalPoints;

    /** 完成的漏洞数量 */
    private Integer completedVulnerabilities;

    /** 通过的测试数量 */
    private Integer passedTests;

    /** 完成的挑战数量 */
    private Integer completedChallenges;

    /** 获得的徽章数量 */
    private Integer earnedBadges;

    /** 当前学习连续天数 */
    private Integer currentStreak;

    /** 最长学习连续天数 */
    private Integer longestStreak;
  }

  /** 从User实体创建UserResponseDto */
  public static UserResponseDto fromEntity(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User实体不能为null");
    }

    UserResponseDto dto = new UserResponseDto();
    dto.setId(user.getId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setFullName(user.getFullName());

    // 安全地设置userRole，避免NullPointerException
    if (user.getUserRole() != null) {
      dto.setUserRole(user.getUserRole().name());
      dto.setUserRoleDescription(user.getUserRole().getDescription());
    } else {
      // 如果userRole为null，设置默认值
      dto.setUserRole("STUDENT");
      dto.setUserRoleDescription("学生");
    }

    // 安全地设置userStatus，避免NullPointerException
    if (user.getUserStatus() != null) {
      dto.setUserStatus(user.getUserStatus().name());
      dto.setUserStatusDescription(user.getUserStatus().getDescription());
    } else {
      // 如果userStatus为null，设置默认值
      dto.setUserStatus("ACTIVE");
      dto.setUserStatusDescription("正常");
    }

    dto.setIsEmailVerified(user.getIsEmailVerified() != null ? user.getIsEmailVerified() : false);
    dto.setAvatarUrl(user.getAvatarUrl());
    dto.setBio(user.getBio());
    dto.setLastLoginAt(user.getLastLoginAt());
    dto.setCreatedAt(user.getCreatedAt());
    return dto;
  }

  /** 从User和UserProfile实体创建完整的UserResponseDto */
  public static UserResponseDto fromEntity(User user, UserProfile profile) {
    UserResponseDto dto = fromEntity(user);

    if (profile != null) {
      UserProfileDto profileDto = new UserProfileDto();

      // 安全地设置skillLevel，避免NullPointerException
      if (profile.getSkillLevel() != null) {
        profileDto.setSkillLevel(profile.getSkillLevel().name());
        profileDto.setSkillLevelDescription(profile.getSkillLevel().getDescription());
      } else {
        // 如果skillLevel为null，设置默认值
        profileDto.setSkillLevel("BEGINNER");
        profileDto.setSkillLevelDescription("初学者");
      }

      profileDto.setLearningGoals(profile.getLearningGoals());
      profileDto.setProfessionalBackground(profile.getProfessionalBackground());
      profileDto.setYearsOfExperience(profile.getYearsOfExperience());
      profileDto.setCountry(profile.getCountry());
      profileDto.setCity(profile.getCity());
      profileDto.setPreferredLanguage(profile.getPreferredLanguage());

      // 安全地设置数值字段，避免NullPointerException
      profileDto.setTotalStudyTime(
          profile.getTotalStudyTime() != null ? profile.getTotalStudyTime() : 0L);
      profileDto.setTotalPoints(profile.getTotalPoints() != null ? profile.getTotalPoints() : 0);
      profileDto.setCompletedVulnerabilities(
          profile.getCompletedVulnerabilities() != null
              ? profile.getCompletedVulnerabilities()
              : 0);
      profileDto.setPassedTests(profile.getPassedTests() != null ? profile.getPassedTests() : 0);
      profileDto.setCompletedChallenges(
          profile.getCompletedChallenges() != null ? profile.getCompletedChallenges() : 0);
      profileDto.setEarnedBadges(profile.getEarnedBadges() != null ? profile.getEarnedBadges() : 0);
      profileDto.setCurrentStreak(
          profile.getCurrentStreak() != null ? profile.getCurrentStreak() : 0);
      profileDto.setLongestStreak(
          profile.getLongestStreak() != null ? profile.getLongestStreak() : 0);

      dto.setProfile(profileDto);
    }

    return dto;
  }
}
