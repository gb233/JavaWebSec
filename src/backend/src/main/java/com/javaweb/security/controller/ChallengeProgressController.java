package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.service.ChallengeCompletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 挑战进度控制器
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/challenge-progress")
@RequiredArgsConstructor
@Tag(name = "挑战进度", description = "挑战进度跟踪相关接口")
public class ChallengeProgressController {

  private final ChallengeCompletionService challengeCompletionService;

  @GetMapping("/completed/{userId}/{vulnerabilityCode}")
  @Operation(summary = "检查挑战完成状态", description = "检查用户是否完成指定漏洞的挑战")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Boolean>> isChallengeCompleted(
      @PathVariable Long userId, @PathVariable String vulnerabilityCode) {
    log.info("检查挑战完成状态: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);

    boolean completed =
        challengeCompletionService.isVulnerabilityChallengeCompleted(userId, vulnerabilityCode);

    return ResponseEntity.ok(ApiResult.success(completed));
  }

  @GetMapping("/criteria/{vulnerabilityCode}")
  @Operation(summary = "获取挑战完成条件", description = "获取指定漏洞的挑战完成条件")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<ChallengeCompletionService.ChallengeCompletionCriteria>>
      getChallengeCriteria(@PathVariable String vulnerabilityCode) {
    log.info("获取挑战完成条件: vulnerabilityCode={}", vulnerabilityCode);

    ChallengeCompletionService.ChallengeCompletionCriteria criteria =
        challengeCompletionService.getChallengeCompletionCriteria(vulnerabilityCode);

    return ResponseEntity.ok(ApiResult.success(criteria));
  }

  @PostMapping("/record")
  @Operation(summary = "记录挑战完成", description = "记录用户挑战完成情况")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> recordChallengeCompletion(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam Integer score,
      @RequestParam(required = false) String badge) {
    log.info(
        "记录挑战完成: userId={}, vulnerabilityCode={}, score={}, badge={}",
        userId,
        vulnerabilityCode,
        score,
        badge);

    challengeCompletionService.recordChallengeCompletion(userId, vulnerabilityCode, score, badge);

    return ResponseEntity.ok(ApiResult.success("挑战完成记录成功"));
  }
}
