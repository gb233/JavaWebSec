package com.javaweb.security.service;

import com.javaweb.security.dto.auth.LoginResponseDto;
import com.javaweb.security.dto.user.UserLoginDto;

/**
 * 用户认证服务接口
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
public interface AuthenticationService {

  /** 用户登录 */
  LoginResponseDto login(UserLoginDto loginDto);

  /** 刷新令牌 */
  LoginResponseDto refreshToken(String refreshToken);

  /** 用户登出 */
  void logout(String token);

  /** 验证令牌是否有效 */
  boolean validateToken(String token);

  /** 从令牌中获取用户名 */
  String getUsernameFromToken(String token);

  /** 检查用户是否已认证 */
  boolean isAuthenticated();

  /** 获取当前认证用户ID */
  Long getCurrentUserId();

  /** 获取当前认证用户名 */
  String getCurrentUsername();
}
