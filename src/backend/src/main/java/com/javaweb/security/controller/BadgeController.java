package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.AchievementBadge;
import com.javaweb.security.entity.UserBadge;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.BadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 徽章控制器 */
@Slf4j
@RestController
@RequestMapping("/api/v1/badges")
@Tag(name = "徽章管理", description = "徽章相关API")
@RequiredArgsConstructor
public class BadgeController {

  private final BadgeService badgeService;
  private final AuthenticationService authenticationService;

  @GetMapping
  @Operation(summary = "获取所有徽章", description = "获取系统中所有可用的徽章")
  public ApiResult<List<AchievementBadge>> getAllBadges() {
    List<AchievementBadge> badges = badgeService.getAllBadges();
    return ApiResult.success(badges);
  }

  @GetMapping("/category/{category}")
  @Operation(summary = "根据分类获取徽章", description = "根据分类获取徽章列表")
  public ApiResult<List<AchievementBadge>> getBadgesByCategory(
      @Parameter(description = "徽章分类") @PathVariable String category) {
    List<AchievementBadge> badges = badgeService.getBadgesByCategory(category);
    return ApiResult.success(badges);
  }

  @GetMapping("/code/{badgeCode}")
  @Operation(summary = "根据代码获取徽章", description = "根据徽章代码获取徽章信息")
  public ApiResult<AchievementBadge> getBadgeByCode(
      @Parameter(description = "徽章代码") @PathVariable String badgeCode) {
    AchievementBadge badge = badgeService.getBadgeByCode(badgeCode);
    if (badge == null) {
      return ApiResult.error("徽章不存在");
    }
    return ApiResult.success(badge);
  }

  @GetMapping("/user")
  @Operation(summary = "获取用户徽章", description = "获取当前用户的所有徽章")
  public ApiResult<List<UserBadge>> getUserBadges(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<UserBadge> userBadges = badgeService.getUserBadges(userId);
    return ApiResult.success(userBadges);
  }

  @GetMapping("/user/category/{category}")
  @Operation(summary = "获取用户分类徽章", description = "获取当前用户指定分类的徽章")
  public ApiResult<List<UserBadge>> getUserBadgesByCategory(
      @Parameter(description = "徽章分类") @PathVariable String category,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<UserBadge> userBadges = badgeService.getUserBadgesByCategory(userId, category);
    return ApiResult.success(userBadges);
  }

  @GetMapping("/user/recent")
  @Operation(summary = "获取用户最近徽章", description = "获取当前用户最近获得的徽章")
  public ApiResult<List<UserBadge>> getUserRecentBadges(
      @Parameter(description = "限制数量") @RequestParam(defaultValue = "5") int limit,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<UserBadge> recentBadges = badgeService.getUserRecentBadges(userId, limit);
    return ApiResult.success(recentBadges);
  }

  @GetMapping("/user/stats")
  @Operation(summary = "获取用户徽章统计", description = "获取当前用户的徽章统计信息")
  public ApiResult<Map<String, Object>> getUserBadgeStats(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    Map<String, Object> stats = badgeService.getUserBadgeStats(userId);
    return ApiResult.success(stats);
  }

  @PostMapping("/award/{badgeCode}")
  @Operation(summary = "颁发徽章", description = "给当前用户颁发指定徽章")
  public ApiResult<UserBadge> awardBadge(
      @Parameter(description = "徽章代码") @PathVariable String badgeCode,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    UserBadge userBadge = badgeService.awardBadgeToUserByCode(userId, badgeCode);
    if (userBadge == null) {
      return ApiResult.error("徽章不存在或已获得");
    }
    return ApiResult.success(userBadge);
  }

  @GetMapping("/check/{badgeCode}")
  @Operation(summary = "检查徽章", description = "检查当前用户是否已获得指定徽章")
  public ApiResult<Boolean> checkBadge(
      @Parameter(description = "徽章代码") @PathVariable String badgeCode,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    boolean hasBadge = badgeService.hasUserEarnedBadgeByCode(userId, badgeCode);
    return ApiResult.success(hasBadge);
  }

  @GetMapping("/user/points")
  @Operation(summary = "获取用户徽章积分总和", description = "获取当前用户所有徽章的积分总和")
  public ApiResult<Integer> getUserBadgePoints(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    Map<String, Object> stats = badgeService.getUserBadgeStats(userId);
    Integer totalBadgePoints = (Integer) stats.get("totalBadgePoints");
    return ApiResult.success(totalBadgePoints != null ? totalBadgePoints : 0);
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
