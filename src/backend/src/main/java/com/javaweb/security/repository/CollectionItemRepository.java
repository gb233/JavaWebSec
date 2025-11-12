package com.javaweb.security.repository;

import com.javaweb.security.entity.CollectionItem;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 收藏项Repository接口 */
@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {

  /** 根据收藏夹ID查找收藏项 */
  List<CollectionItem> findByCollectionId(Long collectionId);

  /** 根据收藏夹ID分页查找收藏项 */
  Page<CollectionItem> findByCollectionId(Long collectionId, Pageable pageable);

  /** 根据收藏夹ID和收藏项类型查找收藏项 */
  List<CollectionItem> findByCollectionIdAndItemType(Long collectionId, String itemType);

  /** 根据收藏夹ID和收藏项ID查找收藏项 */
  Optional<CollectionItem> findByCollectionIdAndItemId(Long collectionId, Long itemId);

  /** 根据收藏夹ID、收藏项类型和收藏项ID查找收藏项 */
  Optional<CollectionItem> findByCollectionIdAndItemTypeAndItemId(
      Long collectionId, String itemType, Long itemId);

  /** 根据收藏项类型查找收藏项 */
  List<CollectionItem> findByItemType(String itemType);

  /** 根据收藏项ID和类型查找收藏项 */
  List<CollectionItem> findByItemIdAndItemType(Long itemId, String itemType);

  /** 根据标题搜索收藏项 */
  @Query(
      "SELECT ci FROM CollectionItem ci WHERE ci.collectionId = :collectionId AND ci.itemTitle LIKE %:keyword%")
  List<CollectionItem> findByCollectionIdAndTitleContaining(
      @Param("collectionId") Long collectionId, @Param("keyword") String keyword);

  /** 根据描述搜索收藏项 */
  @Query(
      "SELECT ci FROM CollectionItem ci WHERE ci.collectionId = :collectionId AND ci.itemDescription LIKE %:keyword%")
  List<CollectionItem> findByCollectionIdAndDescriptionContaining(
      @Param("collectionId") Long collectionId, @Param("keyword") String keyword);

  /** 查找最近添加的收藏项 */
  @Query(
      "SELECT ci FROM CollectionItem ci WHERE ci.collectionId = :collectionId ORDER BY ci.addedAt DESC")
  List<CollectionItem> findRecentByCollectionId(
      @Param("collectionId") Long collectionId, Pageable pageable);

  /** 统计收藏夹中的收藏项数量 */
  long countByCollectionId(Long collectionId);

  /** 统计收藏夹中指定类型的收藏项数量 */
  long countByCollectionIdAndItemType(Long collectionId, String itemType);

  /** 统计用户在指定时间范围内添加的收藏项数量 */
  long countByCollectionIdAndAddedAtBetween(
      Long collectionId, LocalDateTime start, LocalDateTime end);

  /** 查找用户的所有收藏项（通过收藏夹关联） */
  @Query(
      "SELECT ci FROM CollectionItem ci JOIN Collection c ON ci.collectionId = c.id WHERE c.userId = :userId")
  List<CollectionItem> findByUserId(@Param("userId") Long userId);

  /** 查找用户指定类型的收藏项 */
  @Query(
      "SELECT ci FROM CollectionItem ci JOIN Collection c ON ci.collectionId = c.id WHERE c.userId = :userId AND ci.itemType = :itemType")
  List<CollectionItem> findByUserIdAndItemType(
      @Param("userId") Long userId, @Param("itemType") String itemType);

  /** 查找用户最近添加的收藏项 */
  @Query(
      "SELECT ci FROM CollectionItem ci JOIN Collection c ON ci.collectionId = c.id WHERE c.userId = :userId ORDER BY ci.addedAt DESC")
  List<CollectionItem> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

  /** 检查收藏项是否已存在 */
  boolean existsByCollectionIdAndItemTypeAndItemId(Long collectionId, String itemType, Long itemId);

  /** 删除收藏夹中的所有收藏项 */
  void deleteByCollectionId(Long collectionId);

  /** 删除指定收藏项 */
  void deleteByCollectionIdAndItemTypeAndItemId(Long collectionId, String itemType, Long itemId);
}
