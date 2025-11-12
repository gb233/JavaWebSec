package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.NoteTag;
import com.javaweb.security.service.NoteTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** 笔记标签控制器 */
@RestController
@RequestMapping("/api/v1/note-tags")
@Tag(name = "笔记标签", description = "笔记标签管理相关接口")
public class NoteTagController {

  @Autowired private NoteTagService tagService;

  @PostMapping
  @Operation(summary = "创建标签", description = "创建新的笔记标签")
  public ResponseEntity<ApiResult<NoteTag>> createTag(@RequestBody NoteTag tag) {
    NoteTag createdTag = tagService.createTag(tag);
    return ResponseEntity.ok(ApiResult.success(createdTag));
  }

  @PutMapping("/{tagId}")
  @Operation(summary = "更新标签", description = "更新指定的笔记标签")
  public ResponseEntity<ApiResult<NoteTag>> updateTag(
      @PathVariable Long tagId, @RequestBody NoteTag tag) {
    NoteTag updatedTag = tagService.updateTag(tagId, tag);
    return ResponseEntity.ok(ApiResult.success(updatedTag));
  }

  @DeleteMapping("/{tagId}")
  @Operation(summary = "删除标签", description = "删除指定的笔记标签")
  public ResponseEntity<ApiResult<Void>> deleteTag(@PathVariable Long tagId) {
    tagService.deleteTag(tagId);
    return ResponseEntity.ok(ApiResult.success());
  }

  @GetMapping("/{tagId}")
  @Operation(summary = "获取标签详情", description = "获取指定标签的详细信息")
  public ResponseEntity<ApiResult<NoteTag>> getTagById(@PathVariable Long tagId) {
    NoteTag tag = tagService.getTagById(tagId);
    return ResponseEntity.ok(ApiResult.success(tag));
  }

  @GetMapping("/name/{tagName}")
  @Operation(summary = "根据名称获取标签", description = "根据标签名称获取标签信息")
  public ResponseEntity<ApiResult<NoteTag>> getTagByName(@PathVariable String tagName) {
    NoteTag tag = tagService.getTagByName(tagName);
    return ResponseEntity.ok(ApiResult.success(tag));
  }

  @GetMapping
  @Operation(summary = "获取所有标签", description = "获取所有笔记标签")
  public ResponseEntity<ApiResult<List<NoteTag>>> getAllTags() {
    List<NoteTag> tags = tagService.getAllTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @GetMapping("/system")
  @Operation(summary = "获取系统标签", description = "获取系统预定义的标签")
  public ResponseEntity<ApiResult<List<NoteTag>>> getSystemTags() {
    List<NoteTag> tags = tagService.getSystemTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @GetMapping("/user")
  @Operation(summary = "获取用户标签", description = "获取用户创建的标签")
  public ResponseEntity<ApiResult<List<NoteTag>>> getUserTags() {
    List<NoteTag> tags = tagService.getUserTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @GetMapping("/popular")
  @Operation(summary = "获取热门标签", description = "获取使用次数最多的标签")
  public ResponseEntity<ApiResult<List<NoteTag>>> getPopularTags() {
    List<NoteTag> tags = tagService.getPopularTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @GetMapping("/search")
  @Operation(summary = "搜索标签", description = "根据关键词搜索标签")
  public ResponseEntity<ApiResult<List<NoteTag>>> searchTags(
      @Parameter(description = "搜索关键词") @RequestParam String keyword) {
    List<NoteTag> tags = tagService.searchTags(keyword);
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @GetMapping("/color/{color}")
  @Operation(summary = "根据颜色获取标签", description = "获取指定颜色的标签")
  public ResponseEntity<ApiResult<List<NoteTag>>> getTagsByColor(@PathVariable String color) {
    List<NoteTag> tags = tagService.getTagsByColor(color);
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @GetMapping("/most-used")
  @Operation(summary = "获取最常用标签", description = "获取使用次数最多的标签")
  public ResponseEntity<ApiResult<List<NoteTag>>> getMostUsedTags() {
    List<NoteTag> tags = tagService.getMostUsedTags();
    return ResponseEntity.ok(ApiResult.success(tags));
  }

  @GetMapping("/count")
  @Operation(summary = "获取标签总数", description = "获取标签的总数量")
  public ResponseEntity<ApiResult<Long>> getTagCount() {
    long count = tagService.getTagCount();
    return ResponseEntity.ok(ApiResult.success(count));
  }

  @PostMapping("/{tagName}/increment")
  @Operation(summary = "增加标签使用次数", description = "增加指定标签的使用次数")
  public ResponseEntity<ApiResult<Void>> incrementTagUsage(@PathVariable String tagName) {
    tagService.incrementTagUsage(tagName);
    return ResponseEntity.ok(ApiResult.success());
  }

  @PostMapping("/{tagName}/decrement")
  @Operation(summary = "减少标签使用次数", description = "减少指定标签的使用次数")
  public ResponseEntity<ApiResult<Void>> decrementTagUsage(@PathVariable String tagName) {
    tagService.decrementTagUsage(tagName);
    return ResponseEntity.ok(ApiResult.success());
  }

  @DeleteMapping("/unused")
  @Operation(summary = "删除未使用标签", description = "删除使用次数为0的用户标签")
  public ResponseEntity<ApiResult<Void>> deleteUnusedTags() {
    tagService.deleteUnusedTags();
    return ResponseEntity.ok(ApiResult.success());
  }

  @DeleteMapping("/name/{tagName}")
  @Operation(summary = "根据名称删除标签", description = "根据标签名称删除标签")
  public ResponseEntity<ApiResult<Void>> deleteTagByName(@PathVariable String tagName) {
    tagService.deleteTagByName(tagName);
    return ResponseEntity.ok(ApiResult.success());
  }
}
