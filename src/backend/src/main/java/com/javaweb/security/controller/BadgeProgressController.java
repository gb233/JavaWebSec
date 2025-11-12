package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.dto.BadgeProgressDto;
import com.javaweb.security.entity.BadgeProgress;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.BadgeProgressService;
import com.javaweb.security.service.impl.BadgeProgressServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 徽章进度控制器 */
@Slf4j
@RestController
@RequestMapping("/api/v1/badge-progress")
@Tag(name = "徽章进度管理", description = "徽章进度相关API")
@RequiredArgsConstructor
public class BadgeProgressController {

  private final BadgeProgressService badgeProgressService;
  private final BadgeProgressServiceImpl badgeProgressServiceImpl;
  private final AuthenticationService authenticationService;

  @GetMapping("/user")
  @Operation(summary = "获取用户徽章进度", description = "获取当前用户的所有徽章进度")
  public ApiResult<List<BadgeProgress>> getUserBadgeProgress(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<BadgeProgress> progress = badgeProgressService.getUserBadgeProgress(userId);
    return ApiResult.success(progress);
  }

  @GetMapping("/user/details")
  @Operation(summary = "获取用户徽章进度详情", description = "获取当前用户的所有徽章进度（包含徽章详细信息）")
  public ApiResult<List<BadgeProgressDto>> getUserBadgeProgressWithDetails(
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<BadgeProgressDto> progress =
        badgeProgressServiceImpl.getUserBadgeProgressWithDetails(userId);
    return ApiResult.success(progress);
  }

  @GetMapping("/user/category/{category}")
  @Operation(summary = "获取用户分类徽章进度", description = "获取当前用户指定分类的徽章进度")
  public ApiResult<List<BadgeProgress>> getUserBadgeProgressByCategory(
      @Parameter(description = "徽章分类") @PathVariable String category,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<BadgeProgress> progress =
        badgeProgressService.getUserBadgeProgressByCategory(userId, category);
    return ApiResult.success(progress);
  }

  @GetMapping("/user/uncompleted")
  @Operation(summary = "获取用户未完成徽章进度", description = "获取当前用户未完成的徽章进度")
  public ApiResult<List<BadgeProgress>> getUserUncompletedBadgeProgress(
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<BadgeProgress> progress = badgeProgressService.getUserUncompletedBadgeProgress(userId);
    return ApiResult.success(progress);
  }

  @GetMapping("/user/completed")
  @Operation(summary = "获取用户已完成徽章进度", description = "获取当前用户已完成的徽章进度")
  public ApiResult<List<BadgeProgress>> getUserCompletedBadgeProgress(
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<BadgeProgress> progress = badgeProgressService.getUserCompletedBadgeProgress(userId);
    return ApiResult.success(progress);
  }

  @PostMapping("/update")
  @Operation(summary = "更新徽章进度", description = "更新指定徽章的进度")
  public ApiResult<BadgeProgress> updateBadgeProgress(
      @Parameter(description = "徽章ID") @RequestParam Long badgeId,
      @Parameter(description = "进度值") @RequestParam Integer progress,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    BadgeProgress badgeProgress =
        badgeProgressService.updateBadgeProgress(userId, badgeId, progress);
    if (badgeProgress == null) {
      return ApiResult.error("徽章进度不存在");
    }
    return ApiResult.success(badgeProgress);
  }

  @PostMapping("/increment")
  @Operation(summary = "增加徽章进度", description = "增加指定徽章的进度")
  public ApiResult<BadgeProgress> incrementBadgeProgress(
      @Parameter(description = "徽章ID") @RequestParam Long badgeId,
      @Parameter(description = "增加量") @RequestParam Integer increment,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    BadgeProgress badgeProgress =
        badgeProgressService.incrementBadgeProgress(userId, badgeId, increment);
    if (badgeProgress == null) {
      return ApiResult.error("徽章进度不存在");
    }
    return ApiResult.success(badgeProgress);
  }

  @GetMapping("/percentage/{badgeId}")
  @Operation(summary = "获取徽章进度百分比", description = "获取指定徽章的进度百分比")
  public ApiResult<Double> getBadgeProgressPercentage(
      @Parameter(description = "徽章ID") @PathVariable Long badgeId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    Double percentage = badgeProgressService.getBadgeProgressPercentage(userId, badgeId);
    return ApiResult.success(percentage);
  }

  @GetMapping("/stats")
  @Operation(summary = "获取用户徽章进度统计", description = "获取当前用户的徽章进度统计信息")
  public ApiResult<Map<String, Object>> getUserBadgeProgressStats(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    Map<String, Object> stats = badgeProgressService.getUserBadgeProgressStats(userId);
    return ApiResult.success(stats);
  }

  @GetMapping("/check/{badgeId}")
  @Operation(summary = "检查徽章是否完成", description = "检查指定徽章是否已完成")
  public ApiResult<Boolean> isBadgeCompleted(
      @Parameter(description = "徽章ID") @PathVariable Long badgeId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    boolean isCompleted = badgeProgressService.isBadgeCompleted(userId, badgeId);
    return ApiResult.success(isCompleted);
  }

  private Long getCurrentUserId(Authentication authentication) {
    // 优先使用AuthenticationService获取用户ID
    Long userId = authenticationService.getCurrentUserId();
    if (userId != null) {
      log.info("从AuthenticationService获取的用户ID: {}", userId);
      return userId;
    }

    // 备用方案：从Authentication对象获取
    if (authentication != null && authentication.getPrincipal() != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
        String username =
            ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        log.info("当前用户: {}", username);
        userId = authenticationService.getCurrentUserId();
        if (userId != null) {
          return userId;
        }
      }
    }

    log.warn("无法获取当前用户ID，使用默认用户ID: 1");
    return 1L; // 默认返回用户ID 1
  }
}
