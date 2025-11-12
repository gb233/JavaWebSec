package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.dto.dashboard.DashboardOverviewDto;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘", description = "仪表盘概览接口")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

  private final DashboardService dashboardService;
  private final AuthenticationService authenticationService;

  @GetMapping("/overview")
  @Operation(summary = "获取仪表盘概览", description = "返回当前用户的仪表盘概览数据")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<DashboardOverviewDto>> getOverview() {
    Long userId = authenticationService.getCurrentUserId();
    if (userId == null) {
      log.warn("无法获取当前用户ID，返回未授权错误");
      return ResponseEntity.status(401).body(ApiResult.error(401, "未授权：无法获取用户信息"));
    }
    log.debug("获取仪表盘概览数据，userId={}", userId);
    DashboardOverviewDto overview = dashboardService.getOverview(userId);
    return ResponseEntity.ok(ApiResult.success(overview));
  }
}
