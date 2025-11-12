package com.javaweb.security.service.impl;

import com.javaweb.security.dto.auth.LoginResponseDto;
import com.javaweb.security.dto.user.UserLoginDto;
import com.javaweb.security.dto.user.UserResponseDto;
import com.javaweb.security.entity.User;
import com.javaweb.security.entity.UserProfile;
import com.javaweb.security.repository.UserProfileRepository;
import com.javaweb.security.repository.UserRepository;
import com.javaweb.security.security.JwtTokenProvider;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.BadgeDetectionService;
import com.javaweb.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户认证服务实现类
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final BadgeDetectionService badgeDetectionService;

  @Override
  @Transactional
  public LoginResponseDto login(UserLoginDto loginDto) {
    log.info("用户登录请求：identifier={}", loginDto.getLoginIdentifier());

    try {
      // 查找用户
      User user =
          userRepository
              .findByUsernameOrEmail(loginDto.getLoginIdentifier())
              .orElseThrow(() -> new BadCredentialsException("用户名或邮箱不存在"));

      // 检查用户状态
      validateUserStatus(user);

      // 执行Spring Security认证（包含密码验证）
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginDto.getLoginIdentifier(), loginDto.getPassword()));

      // 生成JWT令牌 - 根据rememberMe调整过期时间
      long tokenExpirationMs;
      if (Boolean.TRUE.equals(loginDto.getRememberMe())) {
        // 记住我：使用30天过期时间（2592000000毫秒）
        tokenExpirationMs = 30L * 24 * 60 * 60 * 1000;
      } else {
        // 不记住我：使用默认过期时间（24小时）
        tokenExpirationMs = jwtTokenProvider.getJwtExpirationInMs();
      }

      String accessToken = jwtTokenProvider.generateToken(authentication, tokenExpirationMs);
      long expiresAt = System.currentTimeMillis() + tokenExpirationMs;

      // 检查是否为首次登录（必须在updateLastLoginInfo之前检查）
      boolean isFirstLogin = user.getLastLoginAt() == null;

      // 检测首次登录徽章（必须在updateLastLoginInfo之前检测）
      if (isFirstLogin) {
        try {
          badgeDetectionService.checkFirstLoginBadge(user.getId());
          log.info("检测首次登录徽章: userId={}", user.getId());
        } catch (Exception e) {
          log.error("检测首次登录徽章失败: userId={}, error={}", user.getId(), e.getMessage(), e);
          // 不抛出异常，避免影响登录流程
        }
      }

      // 处理登录成功（在徽章检测之后更新登录信息）
      try {
        userService.handleLoginSuccess(user.getId());
      } catch (Exception e) {
        log.error("处理登录成功失败: userId={}, error={}", user.getId(), e.getMessage(), e);
        // 不抛出异常，避免影响登录流程
      }

      try {
        userService.updateLastLoginInfo(user.getId(), loginDto.getClientIp());
      } catch (Exception e) {
        log.error("更新最后登录信息失败: userId={}, error={}", user.getId(), e.getMessage(), e);
        // 不抛出异常，避免影响登录流程
      }

      // 获取用户完整信息
      UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);

      // 如果profile不存在，创建默认profile（防御性编程）
      if (profile == null) {
        log.warn("用户 {} 没有UserProfile，尝试创建默认配置", user.getId());
        try {
          profile = userService.createUserProfile(user.getId());
        } catch (Exception e) {
          log.error("创建UserProfile失败: userId={}, error={}", user.getId(), e.getMessage(), e);
          // 继续使用null profile，UserResponseDto.fromEntity会处理
        }
      }

      UserResponseDto userDto;
      try {
        userDto = UserResponseDto.fromEntity(user, profile);
      } catch (Exception e) {
        log.error("创建UserResponseDto失败: userId={}, error={}", user.getId(), e.getMessage(), e);
        throw new RuntimeException("创建用户响应DTO失败：" + e.getMessage(), e);
      }

      LoginResponseDto response = LoginResponseDto.create(accessToken, expiresAt, userDto);
      response.setIsFirstLogin(isFirstLogin);
      response.setTokenType("Bearer"); // 确保tokenType被设置

      log.info("用户登录成功：userId={}, username={}", user.getId(), user.getUsername());
      return response;

    } catch (BadCredentialsException e) {
      log.warn("用户登录失败：identifier={}, reason={}", loginDto.getLoginIdentifier(), e.getMessage());
      throw e;
    } catch (RuntimeException e) {
      // 重新抛出RuntimeException，让GlobalExceptionHandler处理
      log.error(
          "用户登录运行时异常：identifier={}, error={}", loginDto.getLoginIdentifier(), e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error("用户登录异常：identifier={}, error={}", loginDto.getLoginIdentifier(), e.getMessage(), e);
      throw new RuntimeException("登录过程中发生错误：" + e.getMessage(), e);
    }
  }

  @Override
  public LoginResponseDto refreshToken(String refreshToken) {
    log.info("刷新令牌请求");

    try {
      // 验证刷新令牌
      if (!jwtTokenProvider.validateToken(refreshToken)) {
        throw new BadCredentialsException("刷新令牌无效");
      }

      // 从令牌中获取用户名
      String username = jwtTokenProvider.getUsernameFromJWT(refreshToken);

      // 查找用户
      User user =
          userRepository
              .findByUsernameOrEmail(username)
              .orElseThrow(() -> new BadCredentialsException("用户不存在"));

      // 检查用户状态
      validateUserStatus(user);

      // 生成新的访问令牌
      Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
      String newAccessToken = jwtTokenProvider.generateToken(authentication);
      long expiresAt = System.currentTimeMillis() + jwtTokenProvider.getJwtExpirationInMs();

      // 获取用户完整信息
      UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
      UserResponseDto userDto = UserResponseDto.fromEntity(user, profile);

      log.info("令牌刷新成功：userId={}, username={}", user.getId(), user.getUsername());
      return LoginResponseDto.create(newAccessToken, refreshToken, expiresAt, userDto);

    } catch (Exception e) {
      log.error("令牌刷新失败：error={}", e.getMessage(), e);
      throw new BadCredentialsException("令牌刷新失败：" + e.getMessage());
    }
  }

  @Override
  @Transactional
  public void logout(String token) {
    log.info("用户登出请求");

    try {
      // 验证令牌
      if (!jwtTokenProvider.validateToken(token)) {
        log.warn("登出时令牌无效");
        return;
      }

      // 获取用户信息
      String username = jwtTokenProvider.getUsernameFromJWT(token);

      // TODO: 实现令牌黑名单机制
      // tokenBlacklistService.addToBlacklist(token);

      // 清除Security上下文
      SecurityContextHolder.clearContext();

      log.info("用户登出成功：username={}", username);

    } catch (Exception e) {
      log.error("用户登出异常：error={}", e.getMessage(), e);
    }
  }

  @Override
  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }

    try {
      return jwtTokenProvider.validateToken(token);
    } catch (Exception e) {
      log.debug("令牌验证失败：error={}", e.getMessage());
      return false;
    }
  }

  @Override
  public String getUsernameFromToken(String token) {
    if (!validateToken(token)) {
      return null;
    }

    try {
      return jwtTokenProvider.getUsernameFromJWT(token);
    } catch (Exception e) {
      log.error("从令牌获取用户名失败：error={}", e.getMessage());
      return null;
    }
  }

  @Override
  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.isAuthenticated()
        && !(authentication.getPrincipal() instanceof String);
  }

  @Override
  public Long getCurrentUserId() {
    if (!isAuthenticated()) {
      return null;
    }

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String username = userDetails.getUsername();

      return userRepository.findByUsernameOrEmail(username).map(User::getId).orElse(null);

    } catch (Exception e) {
      log.error("获取当前用户ID失败：error={}", e.getMessage());
      return null;
    }
  }

  @Override
  public String getCurrentUsername() {
    if (!isAuthenticated()) {
      return null;
    }

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      return userDetails.getUsername();

    } catch (Exception e) {
      log.error("获取当前用户名失败：error={}", e.getMessage());
      return null;
    }
  }

  // ===========================
  // 私有辅助方法
  // ===========================

  private void validateUserStatus(User user) {
    // 检查账户是否被锁定
    if (user.isAccountLocked()) {
      throw new BadCredentialsException("账户已被锁定，请稍后再试");
    }

    // 检查账户是否启用
    if (!user.isAccountEnabled()) {
      String message;
      switch (user.getUserStatus()) {
        case INACTIVE:
          message = "账户未激活，请先激活账户";
          break;
        case SUSPENDED:
          message = "账户已被暂停，请联系管理员";
          break;
        case BANNED:
          message = "账户已被封禁";
          break;
        default:
          message = "账户状态异常";
      }
      throw new BadCredentialsException(message);
    }
  }
}
