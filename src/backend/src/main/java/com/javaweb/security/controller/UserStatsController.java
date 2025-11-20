package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.service.UserStatsUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户统计更新控制器
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user-stats")
@RequiredArgsConstructor
@Tag(name = "用户统计", description = "用户学习统计更新相关接口")
public class UserStatsController {

  private final UserStatsUpdateService userStatsUpdateService;

  @PostMapping("/vulnerability")
  @Operation(summary = "更新漏洞学习统计", description = "更新用户漏洞学习统计信息")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> updateVulnerabilityStats(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam Integer studyTime,
      @RequestParam Integer points) {
    log.info(
        "更新漏洞学习统计: userId={}, vulnerabilityCode={}, studyTime={}, points={}",
        userId,
        vulnerabilityCode,
        studyTime,
        points);

    try {
      // 参数验证
      if (userId == null) {
        return ResponseEntity.badRequest().body(ApiResult.failed("用户ID不能为空"));
      }
      if (vulnerabilityCode == null || vulnerabilityCode.trim().isEmpty()) {
        return ResponseEntity.badRequest().body(ApiResult.failed("漏洞代码不能为空"));
      }
      if (studyTime == null || studyTime < 0) {
        return ResponseEntity.badRequest().body(ApiResult.failed("学习时长必须大于等于0"));
      }
      if (points == null || points < 0) {
        return ResponseEntity.badRequest().body(ApiResult.failed("积分必须大于等于0"));
      }

      userStatsUpdateService.updateVulnerabilityStats(userId, vulnerabilityCode, studyTime, points);

      return ResponseEntity.ok(ApiResult.success("漏洞学习统计更新成功"));
    } catch (IllegalArgumentException e) {
      log.warn("更新漏洞学习统计参数错误: {}", e.getMessage());
      return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
    } catch (Exception e) {
      log.error(
          "更新漏洞学习统计失败: userId={}, vulnerabilityCode={}, studyTime={}, points={}, error={}",
          userId,
          vulnerabilityCode,
          studyTime,
          points,
          e.getMessage(),
          e);
      return ResponseEntity.internalServerError()
          .body(ApiResult.failed("更新漏洞学习统计失败: " + e.getMessage()));
    }
  }

  @PostMapping("/test")
  @Operation(summary = "更新测试统计", description = "更新用户测试统计信息")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> updateTestStats(
      @RequestParam Long userId,
      @RequestParam Long testId,
      @RequestParam Boolean passed,
      @RequestParam Integer score,
      @RequestParam Integer points) {
    log.info(
        "更新测试统计: userId={}, testId={}, passed={}, score={}, points={}",
        userId,
        testId,
        passed,
        score,
        points);

    userStatsUpdateService.updateTestStats(userId, testId, passed, score, points);

    return ResponseEntity.ok(ApiResult.success("测试统计更新成功"));
  }

  @PostMapping("/challenge")
  @Operation(summary = "更新挑战统计", description = "更新用户挑战统计信息")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> updateChallengeStats(
      @RequestParam Long userId,
      @RequestParam Long challengeId,
      @RequestParam Boolean completed,
      @RequestParam Integer points,
      @RequestParam(required = false) String badge) {
    log.info(
        "更新挑战统计: userId={}, challengeId={}, completed={}, points={}, badge={}",
        userId,
        challengeId,
        completed,
        points,
        badge);

    userStatsUpdateService.updateChallengeStats(userId, challengeId, completed, points, badge);

    return ResponseEntity.ok(ApiResult.success("挑战统计更新成功"));
  }

  @PostMapping("/study-time")
  @Operation(summary = "更新学习时长", description = "更新用户学习时长统计")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> updateStudyTimeStats(
      @RequestParam Long userId, @RequestParam Integer additionalTime) {
    log.info("更新学习时长: userId={}, additionalTime={}", userId, additionalTime);

    userStatsUpdateService.updateStudyTimeStats(userId, additionalTime);

    return ResponseEntity.ok(ApiResult.success("学习时长更新成功"));
  }

  @PostMapping("/streak")
  @Operation(summary = "更新连续学习天数", description = "更新用户连续学习天数")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> updateStreakStats(@RequestParam Long userId) {
    log.info("更新连续学习天数: userId={}", userId);

    userStatsUpdateService.updateStreakStats(userId);

    return ResponseEntity.ok(ApiResult.success("连续学习天数更新成功"));
  }
}
