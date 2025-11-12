package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.ChallengeProgress;
import com.javaweb.security.entity.ChallengeScenario;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 挑战场景控制器
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@RestController
@RequestMapping("/api/v1/challenge-scenarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "挑战场景管理", description = "综合挑战场景相关API")
public class ChallengeController {

  private final ChallengeService challengeService;
  private final AuthenticationService authenticationService;

  @GetMapping
  @Operation(summary = "获取挑战场景列表", description = "获取所有可用的挑战场景")
  public ResponseEntity<ApiResult<List<ChallengeScenario>>> getScenarios(
      @RequestParam(required = false) String difficultyLevel) {
    try {
      List<ChallengeScenario> scenarios = challengeService.getScenarios(difficultyLevel);
      return ResponseEntity.ok(ApiResult.success("获取挑战场景列表成功", scenarios));
    } catch (Exception e) {
      log.error("获取挑战场景列表失败", e);
      return ResponseEntity.ok(ApiResult.error("获取挑战场景列表失败: " + e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  @Operation(summary = "获取挑战场景详情", description = "获取指定挑战场景的详细信息")
  public ResponseEntity<ApiResult<ChallengeScenario>> getScenario(@PathVariable Long id) {
    try {
      ChallengeScenario scenario = challengeService.getScenario(id);
      return ResponseEntity.ok(ApiResult.success("获取挑战场景详情成功", scenario));
    } catch (Exception e) {
      log.error("获取挑战场景详情失败", e);
      return ResponseEntity.ok(ApiResult.error("获取挑战场景详情失败: " + e.getMessage()));
    }
  }

  @PostMapping("/{id}/start")
  @Operation(summary = "开始挑战", description = "开始指定的挑战场景")
  public ResponseEntity<ApiResult<ChallengeProgress>> startChallenge(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    try {
      Long userId = getCurrentUserId(token);
      ChallengeProgress progress = challengeService.startChallenge(userId, id);
      return ResponseEntity.ok(ApiResult.success("开始挑战成功", progress));
    } catch (Exception e) {
      log.error("开始挑战失败", e);
      return ResponseEntity.ok(ApiResult.error("开始挑战失败: " + e.getMessage()));
    }
  }

  @PostMapping("/{id}/execute")
  @Operation(summary = "执行挑战步骤", description = "执行挑战场景的指定步骤")
  public ResponseEntity<ApiResult<ChallengeService.ChallengeResult>> executeStep(
      @PathVariable Long id,
      @RequestBody StepRequest request,
      @RequestHeader("Authorization") String token) {
    try {
      Long userId = getCurrentUserId(token);
      ChallengeService.ChallengeResult result =
          challengeService.executeStep(userId, id, request.getStep(), request.getParams());
      return ResponseEntity.ok(ApiResult.success("执行步骤成功", result));
    } catch (Exception e) {
      log.error("执行挑战步骤失败", e);
      return ResponseEntity.ok(ApiResult.error("执行步骤失败: " + e.getMessage()));
    }
  }

  @GetMapping("/{id}/progress")
  @Operation(summary = "获取挑战进度", description = "获取用户挑战进度信息")
  public ResponseEntity<ApiResult<ChallengeProgress>> getProgress(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    try {
      Long userId = getCurrentUserId(token);
      ChallengeProgress progress = challengeService.getProgress(userId, id);
      return ResponseEntity.ok(ApiResult.success("获取挑战进度成功", progress));
    } catch (Exception e) {
      log.error("获取挑战进度失败", e);
      return ResponseEntity.ok(ApiResult.error("获取挑战进度失败: " + e.getMessage()));
    }
  }

  @PostMapping("/{id}/reset")
  @Operation(summary = "重置挑战", description = "重置挑战进度，重新开始挑战")
  public ResponseEntity<ApiResult<ChallengeProgress>> resetChallenge(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    try {
      Long userId = getCurrentUserId(token);
      ChallengeProgress progress = challengeService.resetChallenge(userId, id);
      return ResponseEntity.ok(ApiResult.success("重置挑战成功", progress));
    } catch (Exception e) {
      log.error("重置挑战失败", e);
      return ResponseEntity.ok(ApiResult.error("重置挑战失败: " + e.getMessage()));
    }
  }

  /** 获取当前用户ID */
  private Long getCurrentUserId(String token) {
    Long userId = authenticationService.getCurrentUserId();
    if (userId != null) {
      log.info("从AuthenticationService获取的用户ID: {}", userId);
      return userId;
    }

    log.warn("无法获取当前用户ID，使用默认用户ID: 1");
    return 1L;
  }

  /** 步骤请求类 */
  public static class StepRequest {
    private String step;
    private Map<String, Object> params;

    public String getStep() {
      return step;
    }

    public void setStep(String step) {
      this.step = step;
    }

    public Map<String, Object> getParams() {
      return params;
    }

    public void setParams(Map<String, Object> params) {
      this.params = params;
    }
  }
}
