package com.javaweb.security.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录DTO
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
public class UserLoginDto {

  /** 登录标识（用户名或邮箱） */
  @NotBlank(message = "登录标识不能为空")
  @JsonProperty("loginIdentifier")
  private String loginIdentifier;

  /** 密码 */
  @NotBlank(message = "密码不能为空")
  private String password;

  /** 是否记住登录状态 */
  private Boolean rememberMe = false;

  /** 验证码（如果需要） */
  private String captcha;

  /** 客户端信息 */
  private String userAgent;

  /** 客户端IP地址 */
  private String clientIp;
}
