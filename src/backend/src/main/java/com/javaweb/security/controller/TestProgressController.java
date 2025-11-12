package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.service.TestCompletionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 测试进度控制器
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/test-progress")
@RequiredArgsConstructor
@Tag(name = "测试进度", description = "测试进度跟踪相关接口")
public class TestProgressController {

  private final TestCompletionService testCompletionService;

  @GetMapping("/passed/{userId}/{vulnerabilityCode}")
  @Operation(summary = "检查测试通过状态", description = "检查用户是否通过指定漏洞的测试")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Boolean>> isTestPassed(
      @PathVariable Long userId, @PathVariable String vulnerabilityCode) {
    log.info("检查测试通过状态: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);

    boolean passed = testCompletionService.isVulnerabilityTestPassed(userId, vulnerabilityCode);

    return ResponseEntity.ok(ApiResult.success(passed));
  }

  @GetMapping("/criteria/{vulnerabilityCode}")
  @Operation(summary = "获取测试完成条件", description = "获取指定漏洞的测试完成条件")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<TestCompletionService.TestCompletionCriteria>> getTestCriteria(
      @PathVariable String vulnerabilityCode) {
    log.info("获取测试完成条件: vulnerabilityCode={}", vulnerabilityCode);

    TestCompletionService.TestCompletionCriteria criteria =
        testCompletionService.getTestCompletionCriteria(vulnerabilityCode);

    return ResponseEntity.ok(ApiResult.success(criteria));
  }

  @PostMapping("/record")
  @Operation(summary = "记录测试完成", description = "记录用户测试完成情况")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Void>> recordTestCompletion(
      @RequestParam Long userId,
      @RequestParam String vulnerabilityCode,
      @RequestParam Integer score,
      @RequestParam Double accuracy) {
    log.info(
        "记录测试完成: userId={}, vulnerabilityCode={}, score={}, accuracy={}",
        userId,
        vulnerabilityCode,
        score,
        accuracy);

    testCompletionService.recordTestCompletion(userId, vulnerabilityCode, score, accuracy);

    return ResponseEntity.ok(ApiResult.success("测试完成记录成功"));
  }
}
