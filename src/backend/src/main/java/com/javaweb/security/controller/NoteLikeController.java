package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.NoteLike;
import com.javaweb.security.service.NoteLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 笔记点赞控制器 */
@RestController
@RequestMapping("/api/v1/note-likes")
@Tag(name = "笔记点赞", description = "笔记点赞管理相关接口")
public class NoteLikeController {

  @Autowired private NoteLikeService likeService;

  @PostMapping("/{noteId}")
  @Operation(summary = "点赞笔记", description = "对指定笔记进行点赞")
  public ResponseEntity<ApiResult<NoteLike>> likeNote(
      @PathVariable Long noteId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    NoteLike like = likeService.likeNote(userId, noteId);
    return ResponseEntity.ok(ApiResult.success(like));
  }

  @DeleteMapping("/{noteId}")
  @Operation(summary = "取消点赞", description = "取消对指定笔记的点赞")
  public ResponseEntity<ApiResult<Void>> unlikeNote(
      @PathVariable Long noteId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    likeService.unlikeNote(userId, noteId);
    return ResponseEntity.ok(ApiResult.success());
  }

  @GetMapping("/{noteId}/status")
  @Operation(summary = "检查点赞状态", description = "检查当前用户是否已点赞指定笔记")
  public ResponseEntity<ApiResult<Boolean>> isLiked(
      @PathVariable Long noteId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    boolean isLiked = likeService.isLiked(userId, noteId);
    return ResponseEntity.ok(ApiResult.success(isLiked));
  }

  @GetMapping("/{noteId}/list")
  @Operation(summary = "获取笔记点赞列表", description = "获取指定笔记的所有点赞记录")
  public ResponseEntity<ApiResult<List<NoteLike>>> getNoteLikes(@PathVariable Long noteId) {
    List<NoteLike> likes = likeService.getNoteLikes(noteId);
    return ResponseEntity.ok(ApiResult.success(likes));
  }

  @GetMapping("/my")
  @Operation(summary = "获取我的点赞", description = "获取当前用户的所有点赞记录")
  public ResponseEntity<ApiResult<List<NoteLike>>> getMyLikes(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<NoteLike> likes = likeService.getUserLikes(userId);
    return ResponseEntity.ok(ApiResult.success(likes));
  }

  @GetMapping("/{noteId}/count")
  @Operation(summary = "获取笔记点赞数", description = "获取指定笔记的点赞数量")
  public ResponseEntity<ApiResult<Long>> countNoteLikes(@PathVariable Long noteId) {
    long count = likeService.countNoteLikes(noteId);
    return ResponseEntity.ok(ApiResult.success(count));
  }

  @GetMapping("/my/count")
  @Operation(summary = "获取我的点赞数", description = "获取当前用户的点赞总数")
  public ResponseEntity<ApiResult<Long>> countMyLikes(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    long count = likeService.countUserLikes(userId);
    return ResponseEntity.ok(ApiResult.success(count));
  }

  @GetMapping("/recent")
  @Operation(summary = "获取最近点赞的笔记", description = "获取当前用户最近点赞的笔记ID列表")
  public ResponseEntity<ApiResult<List<Long>>> getRecentLikedNotes(
      @RequestParam(defaultValue = "10") int limit, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<Long> noteIds = likeService.getRecentLikedNotes(userId, limit);
    return ResponseEntity.ok(ApiResult.success(noteIds));
  }

  @GetMapping("/popular")
  @Operation(summary = "获取热门点赞笔记", description = "获取最近热门点赞的笔记")
  public ResponseEntity<ApiResult<List<Object[]>>> getPopularLikedNotes(
      @RequestParam(defaultValue = "10") int limit) {
    List<Object[]> popularNotes = likeService.getPopularLikedNotes(limit);
    return ResponseEntity.ok(ApiResult.success(popularNotes));
  }

  /** 获取当前用户ID */
  private Long getCurrentUserId(Authentication authentication) {
    // 这里需要根据实际的认证实现来获取用户ID
    // 假设从认证对象中获取用户ID
    return Long.valueOf(authentication.getName());
  }
}
