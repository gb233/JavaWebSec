package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.common.result.PageResult;
import com.javaweb.security.dto.admin.AdminUserDto;
import com.javaweb.security.dto.admin.SystemLogDto;
import com.javaweb.security.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台控制器
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "管理后台", description = "管理后台相关接口")
public class AdminController {

  private final AdminService adminService;

  /** 获取管理员用户列表 */
  @GetMapping("/users")
  @Operation(summary = "获取管理员用户列表", description = "获取管理员用户列表")
  public ResponseEntity<ApiResult<PageResult<AdminUserDto>>> getAdminUsers(
      @Parameter(description = "角色") @RequestParam(required = false) String role,
      @Parameter(description = "是否激活") @RequestParam(required = false) Boolean isActive,
      @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {

    try {
      PageResult<AdminUserDto> result = adminService.getAdminUsers(role, isActive, page, size);
      return ResponseEntity.ok(ApiResult.success("获取管理员用户列表成功", result));
    } catch (Exception e) {
      log.error("获取管理员用户列表失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("获取管理员用户列表失败: " + e.getMessage()));
    }
  }

  /** 创建管理员用户 */
  @PostMapping("/users")
  @Operation(summary = "创建管理员用户", description = "创建新的管理员用户")
  public ResponseEntity<ApiResult<AdminUserDto>> createAdminUser(
      @Parameter(description = "管理员用户信息") @RequestBody AdminUserDto adminUserDto) {

    try {
      AdminUserDto result = adminService.createAdminUser(adminUserDto);
      return ResponseEntity.ok(ApiResult.success("创建管理员用户成功", result));
    } catch (Exception e) {
      log.error("创建管理员用户失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("创建管理员用户失败: " + e.getMessage()));
    }
  }

  /** 更新管理员用户 */
  @PutMapping("/users/{id}")
  @Operation(summary = "更新管理员用户", description = "更新管理员用户信息")
  public ResponseEntity<ApiResult<AdminUserDto>> updateAdminUser(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Parameter(description = "管理员用户信息") @RequestBody AdminUserDto adminUserDto) {

    try {
      AdminUserDto result = adminService.updateAdminUser(id, adminUserDto);
      return ResponseEntity.ok(ApiResult.success("更新管理员用户成功", result));
    } catch (Exception e) {
      log.error("更新管理员用户失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("更新管理员用户失败: " + e.getMessage()));
    }
  }

  /** 删除管理员用户 */
  @DeleteMapping("/users/{id}")
  @Operation(summary = "删除管理员用户", description = "删除管理员用户")
  public ResponseEntity<ApiResult<Void>> deleteAdminUser(
      @Parameter(description = "用户ID") @PathVariable Long id) {

    try {
      adminService.deleteAdminUser(id);
      return ResponseEntity.ok(ApiResult.success("删除管理员用户成功"));
    } catch (Exception e) {
      log.error("删除管理员用户失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("删除管理员用户失败: " + e.getMessage()));
    }
  }

  /** 获取系统日志 */
  @GetMapping("/logs")
  @Operation(summary = "获取系统日志", description = "获取系统日志列表")
  public ResponseEntity<ApiResult<PageResult<SystemLogDto>>> getSystemLogs(
      @Parameter(description = "日志级别") @RequestParam(required = false) String level,
      @Parameter(description = "模块") @RequestParam(required = false) String module,
      @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
      @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {

    try {
      PageResult<SystemLogDto> result =
          adminService.getSystemLogs(level, module, userId, page, size);
      return ResponseEntity.ok(ApiResult.success("获取系统日志成功", result));
    } catch (Exception e) {
      log.error("获取系统日志失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("获取系统日志失败: " + e.getMessage()));
    }
  }

  /** 获取系统统计 */
  @GetMapping("/stats/system")
  @Operation(summary = "获取系统统计", description = "获取系统统计数据")
  public ResponseEntity<ApiResult<Map<String, Object>>> getSystemStats() {

    try {
      Map<String, Object> stats = adminService.getSystemStats();
      return ResponseEntity.ok(ApiResult.success("获取系统统计成功", stats));
    } catch (Exception e) {
      log.error("获取系统统计失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("获取系统统计失败: " + e.getMessage()));
    }
  }

  /** 获取用户统计 */
  @GetMapping("/stats/users")
  @Operation(summary = "获取用户统计", description = "获取用户统计数据")
  public ResponseEntity<ApiResult<Map<String, Object>>> getUserStats() {

    try {
      Map<String, Object> stats = adminService.getUserStats();
      return ResponseEntity.ok(ApiResult.success("获取用户统计成功", stats));
    } catch (Exception e) {
      log.error("获取用户统计失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("获取用户统计失败: " + e.getMessage()));
    }
  }

  /** 获取学习统计 */
  @GetMapping("/stats/learning")
  @Operation(summary = "获取学习统计", description = "获取学习统计数据")
  public ResponseEntity<ApiResult<Map<String, Object>>> getLearningStats() {

    try {
      Map<String, Object> stats = adminService.getLearningStats();
      return ResponseEntity.ok(ApiResult.success("获取学习统计成功", stats));
    } catch (Exception e) {
      log.error("获取学习统计失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("获取学习统计失败: " + e.getMessage()));
    }
  }

  /** 获取测试统计 */
  @GetMapping("/stats/tests")
  @Operation(summary = "获取测试统计", description = "获取测试统计数据")
  public ResponseEntity<ApiResult<Map<String, Object>>> getTestStats() {

    try {
      Map<String, Object> stats = adminService.getTestStats();
      return ResponseEntity.ok(ApiResult.success("获取测试统计成功", stats));
    } catch (Exception e) {
      log.error("获取测试统计失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("获取测试统计失败: " + e.getMessage()));
    }
  }

  /** 获取挑战统计 */
  @GetMapping("/stats/challenges")
  @Operation(summary = "获取挑战统计", description = "获取挑战统计数据")
  public ResponseEntity<ApiResult<Map<String, Object>>> getChallengeStats() {

    try {
      Map<String, Object> stats = adminService.getChallengeStats();
      return ResponseEntity.ok(ApiResult.success("获取挑战统计成功", stats));
    } catch (Exception e) {
      log.error("获取挑战统计失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("获取挑战统计失败: " + e.getMessage()));
    }
  }

  /** 清理过期日志 */
  @PostMapping("/logs/clean")
  @Operation(summary = "清理过期日志", description = "清理指定天数前的过期日志")
  public ResponseEntity<ApiResult<Void>> cleanExpiredLogs(
      @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") int days) {

    try {
      adminService.cleanExpiredLogs(days);
      return ResponseEntity.ok(ApiResult.success("清理过期日志成功"));
    } catch (Exception e) {
      log.error("清理过期日志失败", e);
      return ResponseEntity.badRequest().body(ApiResult.failed("清理过期日志失败: " + e.getMessage()));
    }
  }
}
