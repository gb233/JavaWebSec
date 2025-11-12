package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.CollectionItem;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.CollectionItemService;
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

/** 收藏项控制器 */
@Slf4j
@RestController
@RequestMapping("/api/v1/collection-items")
@Tag(name = "收藏项管理", description = "收藏项的增删改查及统计接口")
public class CollectionItemController {

  @Autowired private CollectionItemService collectionItemService;
  @Autowired private CollectionService collectionService;
  @Autowired private AuthenticationService authenticationService;

  @Operation(summary = "添加收藏项", description = "向收藏夹添加新的收藏项")
  @PostMapping
  public ResponseEntity<ApiResult<CollectionItem>> addCollectionItem(
      @RequestBody CollectionItem collectionItem, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能向自己的收藏夹添加项目
    if (!collectionService.existsByIdAndUserId(collectionItem.getCollectionId(), userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏夹"));
    }
    CollectionItem addedItem = collectionItemService.addCollectionItem(collectionItem);
    return ResponseEntity.ok(ApiResult.success(addedItem));
  }

  @Operation(summary = "更新收藏项", description = "根据收藏项ID更新现有收藏项")
  @PutMapping("/{itemId}")
  public ResponseEntity<ApiResult<CollectionItem>> updateCollectionItem(
      @PathVariable Long itemId,
      @RequestBody CollectionItem collectionItem,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能更新自己收藏夹中的项目
    if (!collectionItemService.hasItemPermission(userId, itemId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏项"));
    }
    CollectionItem updatedItem = collectionItemService.updateCollectionItem(itemId, collectionItem);
    return ResponseEntity.ok(ApiResult.success(updatedItem));
  }

  @Operation(summary = "删除收藏项", description = "根据收藏项ID删除收藏项")
  @DeleteMapping("/{itemId}")
  public ResponseEntity<ApiResult<Void>> deleteCollectionItem(
      @PathVariable Long itemId, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能删除自己收藏夹中的项目
    if (!collectionItemService.hasItemPermission(userId, itemId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏项"));
    }
    collectionItemService.deleteCollectionItem(itemId);
    return ResponseEntity.ok(ApiResult.success(null));
  }

  @Operation(summary = "获取单个收藏项", description = "根据收藏项ID获取收藏项详情")
  @GetMapping("/{itemId}")
  public ResponseEntity<ApiResult<CollectionItem>> getCollectionItemById(
      @PathVariable Long itemId) {
    return collectionItemService
        .getCollectionItemById(itemId)
        .map(item -> ResponseEntity.ok(ApiResult.success(item)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @Operation(summary = "获取收藏夹的所有收藏项", description = "根据收藏夹ID获取所有收藏项")
  @GetMapping("/collection/{collectionId}")
  public ResponseEntity<ApiResult<List<CollectionItem>>> getCollectionItemsByCollectionId(
      @PathVariable Long collectionId) {
    List<CollectionItem> items =
        collectionItemService.getCollectionItemsByCollectionId(collectionId);
    return ResponseEntity.ok(ApiResult.success(items));
  }

  @Operation(summary = "分页获取收藏夹的收藏项", description = "分页获取收藏夹的收藏项")
  @GetMapping("/collection/{collectionId}/page")
  public ResponseEntity<ApiResult<Page<CollectionItem>>> getCollectionItemsByCollectionIdPaged(
      @PathVariable Long collectionId, @PageableDefault Pageable pageable) {
    Page<CollectionItem> items =
        collectionItemService.getCollectionItemsByCollectionId(collectionId, pageable);
    return ResponseEntity.ok(ApiResult.success(items));
  }

  @Operation(summary = "获取收藏夹指定类型的收藏项", description = "获取收藏夹中指定类型的收藏项")
  @GetMapping("/collection/{collectionId}/type/{itemType}")
  public ResponseEntity<ApiResult<List<CollectionItem>>> getCollectionItemsByType(
      @PathVariable Long collectionId, @PathVariable String itemType) {
    List<CollectionItem> items =
        collectionItemService.getCollectionItemsByCollectionIdAndType(collectionId, itemType);
    return ResponseEntity.ok(ApiResult.success(items));
  }

  @Operation(summary = "获取我的所有收藏项", description = "获取当前用户的所有收藏项")
  @GetMapping("/my")
  public ResponseEntity<ApiResult<List<CollectionItem>>> getMyCollectionItems(
      Authentication authentication) {
    List<CollectionItem> items =
        collectionItemService.getCollectionItemsByUserId(getCurrentUserId(authentication));
    return ResponseEntity.ok(ApiResult.success(items));
  }

  @Operation(summary = "获取我的指定类型收藏项", description = "获取当前用户指定类型的收藏项")
  @GetMapping("/my/type/{itemType}")
  public ResponseEntity<ApiResult<List<CollectionItem>>> getMyCollectionItemsByType(
      @PathVariable String itemType, Authentication authentication) {
    List<CollectionItem> items =
        collectionItemService.getCollectionItemsByUserIdAndType(
            getCurrentUserId(authentication), itemType);
    return ResponseEntity.ok(ApiResult.success(items));
  }

  @Operation(summary = "搜索收藏项", description = "在指定收藏夹中搜索收藏项")
  @GetMapping("/search")
  public ResponseEntity<ApiResult<List<CollectionItem>>> searchCollectionItems(
      @Parameter(description = "收藏夹ID", required = true) @RequestParam Long collectionId,
      @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword) {
    List<CollectionItem> items = collectionItemService.searchCollectionItems(collectionId, keyword);
    return ResponseEntity.ok(ApiResult.success(items));
  }

  @Operation(summary = "检查收藏项是否存在", description = "检查指定收藏项是否已存在")
  @GetMapping("/exists")
  public ResponseEntity<ApiResult<Boolean>> checkCollectionItemExists(
      @Parameter(description = "收藏夹ID", required = true) @RequestParam Long collectionId,
      @Parameter(description = "收藏项类型", required = true) @RequestParam String itemType,
      @Parameter(description = "收藏项ID", required = true) @RequestParam Long itemId) {
    boolean exists =
        collectionItemService.existsByCollectionIdAndItemTypeAndItemId(
            collectionId, itemType, itemId);
    return ResponseEntity.ok(ApiResult.success(exists));
  }

  @Operation(summary = "获取收藏项统计", description = "获取指定收藏夹的收藏项统计信息")
  @GetMapping("/stats/collection/{collectionId}")
  public ResponseEntity<ApiResult<Map<String, Long>>> getCollectionItemStats(
      @PathVariable Long collectionId) {
    Map<String, Long> stats = collectionItemService.getCollectionItemStats(collectionId);
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  @Operation(summary = "获取我的收藏项统计", description = "获取当前用户的收藏项统计信息")
  @GetMapping("/stats/my")
  public ResponseEntity<ApiResult<Map<String, Long>>> getMyCollectionItemStats(
      Authentication authentication) {
    Map<String, Long> stats =
        collectionItemService.getUserCollectionItemStats(getCurrentUserId(authentication));
    return ResponseEntity.ok(ApiResult.success(stats));
  }

  @Operation(summary = "获取我的最近收藏项", description = "获取当前用户最近添加的收藏项")
  @GetMapping("/my/recent")
  public ResponseEntity<ApiResult<List<CollectionItem>>> getMyRecentCollectionItems(
      Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {
    List<CollectionItem> items =
        collectionItemService.getRecentCollectionItemsByUserId(
            getCurrentUserId(authentication), pageable);
    return ResponseEntity.ok(ApiResult.success(items));
  }

  @Operation(summary = "批量添加收藏项", description = "批量向收藏夹添加收藏项")
  @PostMapping("/batch")
  public ResponseEntity<ApiResult<List<CollectionItem>>> batchAddCollectionItems(
      @Parameter(description = "收藏夹ID", required = true) @RequestParam Long collectionId,
      @RequestBody List<CollectionItem> items,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能向自己的收藏夹添加项目
    if (!collectionService.existsByIdAndUserId(collectionId, userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏夹"));
    }
    List<CollectionItem> addedItems =
        collectionItemService.batchAddCollectionItems(collectionId, items);
    return ResponseEntity.ok(ApiResult.success(addedItems));
  }

  @Operation(summary = "批量删除收藏项", description = "批量删除收藏项")
  @DeleteMapping("/batch")
  public ResponseEntity<ApiResult<Void>> batchDeleteCollectionItems(
      @RequestBody List<Long> itemIds, Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能删除自己收藏夹中的项目
    for (Long itemId : itemIds) {
      if (!collectionItemService.hasItemPermission(userId, itemId)) {
        return ResponseEntity.badRequest().body(ApiResult.error("无权限操作某些收藏项"));
      }
    }
    collectionItemService.batchDeleteCollectionItems(itemIds);
    return ResponseEntity.ok(ApiResult.success(null));
  }

  @Operation(summary = "移动收藏项", description = "将收藏项移动到其他收藏夹")
  @PutMapping("/{itemId}/move")
  public ResponseEntity<ApiResult<CollectionItem>> moveCollectionItem(
      @PathVariable Long itemId,
      @Parameter(description = "目标收藏夹ID", required = true) @RequestParam Long targetCollectionId,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能移动自己收藏夹中的项目，且目标收藏夹也必须是自己的
    if (!collectionItemService.hasItemPermission(userId, itemId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏项"));
    }
    if (!collectionService.existsByIdAndUserId(targetCollectionId, userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作目标收藏夹"));
    }
    CollectionItem movedItem = collectionItemService.moveCollectionItem(itemId, targetCollectionId);
    return ResponseEntity.ok(ApiResult.success(movedItem));
  }

  @Operation(summary = "复制收藏项", description = "将收藏项复制到其他收藏夹")
  @PostMapping("/{itemId}/copy")
  public ResponseEntity<ApiResult<CollectionItem>> copyCollectionItem(
      @PathVariable Long itemId,
      @Parameter(description = "目标收藏夹ID", required = true) @RequestParam Long targetCollectionId,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    // 检查权限：只能复制自己收藏夹中的项目，且目标收藏夹也必须是自己的
    if (!collectionItemService.hasItemPermission(userId, itemId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作此收藏项"));
    }
    if (!collectionService.existsByIdAndUserId(targetCollectionId, userId)) {
      return ResponseEntity.badRequest().body(ApiResult.error("无权限操作目标收藏夹"));
    }
    CollectionItem copiedItem =
        collectionItemService.copyCollectionItem(itemId, targetCollectionId);
    return ResponseEntity.ok(ApiResult.success(copiedItem));
  }

  @Operation(summary = "快速添加收藏项", description = "快速添加收藏项到收藏夹，如果未指定收藏夹则使用默认收藏夹")
  @PostMapping("/quick-add")
  public ResponseEntity<ApiResult<CollectionItem>> quickAddItem(
      @Parameter(description = "收藏项类型", required = true) @RequestParam String itemType,
      @Parameter(description = "收藏项ID", required = true) @RequestParam Long itemId,
      @Parameter(description = "收藏项标题", required = true) @RequestParam String itemTitle,
      @Parameter(description = "收藏项描述") @RequestParam(required = false) String itemDescription,
      @Parameter(description = "收藏项URL") @RequestParam(required = false) String itemUrl,
      @Parameter(description = "收藏夹ID，不指定则使用默认收藏夹") @RequestParam(required = false)
          Long collectionId,
      Authentication authentication) {
    Long userId = getCurrentUserId(authentication);
    try {
      CollectionItem addedItem =
          collectionItemService.quickAddItem(
              userId, itemType, itemId, itemTitle, itemDescription, itemUrl, collectionId);
      return ResponseEntity.ok(ApiResult.success(addedItem));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body(ApiResult.error(e.getMessage()));
    }
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
        log.debug(
            "当前用户: {}",
            ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername());
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
