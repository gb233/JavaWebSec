package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.UserActivity;
import com.javaweb.security.service.UserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户活动控制器
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user-activities")
@RequiredArgsConstructor
@Tag(name = "用户活动", description = "用户活动记录相关接口")
public class UserActivityController {

  private final UserActivityService userActivityService;

  @GetMapping("/recent/{userId}")
  @Operation(summary = "获取最近活动", description = "获取用户最近的活动记录")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<List<UserActivity>>> getRecentActivities(
      @PathVariable Long userId, @RequestParam(defaultValue = "10") int limit) {
    log.info("获取最近活动: userId={}, limit={}", userId, limit);

    List<UserActivity> activities = userActivityService.getRecentActivities(userId, limit);

    return ResponseEntity.ok(ApiResult.success(activities));
  }

  @GetMapping("/statistics/{userId}")
  @Operation(summary = "获取活动统计", description = "获取用户活动统计信息")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Map<String, Object>>> getActivityStatistics(
      @PathVariable Long userId) {
    log.info("获取活动统计: userId={}", userId);

    Map<String, Object> statistics = userActivityService.getActivityStatistics(userId);

    return ResponseEntity.ok(ApiResult.success(statistics));
  }

  @PostMapping("/learning-completed")
  @Operation(summary = "记录学习完成活动", description = "记录用户学习完成活动")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> recordLearningCompleted(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam Integer studyTime,
      @RequestParam Integer score) {
    log.info(
        "记录学习完成活动: userId={}, vulnerabilityCode={}, studyTime={}, score={}",
        userId,
        vulnerabilityCode,
        studyTime,
        score);

    userActivityService.recordLearningCompleted(userId, vulnerabilityCode, studyTime, score);

    return ResponseEntity.ok(ApiResult.success("学习完成活动记录成功"));
  }

  @PostMapping("/test-passed")
  @Operation(summary = "记录测试通过活动", description = "记录用户测试通过活动")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> recordTestPassed(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam Integer score,
      @RequestParam Double accuracy) {
    log.info(
        "记录测试通过活动: userId={}, vulnerabilityCode={}, score={}, accuracy={}",
        userId,
        vulnerabilityCode,
        score,
        accuracy);

    userActivityService.recordTestPassed(userId, vulnerabilityCode, score, accuracy);

    return ResponseEntity.ok(ApiResult.success("测试通过活动记录成功"));
  }

  @PostMapping("/challenge-completed")
  @Operation(summary = "记录挑战完成活动", description = "记录用户挑战完成活动")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> recordChallengeCompleted(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam Integer score,
      @RequestParam(required = false) String badge) {
    log.info(
        "记录挑战完成活动: userId={}, vulnerabilityCode={}, score={}, badge={}",
        userId,
        vulnerabilityCode,
        score,
        badge);

    userActivityService.recordChallengeCompleted(userId, vulnerabilityCode, score, badge);

    return ResponseEntity.ok(ApiResult.success("挑战完成活动记录成功"));
  }
}
