package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.common.result.ResultCode;
import com.javaweb.security.dto.knowledge.VulnerabilityCategoryDto;
import com.javaweb.security.dto.knowledge.VulnerabilityDetailDto;
import com.javaweb.security.dto.knowledge.VulnerabilitySummaryDto;
import com.javaweb.security.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Locale;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
@Validated
@Tag(name = "漏洞知识中心", description = "漏洞知识内容与学习模块接口")
@SecurityRequirement(name = "bearerAuth")
public class KnowledgeController {

  private final KnowledgeService knowledgeService;

  @GetMapping("/categories")
  @Operation(summary = "获取漏洞分类列表", description = "返回已启用的漏洞分类及基础信息")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<List<VulnerabilityCategoryDto>>> listCategories() {
    List<VulnerabilityCategoryDto> categories = knowledgeService.listCategories();
    return ResponseEntity.ok(ApiResult.success(categories));
  }

  @GetMapping("/vulnerabilities")
  @Operation(summary = "分页查询漏洞内容", description = "支持按分类、关键词过滤，并按权重、热度或时间排序")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<Page<VulnerabilitySummaryDto>>> listVulnerabilities(
      @Parameter(description = "分类代码，如 A01") @RequestParam(value = "category", required = false)
          String category,
      @Parameter(description = "搜索关键词，匹配标题与描述") @RequestParam(value = "keyword", required = false)
          String keyword,
      @Parameter(description = "页码(从0开始)") @RequestParam(value = "page", defaultValue = "0") @Min(0)
          int page,
      @Parameter(description = "分页大小")
          @RequestParam(value = "size", defaultValue = "12")
          @Min(1)
          @Max(100)
          int size,
      @Parameter(description = "排序字段，支持 orderNum,viewCount,createdAt,estimatedTime")
          @RequestParam(value = "sort", defaultValue = "orderNum,asc")
          String sort) {

    try {
      Pageable pageable = buildPageable(page, size, sort);
      Page<VulnerabilitySummaryDto> result =
          knowledgeService.listVulnerabilities(category, keyword, pageable);
      return ResponseEntity.ok(ApiResult.success(result));
    } catch (Exception ex) {
      log.error(
          "查询漏洞列表失败: category={}, keyword={}, error={}", category, keyword, ex.getMessage(), ex);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.error("查询漏洞列表失败"));
    }
  }

  @GetMapping("/vulnerabilities/{id}")
  @Operation(summary = "获取漏洞详情", description = "根据ID返回完整的漏洞知识内容")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResult<VulnerabilityDetailDto>> getVulnerability(
      @Parameter(description = "漏洞内容ID", required = true) @PathVariable("id") Long id) {

    try {
      VulnerabilityDetailDto detail = knowledgeService.getVulnerability(id);
      return ResponseEntity.ok(ApiResult.success(detail));
    } catch (EntityNotFoundException ex) {
      log.warn("获取漏洞详情失败，记录不存在: id={}", id);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResult.error(ResultCode.VULNERABILITY_NOT_FOUND, ex.getMessage()));
    } catch (Exception ex) {
      log.error("获取漏洞详情异常: id={}, error={}", id, ex.getMessage(), ex);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResult.error("获取漏洞详情失败"));
    }
  }

  private Pageable buildPageable(int page, int size, String sort) {
    String[] parts = sort.split(",");
    String sortField = parts.length > 0 ? parts[0].trim() : "orderNum";
    String sortDir = parts.length > 1 ? parts[1].trim().toLowerCase(Locale.ROOT) : "asc";

    if (!isAllowedSortField(sortField)) {
      sortField = "orderNum";
    }

    Sort.Direction direction = "asc".equals(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return PageRequest.of(page, size, Sort.by(direction, sortField));
  }

  private boolean isAllowedSortField(String field) {
    if (field == null || field.isEmpty()) {
      return false;
    }
    switch (field) {
      case "orderNum":
      case "viewCount":
      case "createdAt":
      case "estimatedTime":
        return true;
      default:
        return false;
    }
  }
}
