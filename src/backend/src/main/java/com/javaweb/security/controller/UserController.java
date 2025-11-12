package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.dto.user.UserResponseDto;
import com.javaweb.security.dto.user.UserUpdateDto;
import com.javaweb.security.entity.User;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.FileService;
import com.javaweb.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户管理控制器
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户管理", description = "用户信息管理相关接口")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;
  private final AuthenticationService authenticationService;
  private final FileService fileService;

  /** 获取当前用户信息 */
  @GetMapping("/profile")
  @Operation(summary = "获取个人信息", description = "获取当前登录用户的详细信息")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<UserResponseDto>> getCurrentUserProfile() {

    Long currentUserId = authenticationService.getCurrentUserId();
    log.info("获取当前用户信息：userId={}", currentUserId);

    try {
      UserResponseDto user = userService.getFreshUserProfile(currentUserId);
      return ResponseEntity.ok(ApiResult.success("获取成功", user));
    } catch (IllegalArgumentException e) {
      log.warn("获取用户信息失败：userId={}, reason={}", currentUserId, e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("获取用户信息异常：userId={}, error={}", currentUserId, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("获取用户信息失败"));
    }
  }

  /** 更新当前用户信息 */
  @PutMapping("/profile")
  @Operation(summary = "更新个人信息", description = "更新当前登录用户的个人信息")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<UserResponseDto>> updateCurrentUserProfile(
      @Parameter(description = "用户更新信息", required = true) @Valid @RequestBody
          UserUpdateDto updateDto) {

    Long currentUserId = authenticationService.getCurrentUserId();
    log.info("更新用户信息：userId={}", currentUserId);

    try {
      UserResponseDto updatedUser = userService.updateUser(currentUserId, updateDto);

      log.info("用户信息更新成功：userId={}", currentUserId);
      return ResponseEntity.ok(ApiResult.success("更新成功", updatedUser));

    } catch (IllegalArgumentException e) {
      log.warn("用户信息更新失败：userId={}, reason={}", currentUserId, e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("用户信息更新异常：userId={}, error={}", currentUserId, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("更新失败"));
    }
  }

  /** 修改当前用户密码 */
  @PutMapping("/password")
  @Operation(summary = "修改密码", description = "修改当前用户密码")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<String>> changePassword(
      @Parameter(description = "密码修改信息", required = true) @RequestBody
          Map<String, String> request) {

    Long currentUserId = authenticationService.getCurrentUserId();
    String oldPassword = request.get("oldPassword");
    String newPassword = request.get("newPassword");
    String confirmPassword = request.get("confirmPassword");

    log.info("修改用户密码：userId={}", currentUserId);

    try {
      // 验证参数是否为空
      if (oldPassword == null || oldPassword.trim().isEmpty()) {
        return ResponseEntity.badRequest().body(ApiResult.failed("当前密码不能为空"));
      }
      if (newPassword == null || newPassword.trim().isEmpty()) {
        return ResponseEntity.badRequest().body(ApiResult.failed("新密码不能为空"));
      }
      if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
        return ResponseEntity.badRequest().body(ApiResult.failed("确认密码不能为空"));
      }

      // 验证新密码和确认密码是否一致
      if (!newPassword.equals(confirmPassword)) {
        return ResponseEntity.badRequest().body(ApiResult.failed("新密码和确认密码不一致"));
      }

      userService.changePassword(currentUserId, oldPassword, newPassword);

      log.info("用户密码修改成功：userId={}", currentUserId);
      return ResponseEntity.ok(ApiResult.success("密码修改成功"));

    } catch (IllegalArgumentException e) {
      log.warn("密码修改失败：userId={}, reason={}", currentUserId, e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("密码修改异常：userId={}, error={}", currentUserId, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("密码修改失败"));
    }
  }

  /** 验证邮箱 */
  @PostMapping("/verify-email")
  @Operation(summary = "验证邮箱", description = "通过验证码验证用户邮箱")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<String>> verifyEmail(
      @Parameter(description = "邮箱验证信息", required = true) @RequestBody
          Map<String, String> request) {

    Long currentUserId = authenticationService.getCurrentUserId();
    String verificationCode = request.get("verificationCode");

    log.info("验证用户邮箱：userId={}", currentUserId);

    try {
      userService.verifyEmail(currentUserId, verificationCode);

      log.info("用户邮箱验证成功：userId={}", currentUserId);
      return ResponseEntity.ok(ApiResult.success("邮箱验证成功"));

    } catch (IllegalArgumentException e) {
      log.warn("邮箱验证失败：userId={}, reason={}", currentUserId, e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("邮箱验证异常：userId={}, error={}", currentUserId, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("邮箱验证失败"));
    }
  }

  /** 根据ID获取用户信息（公开信息） */
  @GetMapping("/{userId}")
  @Operation(summary = "获取用户信息", description = "根据用户ID获取用户公开信息")
  public ResponseEntity<ApiResult<UserResponseDto>> getUserById(
      @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {

    log.debug("获取用户信息：userId={}", userId);

    try {
      Optional<UserResponseDto> userOpt = userService.findById(userId);

      if (userOpt.isPresent()) {
        UserResponseDto user = userOpt.get();
        // 隐藏敏感信息（只返回公开信息）
        user.setEmail(null);

        return ResponseEntity.ok(ApiResult.success("获取成功", user));
      } else {
        return ResponseEntity.notFound().build();
      }

    } catch (Exception e) {
      log.error("获取用户信息异常：userId={}, error={}", userId, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("获取用户信息失败"));
    }
  }

  /** 分页查询用户列表 */
  @GetMapping("")
  @Operation(summary = "分页查询用户", description = "分页查询用户列表（支持筛选）")
  public ResponseEntity<ApiResult<Page<UserResponseDto>>> getUsers(
      @Parameter(description = "用户名（模糊查询）") @RequestParam(required = false) String username,
      @Parameter(description = "邮箱（模糊查询）") @RequestParam(required = false) String email,
      @Parameter(description = "用户角色") @RequestParam(required = false) User.UserRole userRole,
      @Parameter(description = "用户状态") @RequestParam(required = false) User.UserStatus userStatus,
      @Parameter(description = "页码（从0开始）") @RequestParam(defaultValue = "0") @Min(0) int page,
      @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) @Max(100)
          int size,
      @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sortBy,
      @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {

    log.debug("分页查询用户：page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);

    try {
      Sort.Direction direction =
          "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
      Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

      Page<UserResponseDto> users =
          userService.findUsers(username, email, userRole, userStatus, pageable);

      // 隐藏敏感信息
      users.getContent().forEach(user -> user.setEmail(null));

      return ResponseEntity.ok(ApiResult.success("查询成功", users));

    } catch (Exception e) {
      log.error("分页查询用户异常：error={}", e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("查询失败"));
    }
  }

  /** 搜索用户 */
  @GetMapping("/search")
  @Operation(summary = "搜索用户", description = "根据关键词搜索用户")
  public ResponseEntity<ApiResult<Page<UserResponseDto>>> searchUsers(
      @Parameter(description = "搜索关键词", required = true)
          @RequestParam
          @NotBlank
          @Size(min = 2, max = 50)
          String keyword,
      @Parameter(description = "页码（从0开始）") @RequestParam(defaultValue = "0") @Min(0) int page,
      @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") @Min(1) @Max(100)
          int size) {

    log.debug("搜索用户：keyword={}, page={}, size={}", keyword, page, size);

    try {
      Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
      Page<UserResponseDto> users = userService.searchUsers(keyword, pageable);

      // 隐藏敏感信息
      users.getContent().forEach(user -> user.setEmail(null));

      return ResponseEntity.ok(ApiResult.success("搜索成功", users));

    } catch (Exception e) {
      log.error("搜索用户异常：keyword={}, error={}", keyword, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("搜索失败"));
    }
  }

  /** 获取用户排行榜 */
  @GetMapping("/ranking")
  @Operation(summary = "用户排行榜", description = "获取用户排行榜")
  public ResponseEntity<ApiResult<List<UserResponseDto>>> getUserRanking(
      @Parameter(
              description =
                  "排行榜类型（points: 积分, studytime: 学习时间, vulnerabilities: 完成漏洞数, streak: 连续学习天数）")
          @RequestParam(defaultValue = "points")
          String type,
      @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") @Min(1) @Max(50)
          int limit) {

    log.debug("获取用户排行榜：type={}, limit={}", type, limit);

    try {
      List<UserResponseDto> ranking = userService.getUserRanking(type, limit);

      // 隐藏敏感信息
      ranking.forEach(user -> user.setEmail(null));

      return ResponseEntity.ok(ApiResult.success("获取成功", ranking));

    } catch (IllegalArgumentException e) {
      log.warn("获取排行榜失败：type={}, reason={}", type, e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("获取排行榜异常：type={}, error={}", type, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("获取排行榜失败"));
    }
  }

  /** 获取用户统计信息 */
  @GetMapping("/stats")
  @Operation(summary = "用户统计", description = "获取用户统计信息")
  public ResponseEntity<ApiResult<UserService.UserStatsDto>> getUserStats() {

    log.debug("获取用户统计信息");

    try {
      UserService.UserStatsDto stats = userService.getUserStats();

      return ResponseEntity.ok(ApiResult.success("获取成功", stats));

    } catch (Exception e) {
      log.error("获取用户统计异常：error={}", e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("获取统计信息失败"));
    }
  }

  /** 上传用户头像 */
  @PostMapping("/avatar")
  @Operation(summary = "上传头像", description = "上传当前用户的头像")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<UserResponseDto>> uploadAvatar(
      @Parameter(description = "头像文件", required = true) @RequestParam("avatar")
          MultipartFile file) {

    Long currentUserId = authenticationService.getCurrentUserId();
    log.info("上传用户头像：userId={}", currentUserId);

    try {
      // 使用FileService上传头像
      String avatarUrl = fileService.uploadAvatar(file, currentUserId);

      // 更新用户的头像URL
      UserUpdateDto updateDto = new UserUpdateDto();
      updateDto.setAvatarUrl(avatarUrl);
      UserResponseDto updatedUser = userService.updateUser(currentUserId, updateDto);

      log.info("用户头像上传成功：userId={}, avatarUrl={}", currentUserId, avatarUrl);
      return ResponseEntity.ok(ApiResult.success("头像上传成功", updatedUser));

    } catch (IllegalArgumentException e) {
      log.warn("头像上传失败：userId={}, reason={}", currentUserId, e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error("头像上传异常：userId={}, error={}", currentUserId, e.getMessage(), e);
      return ResponseEntity.internalServerError().body(ApiResult.failed("头像上传失败"));
    }
  }
}
