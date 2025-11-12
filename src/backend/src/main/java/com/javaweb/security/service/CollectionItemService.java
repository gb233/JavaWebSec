package com.javaweb.security.service;

import com.javaweb.security.entity.CollectionItem;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** 收藏项服务接口 */
public interface CollectionItemService {

  /** 添加收藏项 */
  CollectionItem addCollectionItem(CollectionItem collectionItem);

  /** 更新收藏项 */
  CollectionItem updateCollectionItem(Long id, CollectionItem collectionItem);

  /** 删除收藏项 */
  void deleteCollectionItem(Long id);

  /** 根据ID获取收藏项 */
  Optional<CollectionItem> getCollectionItemById(Long id);

  /** 获取收藏夹的所有收藏项 */
  List<CollectionItem> getCollectionItemsByCollectionId(Long collectionId);

  /** 分页获取收藏夹的收藏项 */
  Page<CollectionItem> getCollectionItemsByCollectionId(Long collectionId, Pageable pageable);

  /** 获取收藏夹指定类型的收藏项 */
  List<CollectionItem> getCollectionItemsByCollectionIdAndType(Long collectionId, String itemType);

  /** 获取用户的所有收藏项 */
  List<CollectionItem> getCollectionItemsByUserId(Long userId);

  /** 获取用户指定类型的收藏项 */
  List<CollectionItem> getCollectionItemsByUserIdAndType(Long userId, String itemType);

  /** 搜索收藏项 */
  List<CollectionItem> searchCollectionItems(Long collectionId, String keyword);

  /** 检查收藏项是否已存在 */
  boolean existsByCollectionIdAndItemTypeAndItemId(Long collectionId, String itemType, Long itemId);

  /** 获取收藏项统计信息 */
  Map<String, Long> getCollectionItemStats(Long collectionId);

  /** 获取用户收藏项统计信息 */
  Map<String, Long> getUserCollectionItemStats(Long userId);

  /** 获取用户最近添加的收藏项 */
  List<CollectionItem> getRecentCollectionItemsByUserId(Long userId, Pageable pageable);

  /** 批量添加收藏项 */
  List<CollectionItem> batchAddCollectionItems(Long collectionId, List<CollectionItem> items);

  /** 批量删除收藏项 */
  void batchDeleteCollectionItems(List<Long> itemIds);

  /** 移动收藏项到其他收藏夹 */
  CollectionItem moveCollectionItem(Long itemId, Long targetCollectionId);

  /** 复制收藏项到其他收藏夹 */
  CollectionItem copyCollectionItem(Long itemId, Long targetCollectionId);

  /** 检查用户是否有权限操作收藏项 */
  boolean hasItemPermission(Long userId, Long itemId);

  /** 快速添加收藏项，如果未指定收藏夹则使用默认收藏夹 */
  CollectionItem quickAddItem(
      Long userId,
      String itemType,
      Long itemId,
      String itemTitle,
      String itemDescription,
      String itemUrl,
      Long collectionId);
}
