package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.dto.attacklog.AttackLogCreateRequest;
import com.javaweb.security.dto.attacklog.AttackLogQueryCriteria;
import com.javaweb.security.dto.attacklog.AttackLogResponseDto;
import com.javaweb.security.dto.attacklog.AttackLogStatsDto;
import com.javaweb.security.service.AttackLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 注意：前端“日志管理/攻击日志”模块尚未完成联调，目前仅用于后续演示与扩展。 控制器开放的接口可直接调用，但默认界面不会展示该数据。 */
@Slf4j
@RestController
@RequestMapping("/api/v1/attack-logs")
@RequiredArgsConstructor
@Validated
@Tag(name = "攻击日志", description = "攻击日志查询与记录接口")
@SecurityRequirement(name = "bearerAuth")
public class AttackLogController {

  private final AttackLogService attackLogService;

  @GetMapping
  @Operation(summary = "分页查询攻击日志", description = "根据条件筛选攻击日志记录")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Page<AttackLogResponseDto>>> queryAttackLogs(
      AttackLogQueryCriteria criteria,
      @Parameter(description = "页码(从0开始)") @RequestParam(value = "page", defaultValue = "0") @Min(0)
          int page,
      @Parameter(description = "分页大小")
          @RequestParam(value = "size", defaultValue = "20")
          @Min(1)
          @Max(200)
          int size,
      @Parameter(description = "排序字段，格式为 field,dir，例如 createdAt,desc")
          @RequestParam(value = "sort", defaultValue = "createdAt,desc")
          String sort) {

    Pageable pageable = buildPageable(page, size, sort);
    Page<AttackLogResponseDto> result = attackLogService.queryAttackLogs(criteria, pageable);
    return ResponseEntity.ok(ApiResult.success(result));
  }

  @GetMapping("/{id}")
  @Operation(summary = "获取攻击日志详情", description = "根据ID获取单条攻击日志详情")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<AttackLogResponseDto>> getAttackLog(@PathVariable("id") Long id) {
    log.info("查询攻击日志详情：id={}", id);
    AttackLogResponseDto dto = attackLogService.getAttackLog(id);
    return ResponseEntity.ok(ApiResult.success(dto));
  }

  @PostMapping
  @Operation(summary = "记录攻击日志", description = "提供内部/测试使用的攻击日志记录接口")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<AttackLogResponseDto>> recordAttackLog(
      @Valid @RequestBody AttackLogCreateRequest request) {
    log.info(
        "记录攻击日志：userId={}, vulnerabilityId={}, attackType={}",
        request.getUserId(),
        request.getVulnerabilityId(),
        request.getAttackType());
    AttackLogResponseDto dto = attackLogService.recordAttack(request);
    return ResponseEntity.ok(ApiResult.success("记录成功", dto));
  }

  @GetMapping("/stats")
  @Operation(summary = "获取攻击日志统计", description = "返回全局及按模块的攻击日志统计数据")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<AttackLogStatsDto>> getAttackLogStats() {
    AttackLogStatsDto stats = attackLogService.getAttackLogStats();
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  private Pageable buildPageable(int page, int size, String sort) {
    String[] parts = sort.split(",");
    String sortField =
        parts.length > 0 && parts[0].trim().length() > 0 ? parts[0].trim() : "createdAt";
    Sort.Direction direction = Sort.Direction.DESC;
    if (parts.length > 1) {
      String dir = parts[1].trim().toLowerCase();
      if ("asc".equals(dir)) {
        direction = Sort.Direction.ASC;
      }
    }
    // 默认降序且防止字段穿透，限定可排序字段
    switch (sortField) {
      case "createdAt":
      case "responseStatus":
      case "executionTime":
        break;
      default:
        sortField = "createdAt";
    }
    return PageRequest.of(page, size, Sort.by(direction, sortField));
  }
}
