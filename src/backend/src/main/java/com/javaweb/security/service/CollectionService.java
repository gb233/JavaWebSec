package com.javaweb.security.service;

import com.javaweb.security.entity.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** 收藏夹服务接口 */
public interface CollectionService {

  /** 创建收藏夹 */
  Collection createCollection(Collection collection);

  /** 更新收藏夹 */
  Collection updateCollection(Long id, Collection collection);

  /** 删除收藏夹 */
  void deleteCollection(Long id);

  /** 根据ID获取收藏夹 */
  Optional<Collection> getCollectionById(Long id);

  /** 获取用户的所有收藏夹 */
  List<Collection> getCollectionsByUserId(Long userId);

  /** 分页获取用户的收藏夹 */
  Page<Collection> getCollectionsByUserId(Long userId, Pageable pageable);

  /** 获取用户的公开收藏夹 */
  List<Collection> getPublicCollectionsByUserId(Long userId);

  /** 获取用户的默认收藏夹 */
  Optional<Collection> getDefaultCollectionByUserId(Long userId);

  /** 获取所有公开收藏夹 */
  Page<Collection> getPublicCollections(Pageable pageable);

  /** 搜索收藏夹 */
  List<Collection> searchCollections(Long userId, String keyword);

  /** 获取收藏夹统计信息 */
  Map<String, Long> getCollectionStats(Long userId);

  /** 获取全局收藏夹统计信息 */
  Map<String, Long> getGlobalCollectionStats();

  /** 设置默认收藏夹 */
  Collection setDefaultCollection(Long userId, Long collectionId);

  /** 检查收藏夹是否存在 */
  boolean existsByIdAndUserId(Long id, Long userId);

  /** 获取用户最近创建的收藏夹 */
  List<Collection> getRecentCollectionsByUserId(Long userId, Pageable pageable);

  /** 获取用户最近更新的收藏夹 */
  List<Collection> getRecentlyUpdatedCollectionsByUserId(Long userId, Pageable pageable);

  /** 获取热门收藏夹 */
  List<Collection> getPopularCollections(Pageable pageable);

  /** 确保用户有默认收藏夹，如果没有则创建一个 */
  Collection ensureDefaultCollection(Long userId);

  /** 获取或创建用户的默认收藏夹 */
  Collection getOrCreateDefaultCollection(Long userId);
}
