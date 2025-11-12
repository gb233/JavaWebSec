package com.javaweb.security.dto.auth;

import com.javaweb.security.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

  /** JWT访问令牌 */
  private String accessToken;

  /** 令牌类型（Bearer） */
  private String tokenType = "Bearer";

  /** 令牌过期时间（毫秒时间戳） */
  private Long expiresAt;

  /** 刷新令牌（如果启用） */
  private String refreshToken;

  /** 用户信息 */
  private UserResponseDto user;

  /** 是否为首次登录 */
  private Boolean isFirstLogin = false;

  /** 创建登录响应 */
  public static LoginResponseDto create(String accessToken, Long expiresAt, UserResponseDto user) {
    LoginResponseDto response = new LoginResponseDto();
    response.setAccessToken(accessToken);
    response.setExpiresAt(expiresAt);
    response.setUser(user);
    return response;
  }

  /** 创建完整的登录响应（包含刷新令牌） */
  public static LoginResponseDto create(
      String accessToken, String refreshToken, Long expiresAt, UserResponseDto user) {
    LoginResponseDto response = create(accessToken, expiresAt, user);
    response.setRefreshToken(refreshToken);
    return response;
  }
}
