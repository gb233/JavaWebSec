package com.javaweb.security.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 用户注册DTO
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
public class UserRegistrationDto {

  /** 用户名（3-20字符，只能包含字母、数字、下划线） */
  @NotBlank(message = "用户名不能为空")
  @Length(min = 3, max = 20, message = "用户名长度必须在3-20字符之间") @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
  private String username;

  /** 邮箱地址 */
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  @Length(max = 100, message = "邮箱长度不能超过100字符") private String email;

  /** 密码（8-32字符，必须包含字母和数字） */
  @NotBlank(message = "密码不能为空")
  @Length(min = 8, max = 32, message = "密码长度必须在8-32字符之间") @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
  private String password;

  /** 确认密码 */
  @NotBlank(message = "确认密码不能为空")
  private String confirmPassword;

  /** 真实姓名 */
  @Length(max = 50, message = "真实姓名长度不能超过50字符") private String fullName;

  /** 个人简介 */
  @Length(max = 500, message = "个人简介长度不能超过500字符") private String bio;

  /** 邮箱验证码 */
  private String emailVerificationCode;

  /** 验证码ID */
  private String captchaId;

  /** 验证码答案 */
  private String captchaAnswer;

  /** 防重放攻击nonce token */
  private String nonce;

  /** 防重放攻击时间戳 */
  private String timestamp;

  /** 是否同意用户协议 */
  private Boolean agreeToTerms = false;

  /** 验证两次密码是否一致 */
  public boolean isPasswordMatch() {
    return password != null && password.equals(confirmPassword);
  }
}
