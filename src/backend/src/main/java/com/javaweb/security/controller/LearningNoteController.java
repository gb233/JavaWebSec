package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.LearningNote;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.LearningNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/** 学习笔记控制器 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "学习笔记", description = "学习笔记管理相关接口")
public class LearningNoteController {

  @Autowired private LearningNoteService noteService;
  @Autowired private AuthenticationService authenticationService;

  @PostMapping
  @Operation(summary = "创建笔记", description = "创建新的学习笔记")
  public ResponseEntity<ApiResult<LearningNote>> createNote(
      @RequestBody LearningNote note, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    note.setUserId(userId);
    LearningNote createdNote = noteService.createNote(note);
    return ResponseEntity.ok(ApiResult.success(createdNote));
  }

  @PutMapping("/{noteId}")
  @Operation(summary = "更新笔记", description = "更新指定的学习笔记")
  public ResponseEntity<ApiResult<LearningNote>> updateNote(
      @PathVariable Long noteId, @RequestBody LearningNote note, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    if (!noteService.hasNotePermission(userId, noteId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此笔记"));
    }
    LearningNote updatedNote = noteService.updateNote(noteId, note);
    return ResponseEntity.ok(ApiResult.success(updatedNote));
  }

  @DeleteMapping("/{noteId}")
  @Operation(summary = "删除笔记", description = "删除指定的学习笔记")
  public ResponseEntity<ApiResult<Void>> deleteNote(
      @PathVariable Long noteId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    if (!noteService.hasNotePermission(userId, noteId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此笔记"));
    }
    noteService.deleteNote(noteId);
    return ResponseEntity.ok(ApiResult.success());
  }

  @GetMapping("/{noteId}")
  @Operation(summary = "获取笔记详情", description = "获取指定笔记的详细信息")
  public ResponseEntity<ApiResult<LearningNote>> getNoteById(@PathVariable Long noteId) {
    LearningNote note = noteService.getNoteById(noteId);
    // 增加查看次数
    noteService.incrementViewCount(noteId);
    return ResponseEntity.ok(ApiResult.success(note));
  }

  @GetMapping("/my")
  @Operation(summary = "获取我的笔记", description = "获取当前用户的笔记列表")
  public ResponseEntity<ApiResult<List<LearningNote>>> getMyNotes(
      Authentication authentication, @PageableDefault(size = 20) Pageable pageable) {
    Long userId = getCurrentUserId(authentication);
    Page<LearningNote> notes = noteService.getUserNotes(userId, pageable);
    return ResponseEntity.ok(ApiResult.success(notes.getContent()));
  }

  @GetMapping("/my/type/{noteType}")
  @Operation(summary = "获取我的笔记（按类型）", description = "获取当前用户指定类型的笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> getMyNotesByType(
      @PathVariable String noteType, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<LearningNote> notes = noteService.getUserNotesByType(userId, noteType);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/my/vulnerability/{vulnerabilityCode}")
  @Operation(summary = "获取我的笔记（按漏洞）", description = "获取当前用户指定漏洞的笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> getMyNotesByVulnerability(
      @PathVariable String vulnerabilityCode, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<LearningNote> notes = noteService.getUserNotesByVulnerability(userId, vulnerabilityCode);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/public")
  @Operation(summary = "获取公开笔记", description = "获取所有公开的笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> getPublicNotes(
      @PageableDefault(size = 20) Pageable pageable) {
    Page<LearningNote> notes = noteService.getPublicNotes(pageable);
    return ResponseEntity.ok(ApiResult.success(notes.getContent()));
  }

  @GetMapping("/public/vulnerability/{vulnerabilityCode}")
  @Operation(summary = "获取公开笔记（按漏洞）", description = "获取指定漏洞的公开笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> getPublicNotesByVulnerability(
      @PathVariable String vulnerabilityCode) {
    List<LearningNote> notes = noteService.getPublicNotesByVulnerability(vulnerabilityCode);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/search")
  @Operation(summary = "搜索笔记", description = "根据关键词搜索笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> searchNotes(
      @Parameter(description = "搜索关键词") @RequestParam String keyword,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<LearningNote> notes = noteService.searchNotes(userId, keyword);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/search/fulltext")
  @Operation(summary = "全文搜索笔记", description = "对笔记内容进行全文搜索")
  public ResponseEntity<ApiResult<List<LearningNote>>> fullTextSearchNotes(
      @Parameter(description = "搜索关键词") @RequestParam String keyword,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<LearningNote> notes = noteService.fullTextSearchNotes(userId, keyword);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/search/tag")
  @Operation(summary = "按标签搜索笔记", description = "根据标签搜索笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> searchNotesByTag(
      @Parameter(description = "标签名") @RequestParam String tag, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<LearningNote> notes = noteService.searchNotesByTag(userId, tag);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/my/pinned")
  @Operation(summary = "获取置顶笔记", description = "获取当前用户的置顶笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> getPinnedNotes(
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<LearningNote> notes = noteService.getPinnedNotes(userId);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/my/recent")
  @Operation(summary = "获取最近笔记", description = "获取当前用户最近修改的笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> getRecentNotes(
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    List<LearningNote> notes = noteService.getRecentModifiedNotes(userId);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/popular")
  @Operation(summary = "获取热门笔记", description = "获取热门笔记列表")
  public ResponseEntity<ApiResult<List<LearningNote>>> getPopularNotes(
      @PageableDefault(size = 10) Pageable pageable) {
    List<LearningNote> notes = noteService.getPopularNotes(pageable);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @GetMapping("/latest")
  @Operation(summary = "获取最新笔记", description = "获取最新发布的笔记")
  public ResponseEntity<ApiResult<List<LearningNote>>> getLatestNotes(
      @PageableDefault(size = 10) Pageable pageable) {
    List<LearningNote> notes = noteService.getLatestNotes(pageable);
    return ResponseEntity.ok(ApiResult.success(notes));
  }

  @PostMapping("/{noteId}/pin")
  @Operation(summary = "置顶/取消置顶笔记", description = "置顶或取消置顶笔记")
  public ResponseEntity<ApiResult<Void>> togglePin(
      @PathVariable Long noteId, @RequestParam Boolean isPinned, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    if (!noteService.hasNotePermission(userId, noteId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此笔记"));
    }
    noteService.togglePin(noteId, isPinned);
    return ResponseEntity.ok(ApiResult.success());
  }

  @PostMapping("/{noteId}/public")
  @Operation(summary = "公开/私有笔记", description = "设置笔记为公开或私有")
  public ResponseEntity<ApiResult<Void>> togglePublic(
      @PathVariable Long noteId, @RequestParam Boolean isPublic, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    if (!noteService.hasNotePermission(userId, noteId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此笔记"));
    }
    noteService.togglePublic(noteId, isPublic);
    return ResponseEntity.ok(ApiResult.success());
  }

  @GetMapping("/stats/my")
  @Operation(summary = "获取我的笔记统计", description = "获取当前用户的笔记统计信息")
  public ResponseEntity<ApiResult<Map<String, Object>>> getMyNoteStats(
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    Map<String, Object> stats = noteService.getUserNoteStats(userId);
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  @GetMapping("/stats/global")
  @Operation(summary = "获取全局笔记统计", description = "获取全局笔记统计信息")
  public ResponseEntity<ApiResult<Map<String, Object>>> getGlobalNoteStats() {
    Map<String, Object> stats = noteService.getNoteStats();
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  /** 获取当前用户ID */
  private Long getCurrentUserId(Authentication authentication) {
    // 优先使用AuthenticationService获取用户ID
    Long userId = authenticationService.getCurrentUserId();
    if (userId != null) {
      log.debug("从AuthenticationService获取的用户ID: {}", userId);
      return userId;
    }

    // 备用方案：从Authentication对象获取
    if (authentication != null && authentication.getPrincipal() != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
        String username =
            ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        log.debug("当前用户: {}", username);
        userId = authenticationService.getCurrentUserId();
        if (userId != null) {
          return userId;
        }
      }
    }

    log.warn("无法获取当前用户ID");
    throw new IllegalStateException("无法获取当前用户ID，请先登录");
  }
}
