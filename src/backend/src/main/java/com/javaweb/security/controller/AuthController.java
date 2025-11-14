package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.dto.auth.LoginResponseDto;
import com.javaweb.security.dto.user.UserLoginDto;
import com.javaweb.security.dto.user.UserRegistrationDto;
import com.javaweb.security.dto.user.UserResponseDto;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户认证", description = "用户注册、登录、登出等认证相关接口")
public class AuthController {

  private final AuthenticationService authenticationService;
  private final UserService userService;
  private final com.javaweb.security.service.CaptchaService captchaService;
  private final com.javaweb.security.service.ReplayAttackPreventionService
      replayAttackPreventionService;
  private final com.javaweb.security.config.SecurityFeaturesConfig securityFeaturesConfig;

  /** 用户注册 */
  @PostMapping("/register")
  @Operation(summary = "用户注册", description = "新用户注册接口")
  public ResponseEntity<ApiResult<UserResponseDto>> register(
      @Parameter(description = "用户注册信息", required = true) @Valid @RequestBody
          UserRegistrationDto registrationDto,
      HttpServletRequest request) {

    log.info(
        "用户注册请求：username={}, email={}, ip={}",
        registrationDto.getUsername(),
        registrationDto.getEmail(),
        getClientIp(request));

    try {
      String clientIp = getClientIp(request);

      // 验证防重放攻击nonce token（如果启用）
      if (!replayAttackPreventionService.verifyNonce(
          registrationDto.getNonce(), registrationDto.getTimestamp(), clientIp)) {
        log.warn("注册请求防重放验证失败：ip={}", clientIp);
        return ResponseEntity.badRequest().body(ApiResult.failed("请求已过期或已被使用，请刷新页面重试"));
      }

      // 验证验证码（如果启用）
      if (!captchaService.verifyCaptcha(
          registrationDto.getCaptchaId(), registrationDto.getCaptchaAnswer(), clientIp)) {
        log.warn("注册请求验证码验证失败：ip={}", clientIp);
        return ResponseEntity.badRequest().body(ApiResult.failed("验证码错误或已过期，请重新获取"));
      }

      // 检查用户协议同意状态
      if (!Boolean.TRUE.equals(registrationDto.getAgreeToTerms())) {
        return ResponseEntity.badRequest().body(ApiResult.failed("请同意用户服务协议"));
      }

      // 执行注册
      UserResponseDto userResponse = userService.register(registrationDto);

      log.info("用户注册成功：userId={}, username={}", userResponse.getId(), userResponse.getUsername());
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResult.success("注册成功", userResponse));

    } catch (IllegalArgumentException e) {
      log.warn("用户注册失败：{}", e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("用户注册异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("注册失败，请稍后重试"));
    }
  }

  /** 用户登录 */
  @PostMapping("/login")
  @Operation(summary = "用户登录", description = "用户登录验证接口")
  public ResponseEntity<ApiResult<LoginResponseDto>> login(
      @Parameter(description = "用户登录信息", required = true) @Valid @RequestBody UserLoginDto loginDto,
      HttpServletRequest request) {

    // 设置客户端信息
    String clientIp = getClientIp(request);
    loginDto.setClientIp(clientIp);
    loginDto.setUserAgent(request.getHeader("User-Agent"));

    log.info("用户登录请求：identifier={}, ip={}", loginDto.getLoginIdentifier(), clientIp);

    try {
      // 验证防重放攻击nonce token（如果启用）
      if (!replayAttackPreventionService.verifyNonce(
          loginDto.getNonce(), loginDto.getTimestamp(), clientIp)) {
        log.warn("登录请求防重放验证失败：ip={}", clientIp);
        return ResponseEntity.badRequest().body(ApiResult.failed("请求已过期或已被使用，请刷新页面重试"));
      }

      // 验证验证码（如果启用）
      if (!captchaService.verifyCaptcha(
          loginDto.getCaptchaId(), loginDto.getCaptchaAnswer(), clientIp)) {
        log.warn("登录请求验证码验证失败：ip={}", clientIp);
        return ResponseEntity.badRequest().body(ApiResult.failed("验证码错误或已过期，请重新获取"));
      }

      // 执行登录
      LoginResponseDto loginResponse = authenticationService.login(loginDto);

      log.info(
          "用户登录成功：userId={}, username={}",
          loginResponse.getUser().getId(),
          loginResponse.getUser().getUsername());

      return ResponseEntity.ok(ApiResult.success("登录成功", loginResponse));

    } catch (BadCredentialsException | IllegalArgumentException e) {
      log.warn("用户登录失败：{}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResult.unauthorized(e.getMessage()));
    } catch (RuntimeException e) {
      // 捕获RuntimeException，提取更详细的错误信息
      log.error("用户登录运行时异常：{}", e.getMessage(), e);
      String errorMessage = e.getMessage();
      if (e.getCause() != null && e.getCause().getMessage() != null) {
        errorMessage = e.getCause().getMessage();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed(errorMessage != null ? errorMessage : "登录失败，请稍后重试"));
    } catch (Exception e) {
      log.error("用户登录异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("登录失败，请稍后重试"));
    }
  }

  /** 刷新令牌 */
  @PostMapping("/refresh")
  @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
  public ResponseEntity<ApiResult<LoginResponseDto>> refreshToken(
      @Parameter(description = "刷新令牌请求", required = true) @RequestBody
          Map<String, String> request) {

    String refreshToken = request.get("refreshToken");
    log.info("刷新令牌请求");

    try {
      LoginResponseDto response = authenticationService.refreshToken(refreshToken);

      log.info("令牌刷新成功：userId={}", response.getUser().getId());
      return ResponseEntity.ok(ApiResult.success("令牌刷新成功", response));

    } catch (IllegalArgumentException e) {
      log.warn("令牌刷新失败：{}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResult.unauthorized(e.getMessage()));
    } catch (Exception e) {
      log.error("令牌刷新异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("令牌刷新失败"));
    }
  }

  /** 用户登出 */
  @PostMapping("/logout")
  @Operation(summary = "用户登出", description = "用户登出接口")
  public ResponseEntity<ApiResult<String>> logout(HttpServletRequest request) {

    String token = extractTokenFromRequest(request);
    log.info("用户登出请求");

    try {
      authenticationService.logout(token);

      log.info("用户登出成功");
      return ResponseEntity.ok(ApiResult.success("登出成功"));

    } catch (Exception e) {
      log.error("用户登出异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failed("登出失败"));
    }
  }

  /** 验证令牌有效性 */
  @GetMapping("/validate")
  @Operation(summary = "验证令牌", description = "验证当前令牌是否有效")
  public ResponseEntity<ApiResult<Map<String, Object>>> validateToken(HttpServletRequest request) {

    String token = extractTokenFromRequest(request);
    log.debug("验证令牌请求");

    try {
      boolean isValid = authenticationService.validateToken(token);
      String username = isValid ? authenticationService.getUsernameFromToken(token) : null;

      Map<String, Object> result =
          Map.of(
              "valid",
              isValid,
              "username",
              username != null ? username : "",
              "authenticated",
              authenticationService.isAuthenticated());

      return ResponseEntity.ok(ApiResult.success("令牌验证完成", result));

    } catch (Exception e) {
      log.error("令牌验证异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("令牌验证失败"));
    }
  }

  /** 检查用户名可用性 */
  @GetMapping("/check-username")
  @Operation(summary = "检查用户名", description = "检查用户名是否可用")
  public ResponseEntity<ApiResult<Map<String, Boolean>>> checkUsername(
      @Parameter(description = "要检查的用户名", required = true) @RequestParam String username) {

    log.debug("检查用户名可用性：username={}", username);

    try {
      boolean available = userService.isUsernameAvailable(username);

      Map<String, Boolean> result = Map.of("available", available);

      return ResponseEntity.ok(ApiResult.success("检查完成", result));

    } catch (Exception e) {
      log.error("用户名检查异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failed("检查失败"));
    }
  }

  /** 检查邮箱可用性 */
  @GetMapping("/check-email")
  @Operation(summary = "检查邮箱", description = "检查邮箱是否可用")
  public ResponseEntity<ApiResult<Map<String, Boolean>>> checkEmail(
      @Parameter(description = "要检查的邮箱", required = true) @RequestParam String email) {

    log.debug("检查邮箱可用性：email={}", email);

    try {
      boolean available = userService.isEmailAvailable(email);

      Map<String, Boolean> result = Map.of("available", available);

      return ResponseEntity.ok(ApiResult.success("检查完成", result));

    } catch (Exception e) {
      log.error("邮箱检查异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failed("检查失败"));
    }
  }

  /** 获取验证码 */
  @GetMapping("/captcha")
  @Operation(summary = "获取验证码", description = "获取数学验证码，用于注册和登录")
  public ResponseEntity<ApiResult<Map<String, String>>> getCaptcha(HttpServletRequest request) {
    String clientId = getClientIp(request);
    log.debug("获取验证码请求：clientId={}", clientId);

    try {
      Map<String, String> captcha = captchaService.generateCaptcha(clientId);
      return ResponseEntity.ok(ApiResult.success("验证码生成成功", captcha));
    } catch (Exception e) {
      log.error("验证码生成异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("验证码生成失败"));
    }
  }

  /** 获取防重放攻击nonce token */
  @GetMapping("/nonce")
  @Operation(summary = "获取nonce token", description = "获取一次性nonce token，用于防止重放攻击")
  public ResponseEntity<ApiResult<Map<String, String>>> getNonce(HttpServletRequest request) {
    String clientId = getClientIp(request);
    log.debug("获取nonce token请求：clientId={}", clientId);

    try {
      Map<String, String> nonce = replayAttackPreventionService.generateNonce(clientId);
      return ResponseEntity.ok(ApiResult.success("nonce token生成成功", nonce));
    } catch (Exception e) {
      log.error("nonce token生成异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("nonce token生成失败"));
    }
  }

  /** 获取安全功能配置 */
  @GetMapping("/security-config")
  @Operation(summary = "获取安全配置", description = "获取验证码和防重放攻击的配置状态")
  public ResponseEntity<ApiResult<Map<String, Boolean>>> getSecurityConfig() {
    log.debug("获取安全配置请求");

    try {
      Map<String, Boolean> config =
          Map.of(
              "captchaEnabled", securityFeaturesConfig.getCaptcha().isEnabled(),
              "replayPreventionEnabled", securityFeaturesConfig.getReplayPrevention().isEnabled());
      return ResponseEntity.ok(ApiResult.success("配置获取成功", config));
    } catch (Exception e) {
      log.error("获取安全配置异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("配置获取失败"));
    }
  }

  /** 发送邮箱验证码 */
  @PostMapping("/send-verification-code")
  @Operation(summary = "发送验证码", description = "发送邮箱验证码")
  public ResponseEntity<ApiResult<String>> sendVerificationCode(
      @Parameter(description = "发送验证码请求", required = true) @RequestBody
          Map<String, String> request) {

    String email = request.get("email");
    log.info("发送邮箱验证码请求：email={}", email);

    try {
      userService.sendEmailVerificationCode(email);

      return ResponseEntity.ok(ApiResult.success("验证码发送成功"));

    } catch (IllegalArgumentException e) {
      log.warn("验证码发送失败：{}", e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("验证码发送异常：{}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.failed("验证码发送失败"));
    }
  }

  // 忘记密码功能暂时注释掉 - 2025-01-15
  /** 重置密码 */
  // @PostMapping("/reset-password")
  // @Operation(summary = "重置密码", description = "通过邮箱重置密码")
  // public ResponseEntity<ApiResult<String>> resetPassword(
  //     @Parameter(description = "重置密码请求", required = true) @RequestBody
  //         Map<String, String> request) {

  //   String email = request.get("email");
  //   log.info("重置密码请求：email={}", email);

  //   try {
  //     userService.resetPassword(email);

  //     return ResponseEntity.ok(ApiResult.success("密码重置成功，新密码已发送到您的邮箱"));

  //   } catch (IllegalArgumentException e) {
  //     log.warn("密码重置失败：{}", e.getMessage());
  //     return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
  //   } catch (Exception e) {
  //     log.error("密码重置异常：{}", e.getMessage(), e);
  //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
  //         .body(ApiResult.failed("密码重置失败"));
  //   }
  // }

  // ===========================
  // 私有辅助方法
  // ===========================

  /** 获取客户端IP地址 */
  private String getClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null
        && !xForwardedFor.isEmpty()
        && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }

  /** 从请求中提取JWT令牌 */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
