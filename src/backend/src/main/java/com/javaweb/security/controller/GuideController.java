package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.dto.GuideStepDto;
import com.javaweb.security.dto.UserGuidePreferenceDto;
import com.javaweb.security.service.GuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户指引控制器
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/guide")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户指引", description = "新手指引相关接口")
public class GuideController {

  private final GuideService guideService;

  @GetMapping("/preference")
  @Operation(summary = "获取用户指引偏好", description = "获取当前用户的指引设置")
  public ResponseEntity<ApiResult<UserGuidePreferenceDto>> getUserGuidePreference() {
    try {
      UserGuidePreferenceDto preference = guideService.getUserGuidePreference();
      return ResponseEntity.ok(ApiResult.success("获取指引偏好成功", preference));
    } catch (Exception e) {
      log.error("获取用户指引偏好失败", e);
      return ResponseEntity.ok(ApiResult.error("获取指引偏好失败: " + e.getMessage()));
    }
  }

  @GetMapping("/should-show")
  @Operation(summary = "检查是否需要显示指引", description = "检查当前用户是否需要显示新手指引")
  public ResponseEntity<ApiResult<Boolean>> shouldShowGuide() {
    try {
      boolean shouldShow = guideService.shouldShowGuide();
      return ResponseEntity.ok(ApiResult.success("检查指引显示状态成功", shouldShow));
    } catch (Exception e) {
      log.error("检查指引显示状态失败", e);
      return ResponseEntity.ok(ApiResult.error("检查指引显示状态失败: " + e.getMessage()));
    }
  }

  @GetMapping("/steps")
  @Operation(summary = "获取指引步骤", description = "获取新手指引的所有步骤")
  public ResponseEntity<ApiResult<List<GuideStepDto>>> getGuideSteps() {
    try {
      List<GuideStepDto> steps = guideService.getGuideSteps();
      return ResponseEntity.ok(ApiResult.success("获取指引步骤成功", steps));
    } catch (Exception e) {
      log.error("获取指引步骤失败", e);
      return ResponseEntity.ok(ApiResult.error("获取指引步骤失败: " + e.getMessage()));
    }
  }

  @PostMapping("/complete")
  @Operation(summary = "标记指引完成", description = "标记用户已完成初始指引")
  public ResponseEntity<ApiResult<Void>> markGuideCompleted() {
    try {
      guideService.markInitialGuideCompleted();
      return ResponseEntity.ok(ApiResult.success("标记指引完成成功", null));
    } catch (Exception e) {
      log.error("标记指引完成失败", e);
      return ResponseEntity.ok(ApiResult.error("标记指引完成失败: " + e.getMessage()));
    }
  }

  @PostMapping("/update-shown-time")
  @Operation(summary = "更新指引显示时间", description = "更新用户指引显示时间")
  public ResponseEntity<ApiResult<Void>> updateGuideShownTime() {
    try {
      guideService.updateGuideShownTime();
      return ResponseEntity.ok(ApiResult.success("更新指引显示时间成功", null));
    } catch (Exception e) {
      log.error("更新指引显示时间失败", e);
      return ResponseEntity.ok(ApiResult.error("更新指引显示时间失败: " + e.getMessage()));
    }
  }

  @PostMapping("/auto-show")
  @Operation(summary = "设置自动显示指引", description = "设置是否自动显示指引")
  public ResponseEntity<ApiResult<Void>> setAutoShowGuide(@RequestParam boolean autoShow) {
    try {
      guideService.setAutoShowGuide(autoShow);
      return ResponseEntity.ok(ApiResult.success("设置自动显示指引成功", null));
    } catch (Exception e) {
      log.error("设置自动显示指引失败", e);
      return ResponseEntity.ok(ApiResult.error("设置自动显示指引失败: " + e.getMessage()));
    }
  }

  @PostMapping("/reset")
  @Operation(summary = "重置用户指引", description = "重置用户的指引状态，允许重新显示指引")
  public ResponseEntity<ApiResult<Void>> resetUserGuide() {
    try {
      guideService.resetUserGuide();
      return ResponseEntity.ok(ApiResult.success("重置用户指引成功", null));
    } catch (Exception e) {
      log.error("重置用户指引失败", e);
      return ResponseEntity.ok(ApiResult.error("重置用户指引失败: " + e.getMessage()));
    }
  }
}
