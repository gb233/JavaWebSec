package com.javaweb.security.repository;

import com.javaweb.security.entity.Collection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 收藏夹Repository接口 */
@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {

  /** 根据用户ID查找收藏夹 */
  List<Collection> findByUserId(Long userId);

  /** 根据用户ID分页查找收藏夹 */
  Page<Collection> findByUserId(Long userId, Pageable pageable);

  /** 根据用户ID和公开状态查找收藏夹 */
  List<Collection> findByUserIdAndIsPublic(Long userId, Boolean isPublic);

  /** 根据用户ID和默认状态查找收藏夹 */
  List<Collection> findByUserIdAndIsDefault(Long userId, Boolean isDefault);

  /** 根据收藏夹ID和用户ID查找收藏夹 */
  Optional<Collection> findByIdAndUserId(Long id, Long userId);

  /** 查找公开收藏夹 */
  Page<Collection> findByIsPublicTrue(Pageable pageable);

  /** 根据名称搜索收藏夹 */
  @Query("SELECT c FROM Collection c WHERE c.userId = :userId AND c.name LIKE %:keyword%")
  List<Collection> findByUserIdAndNameContaining(
      @Param("userId") Long userId, @Param("keyword") String keyword);

  /** 根据描述搜索收藏夹 */
  @Query("SELECT c FROM Collection c WHERE c.userId = :userId AND c.description LIKE %:keyword%")
  List<Collection> findByUserIdAndDescriptionContaining(
      @Param("userId") Long userId, @Param("keyword") String keyword);

  /** 查找用户的默认收藏夹 */
  @Query("SELECT c FROM Collection c WHERE c.userId = :userId AND c.isDefault = true")
  Optional<Collection> findDefaultByUserId(@Param("userId") Long userId);

  /** 统计用户收藏夹数量 */
  long countByUserId(Long userId);

  /** 统计用户公开收藏夹数量 */
  long countByUserIdAndIsPublicTrue(Long userId);

  /** 统计公开收藏夹数量 */
  long countByIsPublicTrue();

  /** 统计用户在指定时间范围内创建的收藏夹数量 */
  long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

  /** 查找最近创建的收藏夹 */
  @Query("SELECT c FROM Collection c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
  List<Collection> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

  /** 查找最近更新的收藏夹 */
  @Query("SELECT c FROM Collection c WHERE c.userId = :userId ORDER BY c.updatedAt DESC")
  List<Collection> findRecentlyUpdatedByUserId(@Param("userId") Long userId, Pageable pageable);

  /** 查找热门收藏夹（按访问次数排序） */
  @Query("SELECT c FROM Collection c WHERE c.isPublic = true ORDER BY c.viewCount DESC")
  List<Collection> findPopularCollections(Pageable pageable);

  /** 查找用户的收藏夹统计信息 */
  @Query(
      "SELECT c.isPublic, COUNT(c) FROM Collection c WHERE c.userId = :userId GROUP BY c.isPublic")
  List<Object[]> countByUserIdGroupByIsPublic(@Param("userId") Long userId);
}
