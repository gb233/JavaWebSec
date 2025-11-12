package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.service.LearningCompletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 学习进度控制器
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/learning-progress")
@RequiredArgsConstructor
@Tag(name = "学习进度", description = "学习进度跟踪相关接口")
public class LearningProgressController {

  private final LearningCompletionService learningCompletionService;

  @GetMapping("/completed/{userId}/{vulnerabilityCode}")
  @Operation(summary = "检查学习完成状态", description = "检查用户是否完成指定漏洞的学习")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Boolean>> isLearningCompleted(
      @PathVariable Long userId, @PathVariable String vulnerabilityCode) {
    log.info("检查学习完成状态: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);

    boolean completed =
        learningCompletionService.isVulnerabilityLearningCompleted(userId, vulnerabilityCode);

    return ResponseEntity.ok(ApiResult.success(completed));
  }

  @PostMapping("/page-visit")
  @Operation(summary = "记录页面访问", description = "记录用户学习页面访问")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> recordPageVisit(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam String pageType,
      @RequestParam Integer duration) {
    log.info(
        "记录页面访问: userId={}, vulnerabilityCode={}, pageType={}, duration={}",
        userId,
        vulnerabilityCode,
        pageType,
        duration);

    learningCompletionService.recordPageVisit(userId, vulnerabilityCode, pageType, duration);

    return ResponseEntity.ok(ApiResult.success("页面访问记录成功"));
  }

  @PostMapping("/interaction")
  @Operation(summary = "记录用户交互", description = "记录用户学习过程中的交互行为")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> recordUserInteraction(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam String interactionType,
      @RequestBody Map<String, Object> interactionData) {
    log.info(
        "记录用户交互: userId={}, vulnerabilityCode={}, interactionType={}",
        userId,
        vulnerabilityCode,
        interactionType);

    learningCompletionService.recordUserInteraction(
        userId, vulnerabilityCode, interactionType, interactionData);

    return ResponseEntity.ok(ApiResult.success("用户交互记录成功"));
  }
}
