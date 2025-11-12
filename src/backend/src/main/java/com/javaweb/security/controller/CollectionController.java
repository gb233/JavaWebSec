package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.Collection;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.CollectionService;
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

/** 收藏夹控制器 */
@Slf4j
@RestController
@RequestMapping("/api/v1/collections")
@Tag(name = "收藏夹管理", description = "收藏夹的增删改查及统计接口")
public class CollectionController {

  @Autowired private CollectionService collectionService;
  @Autowired private AuthenticationService authenticationService;

  @Operation(summary = "创建收藏夹", description = "用户创建新的收藏夹")
  @PostMapping
  public ResponseEntity<ApiResult<Collection>> createCollection(
      @RequestBody Collection collection, Authentication authentication) {
    collection.setUserId(getCurrentUserId(authentication));
    Collection createdCollection = collectionService.createCollection(collection);
    return ResponseEntity.ok(ApiResult.success(createdCollection));
  }

  @Operation(summary = "更新收藏夹", description = "根据收藏夹ID更新现有收藏夹")
  @PutMapping("/{collectionId}")
  public ResponseEntity<ApiResult<Collection>> updateCollection(
      @PathVariable Long collectionId,
      @RequestBody Collection collection,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能更新自己的收藏夹
    if (!collectionService.existsByIdAndUserId(collectionId, userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏夹"));
    }
    Collection updatedCollection = collectionService.updateCollection(collectionId, collection);
    return ResponseEntity.ok(ApiResult.success(updatedCollection));
  }

  @Operation(summary = "删除收藏夹", description = "根据收藏夹ID删除收藏夹")
  @DeleteMapping("/{collectionId}")
  public ResponseEntity<ApiResult<Void>> deleteCollection(
      @PathVariable Long collectionId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能删除自己的收藏夹
    if (!collectionService.existsByIdAndUserId(collectionId, userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏夹"));
    }
    collectionService.deleteCollection(collectionId);
    return ResponseEntity.ok(ApiResult.success(null));
  }

  @Operation(summary = "获取单个收藏夹", description = "根据收藏夹ID获取收藏夹详情")
  @GetMapping("/{collectionId}")
  public ResponseEntity<ApiResult<Collection>> getCollectionById(@PathVariable Long collectionId) {
    return collectionService
        .getCollectionById(collectionId)
        .map(collection -> ResponseEntity.ok(ApiResult.success(collection)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "获取我的所有收藏夹", description = "获取当前用户的所有收藏夹")
  @GetMapping("/my")
  public ResponseEntity<ApiResult<List<Collection>>> getMyCollections(
      Authentication authentication) {
    List<Collection> collections =
        collectionService.getCollectionsByUserId(getCurrentUserId(authentication));
    return ResponseEntity.ok(ApiResult.success(collections));
  }

  @Operation(summary = "分页获取我的收藏夹", description = "分页获取当前用户的所有收藏夹")
  @GetMapping("/my/page")
  public ResponseEntity<ApiResult<Page<Collection>>> getMyCollectionsPaged(
      Authentication authentication, @PageableDefault Pageable pageable) {
    Page<Collection> collections =
        collectionService.getCollectionsByUserId(getCurrentUserId(authentication), pageable);
    return ResponseEntity.ok(ApiResult.success(collections));
  }

  @Operation(summary = "获取我的公开收藏夹", description = "获取当前用户的所有公开收藏夹")
  @GetMapping("/my/public")
  public ResponseEntity<ApiResult<List<Collection>>> getMyPublicCollections(
      Authentication authentication) {
    List<Collection> collections =
        collectionService.getPublicCollectionsByUserId(getCurrentUserId(authentication));
    return ResponseEntity.ok(ApiResult.success(collections));
  }

  @Operation(summary = "获取我的默认收藏夹", description = "获取当前用户的默认收藏夹")
  @GetMapping("/my/default")
  public ResponseEntity<ApiResult<Collection>> getMyDefaultCollection(
      Authentication authentication) {
    return collectionService
        .getDefaultCollectionByUserId(getCurrentUserId(authentication))
        .map(collection -> ResponseEntity.ok(ApiResult.success(collection)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "获取所有公开收藏夹", description = "分页获取所有公开的收藏夹")
  @GetMapping("/public")
  public ResponseEntity<ApiResult<Page<Collection>>> getPublicCollections(
      @PageableDefault Pageable pageable) {
    Page<Collection> collections = collectionService.getPublicCollections(pageable);
    return ResponseEntity.ok(ApiResult.success(collections));
  }

  @Operation(summary = "搜索我的收藏夹", description = "对当前用户的收藏夹进行搜索")
  @GetMapping("/search")
  public ResponseEntity<ApiResult<List<Collection>>> searchMyCollections(
      @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
      Authentication authentication) {
    List<Collection> collections =
        collectionService.searchCollections(getCurrentUserId(authentication), keyword);
    return ResponseEntity.ok(ApiResult.success(collections));
  }

  @Operation(summary = "获取我的收藏夹统计", description = "获取当前用户的收藏夹统计信息")
  @GetMapping("/stats/my")
  public ResponseEntity<ApiResult<Map<String, Long>>> getMyCollectionStats(
      Authentication authentication) {
    Map<String, Long> stats =
        collectionService.getCollectionStats(getCurrentUserId(authentication));
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  @Operation(summary = "获取全局收藏夹统计", description = "获取所有收藏夹的全局统计信息")
  @GetMapping("/stats/global")
  public ResponseEntity<ApiResult<Map<String, Long>>> getGlobalCollectionStats() {
    Map<String, Long> stats = collectionService.getGlobalCollectionStats();
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  @Operation(summary = "设置默认收藏夹", description = "设置当前用户的默认收藏夹")
  @PutMapping("/{collectionId}/set-default")
  public ResponseEntity<ApiResult<Collection>> setDefaultCollection(
      @PathVariable Long collectionId, Authentication authentication) {
    Collection collection =
        collectionService.setDefaultCollection(getCurrentUserId(authentication), collectionId);
    return ResponseEntity.ok(ApiResult.success(collection));
  }

  @Operation(summary = "获取我的最近收藏夹", description = "获取当前用户最近创建的收藏夹")
  @GetMapping("/my/recent")
  public ResponseEntity<ApiResult<List<Collection>>> getMyRecentCollections(
      Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {
    List<Collection> collections =
        collectionService.getRecentCollectionsByUserId(getCurrentUserId(authentication), pageable);
    return ResponseEntity.ok(ApiResult.success(collections));
  }

  @Operation(summary = "获取热门收藏夹", description = "获取热门收藏夹")
  @GetMapping("/popular")
  public ResponseEntity<ApiResult<List<Collection>>> getPopularCollections(
      @PageableDefault(size = 10) Pageable pageable) {
    List<Collection> collections = collectionService.getPopularCollections(pageable);
    return ResponseEntity.ok(ApiResult.success(collections));
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
