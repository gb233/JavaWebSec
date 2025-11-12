package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.service.BadgeNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 徽章通知控制器 */
@RestController
@RequestMapping("/api/v1/badge-notifications")
@Tag(name = "徽章通知管理", description = "徽章通知相关API")
public class BadgeNotificationController {

  @Autowired private BadgeNotificationService notificationService;

  @PostMapping("/test/{badgeId}")
  @Operation(summary = "测试徽章通知", description = "发送测试徽章通知")
  public ApiResult<String> testBadgeNotification(
      @Parameter(description = "徽章ID") @PathVariable Long badgeId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // TODO: 实现测试通知逻辑
    return ApiResult.success("测试通知已发送");
  }

  @PostMapping("/progress/{badgeId}")
  @Operation(summary = "发送进度通知", description = "发送徽章进度更新通知")
  public ApiResult<String> sendProgressNotification(
      @Parameter(description = "徽章ID") @PathVariable Long badgeId,
      @Parameter(description = "进度值") @RequestParam Integer progress,
      @Parameter(description = "目标值") @RequestParam Integer target,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    notificationService.notifyBadgeProgressUpdate(userId, badgeId, progress, target);
    return ApiResult.success("进度通知已发送");
  }

  @PostMapping("/near-completion/{badgeId}")
  @Operation(summary = "发送即将完成通知", description = "发送徽章即将完成通知")
  public ApiResult<String> sendNearCompletionNotification(
      @Parameter(description = "徽章ID") @PathVariable Long badgeId,
      @Parameter(description = "进度值") @RequestParam Integer progress,
      @Parameter(description = "目标值") @RequestParam Integer target,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    notificationService.notifyBadgeNearCompletion(userId, badgeId, progress, target);
    return ApiResult.success("即将完成通知已发送");
  }

  @PostMapping("/milestone")
  @Operation(summary = "发送里程碑通知", description = "发送徽章里程碑通知")
  public ApiResult<String> sendMilestoneNotification(
      @Parameter(description = "里程碑名称") @RequestParam String milestone,
      @Parameter(description = "里程碑值") @RequestParam String value,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    notificationService.notifyBadgeMilestone(userId, milestone, value);
    return ApiResult.success("里程碑通知已发送");
  }

  @PostMapping("/stats")
  @Operation(summary = "发送统计更新通知", description = "发送徽章统计更新通知")
  public ApiResult<String> sendStatsUpdateNotification(
      @Parameter(description = "统计信息") @RequestBody Map<String, Object> stats,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    notificationService.notifyBadgeStatsUpdate(userId, stats);
    return ApiResult.success("统计更新通知已发送");
  }

  private Long getCurrentUserId(Authentication authentication) {
    // TODO: 从认证信息中获取用户ID
    return 1L; // 临时返回固定值
  }
}
