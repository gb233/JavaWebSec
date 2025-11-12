package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.NoteComment;
import com.javaweb.security.service.NoteCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 笔记评论控制器 */
@RestController
@RequestMapping("/api/v1/note-comments")
@Tag(name = "笔记评论", description = "笔记评论管理相关接口")
public class NoteCommentController {

  @Autowired private NoteCommentService commentService;

  @PostMapping
  @Operation(summary = "创建评论", description = "创建新的笔记评论")
  public ResponseEntity<ApiResult<NoteComment>> createComment(
      @RequestBody NoteComment comment, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    comment.setUserId(userId);
    NoteComment createdComment = commentService.createComment(comment);
    return ResponseEntity.ok(ApiResult.success(createdComment));
  }

  @PutMapping("/{commentId}")
  @Operation(summary = "更新评论", description = "更新指定的笔记评论")
  public ResponseEntity<ApiResult<NoteComment>> updateComment(
      @PathVariable Long commentId,
      @RequestBody NoteComment comment,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只有评论作者可以修改
    NoteComment existingComment = commentService.getCommentById(commentId);
    if (!existingComment.getUserId().equals(userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限修改此评论"));
    }
    NoteComment updatedComment = commentService.updateComment(commentId, comment);
    return ResponseEntity.ok(ApiResult.success(updatedComment));
  }

  @DeleteMapping("/{commentId}")
  @Operation(summary = "删除评论", description = "删除指定的笔记评论")
  public ResponseEntity<ApiResult<Void>> deleteComment(
      @PathVariable Long commentId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只有评论作者可以删除
    NoteComment existingComment = commentService.getCommentById(commentId);
    if (!existingComment.getUserId().equals(userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限删除此评论"));
    }
    commentService.deleteComment(commentId);
    return ResponseEntity.ok(ApiResult.success());
  }

  @PostMapping("/{commentId}/soft-delete")
  @Operation(summary = "软删除评论", description = "软删除指定的笔记评论")
  public ResponseEntity<ApiResult<Void>> softDeleteComment(
      @PathVariable Long commentId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只有评论作者可以软删除
    NoteComment existingComment = commentService.getCommentById(commentId);
    if (!existingComment.getUserId().equals(userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此评论"));
    }
    commentService.softDeleteComment(commentId);
    return ResponseEntity.ok(ApiResult.success());
  }

  @PostMapping("/{commentId}/restore")
  @Operation(summary = "恢复评论", description = "恢复软删除的笔记评论")
  public ResponseEntity<ApiResult<Void>> restoreComment(
      @PathVariable Long commentId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只有评论作者可以恢复
    NoteComment existingComment = commentService.getCommentById(commentId);
    if (!existingComment.getUserId().equals(userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此评论"));
    }
    commentService.restoreComment(commentId);
    return ResponseEntity.ok(ApiResult.success());
  }

  @GetMapping("/{commentId}")
  @Operation(summary = "获取评论详情", description = "获取指定评论的详细信息")
  public ResponseEntity<ApiResult<NoteComment>> getCommentById(@PathVariable Long commentId) {
    NoteComment comment = commentService.getCommentById(commentId);
    return ResponseEntity.ok(ApiResult.success(comment));
  }

  @GetMapping("/note/{noteId}")
  @Operation(summary = "获取笔记评论", description = "获取指定笔记的所有评论")
  public ResponseEntity<ApiResult<List<NoteComment>>> getNoteComments(@PathVariable Long noteId) {
    List<NoteComment> comments = commentService.getNoteComments(noteId);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/note/{noteId}/page")
  @Operation(summary = "分页获取笔记评论", description = "分页获取指定笔记的评论")
  public ResponseEntity<ApiResult<Page<NoteComment>>> getNoteCommentsPage(
      @PathVariable Long noteId, @PageableDefault(size = 20) Pageable pageable) {
    Page<NoteComment> comments = commentService.getNoteComments(noteId, pageable);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/note/{noteId}/top")
  @Operation(summary = "获取笔记顶级评论", description = "获取指定笔记的顶级评论（无父评论）")
  public ResponseEntity<ApiResult<List<NoteComment>>> getNoteTopComments(
      @PathVariable Long noteId) {
    List<NoteComment> comments = commentService.getNoteTopComments(noteId);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/note/{noteId}/top/page")
  @Operation(summary = "分页获取笔记顶级评论", description = "分页获取指定笔记的顶级评论")
  public ResponseEntity<ApiResult<Page<NoteComment>>> getNoteTopCommentsPage(
      @PathVariable Long noteId, @PageableDefault(size = 20) Pageable pageable) {
    Page<NoteComment> comments = commentService.getNoteTopComments(noteId, pageable);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/parent/{parentId}")
  @Operation(summary = "获取子评论", description = "获取指定父评论的所有子评论")
  public ResponseEntity<ApiResult<List<NoteComment>>> getChildComments(
      @PathVariable Long parentId) {
    List<NoteComment> comments = commentService.getChildComments(parentId);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/my")
  @Operation(summary = "获取我的评论", description = "获取当前用户的所有评论")
  public ResponseEntity<ApiResult<List<NoteComment>>> getMyComments(
      Authentication authentication, @PageableDefault(size = 20) Pageable pageable) {
    Long userId = getCurrentUserId(authentication);
    Page<NoteComment> comments = commentService.getUserComments(userId, pageable);
    return ResponseEntity.ok(ApiResult.success(comments.getContent()));
  }

  @GetMapping("/note/{noteId}/active")
  @Operation(summary = "获取笔记活跃评论", description = "获取指定笔记的未删除评论")
  public ResponseEntity<ApiResult<List<NoteComment>>> getActiveNoteComments(
      @PathVariable Long noteId) {
    List<NoteComment> comments = commentService.getActiveNoteComments(noteId);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/note/{noteId}/active/top")
  @Operation(summary = "获取笔记活跃顶级评论", description = "获取指定笔记的未删除顶级评论")
  public ResponseEntity<ApiResult<List<NoteComment>>> getActiveNoteTopComments(
      @PathVariable Long noteId) {
    List<NoteComment> comments = commentService.getActiveNoteTopComments(noteId);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/note/{noteId}/count")
  @Operation(summary = "获取笔记评论数", description = "获取指定笔记的评论总数")
  public ResponseEntity<ApiResult<Long>> countNoteComments(@PathVariable Long noteId) {
    long count = commentService.countNoteComments(noteId);
    return ResponseEntity.ok(ApiResult.success(count));
  }

  @GetMapping("/note/{noteId}/active/count")
  @Operation(summary = "获取笔记活跃评论数", description = "获取指定笔记的活跃评论数")
  public ResponseEntity<ApiResult<Long>> countActiveNoteComments(@PathVariable Long noteId) {
    long count = commentService.countActiveNoteComments(noteId);
    return ResponseEntity.ok(ApiResult.success(count));
  }

  @GetMapping("/my/count")
  @Operation(summary = "获取我的评论数", description = "获取当前用户的评论总数")
  public ResponseEntity<ApiResult<Long>> countMyComments(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    long count = commentService.countUserComments(userId);
    return ResponseEntity.ok(ApiResult.success(count));
  }

  @GetMapping("/my/active/count")
  @Operation(summary = "获取我的活跃评论数", description = "获取当前用户的活跃评论数")
  public ResponseEntity<ApiResult<Long>> countMyActiveComments(Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    long count = commentService.countActiveUserComments(userId);
    return ResponseEntity.ok(ApiResult.success(count));
  }

  @GetMapping("/recent")
  @Operation(summary = "获取最近评论", description = "获取最近的评论列表")
  public ResponseEntity<ApiResult<List<NoteComment>>> getRecentComments(
      @PageableDefault(size = 10) Pageable pageable) {
    List<NoteComment> comments = commentService.getRecentComments(pageable);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @GetMapping("/my/recent")
  @Operation(summary = "获取我的最近评论", description = "获取当前用户最近的评论")
  public ResponseEntity<ApiResult<List<NoteComment>>> getMyRecentComments(
      Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {
    Long userId = getCurrentUserId(authentication);
    List<NoteComment> comments = commentService.getUserRecentComments(userId, pageable);
    return ResponseEntity.ok(ApiResult.success(comments));
  }

  @PostMapping("/{commentId}/like")
  @Operation(summary = "点赞评论", description = "对指定评论进行点赞")
  public ResponseEntity<ApiResult<Void>> likeComment(@PathVariable Long commentId) {
    commentService.incrementCommentLikes(commentId);
    return ResponseEntity.ok(ApiResult.success());
  }

  @PostMapping("/{commentId}/unlike")
  @Operation(summary = "取消点赞评论", description = "取消对指定评论的点赞")
  public ResponseEntity<ApiResult<Void>> unlikeComment(@PathVariable Long commentId) {
    commentService.decrementCommentLikes(commentId);
    return ResponseEntity.ok(ApiResult.success());
  }

  /** 获取当前用户ID */
  private Long getCurrentUserId(Authentication authentication) {
    // 这里需要根据实际的认证实现来获取用户ID
    // 假设从认证对象中获取用户ID
    return Long.valueOf(authentication.getName());
  }
}
