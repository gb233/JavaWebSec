package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.CollectionTag;
import com.javaweb.security.service.CollectionTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 收藏标签控制器 */
@RestController
@RequestMapping("/api/v1/collection-tags")
@Tag(name = "收藏标签管理", description = "收藏标签的增删改查及统计接口")
public class CollectionTagController {

  @Autowired private CollectionTagService collectionTagService;

  @Operation(summary = "创建标签", description = "创建新的收藏标签")
  @PostMapping
  public ResponseEntity<ApiResult<CollectionTag>> createTag(@RequestBody CollectionTag tag) {
    CollectionTag createdTag = collectionTagService.createTag(tag);
    return ResponseEntity.ok(ApiResult.success(createdTag));
  }

  @Operation(summary = "更新标签", description = "根据标签ID更新现有标签")
  @PutMapping("/{tagId}")
  public ResponseEntity<ApiResult<CollectionTag>> updateTag(
      @PathVariable Long tagId, @RequestBody CollectionTag tag) {
    CollectionTag updatedTag = collectionTagService.updateTag(tagId, tag);
    return ResponseEntity.ok(ApiResult.success(updatedTag));
  }

  @Operation(summary = "删除标签", description = "根据标签ID删除标签")
  @DeleteMapping("/{tagId}")
  public ResponseEntity<ApiResult<Void>> deleteTag(@PathVariable Long tagId) {
    collectionTagService.deleteTag(tagId);
    return ResponseEntity.ok(ApiResult.success(null));
  }

  @Operation(summary = "获取单个标签", description = "根据标签ID获取标签详情")
  @GetMapping("/{tagId}")
  public ResponseEntity<ApiResult<CollectionTag>> getTagById(@PathVariable Long tagId) {
    return collectionTagService
        .getTagById(tagId)
        .map(tag -> ResponseEntity.ok(ApiResult.success(tag)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "根据名称获取标签", description = "根据标签名称获取标签")
  @GetMapping("/name/{name}")
  public ResponseEntity<ApiResult<CollectionTag>> getTagByName(@PathVariable String name) {
    return collectionTagService
        .getTagByName(name)
        .map(tag -> ResponseEntity.ok(ApiResult.success(tag)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "获取所有标签", description = "获取所有收藏标签")
  @GetMapping
  public ResponseEntity<ApiResult<List<CollectionTag>>> getAllTags() {
    List<CollectionTag> tags = collectionTagService.getAllTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @Operation(summary = "搜索标签", description = "根据关键词搜索标签")
  @GetMapping("/search")
  public ResponseEntity<ApiResult<List<CollectionTag>>> searchTags(
      @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
    List<CollectionTag> tags = collectionTagService.searchTags(keyword);
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @Operation(summary = "获取热门标签", description = "获取使用次数最多的标签")
  @GetMapping("/popular")
  public ResponseEntity<ApiResult<List<CollectionTag>>> getPopularTags() {
    List<CollectionTag> tags = collectionTagService.getPopularTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @Operation(summary = "获取未使用的标签", description = "获取使用次数为0的标签")
  @GetMapping("/unused")
  public ResponseEntity<ApiResult<List<CollectionTag>>> getUnusedTags() {
    List<CollectionTag> tags = collectionTagService.getUnusedTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @Operation(summary = "增加标签使用次数", description = "增加指定标签的使用次数")
  @PutMapping("/{tagId}/increment")
  public ResponseEntity<ApiResult<Void>> incrementTagUsage(@PathVariable Long tagId) {
    collectionTagService.incrementTagUsage(tagId);
    return ResponseEntity.ok(ApiResult.success(null));
  }

  @Operation(summary = "减少标签使用次数", description = "减少指定标签的使用次数")
  @PutMapping("/{tagId}/decrement")
  public ResponseEntity<ApiResult<Void>> decrementTagUsage(@PathVariable Long tagId) {
    collectionTagService.decrementTagUsage(tagId);
    return ResponseEntity.ok(ApiResult.success(null));
  }

  @Operation(summary = "获取标签统计", description = "获取标签统计信息")
  @GetMapping("/stats")
  public ResponseEntity<ApiResult<Map<String, Long>>> getTagStats() {
    Map<String, Long> stats = collectionTagService.getTagStats();
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  @Operation(summary = "批量创建标签", description = "批量创建收藏标签")
  @PostMapping("/batch")
  public ResponseEntity<ApiResult<List<CollectionTag>>> batchCreateTags(
      @RequestBody List<CollectionTag> tags) {
    List<CollectionTag> createdTags = collectionTagService.batchCreateTags(tags);
    return ResponseEntity.ok(ApiResult.success(createdTags));
  }

  @Operation(summary = "清理未使用的标签", description = "删除所有未使用的标签")
  @DeleteMapping("/cleanup")
  public ResponseEntity<ApiResult<Void>> cleanupUnusedTags() {
    collectionTagService.cleanupUnusedTags();
    return ResponseEntity.ok(ApiResult.success(null));
  }
}
