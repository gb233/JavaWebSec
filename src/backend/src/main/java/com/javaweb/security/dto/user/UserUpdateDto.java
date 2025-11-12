package com.javaweb.security.dto.user;

import com.javaweb.security.entity.UserProfile;
import java.time.LocalDate;
import javax.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户信息更新DTO
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
public class UserUpdateDto {

  /** 邮箱地址 */
  @Email(message = "邮箱格式不正确")
  @Length(max = 100, message = "邮箱长度不能超过100字符") private String email;

  /** 真实姓名 */
  @Length(max = 50, message = "真实姓名长度不能超过50字符") private String fullName;

  /** 个人简介 */
  @Length(max = 500, message = "个人简介长度不能超过500字符") private String bio;

  /** 头像URL */
  @Length(max = 200, message = "头像URL长度不能超过200字符") private String avatarUrl;

  // =========================
  // 用户配置文件相关字段
  // =========================

  /** 技能水平 */
  private UserProfile.SkillLevel skillLevel;

  /** 学习目标 */
  @Length(max = 1000, message = "学习目标长度不能超过1000字符") private String learningGoals;

  /** 职业背景 */
  @Length(max = 50, message = "职业背景长度不能超过50字符") private String professionalBackground;

  /** 工作经验年限 */
  private Integer yearsOfExperience;

  /** 生日 */
  private LocalDate birthDate;

  /** 性别 */
  private UserProfile.Gender gender;

  /** 所在国家 */
  @Length(max = 50, message = "国家名称长度不能超过50字符") private String country;

  /** 所在城市 */
  @Length(max = 50, message = "城市名称长度不能超过50字符") private String city;

  /** 时区 */
  @Length(max = 50, message = "时区长度不能超过50字符") private String timezone;

  /** 首选语言 */
  @Length(max = 20, message = "首选语言长度不能超过20字符") private String preferredLanguage;

  /** 是否接收邮件通知 */
  private Boolean emailNotifications;

  /** 是否接收学习提醒 */
  private Boolean learningReminders;
}
