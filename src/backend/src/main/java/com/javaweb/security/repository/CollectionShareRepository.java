package com.javaweb.security.repository;

import com.javaweb.security.entity.CollectionShare;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 收藏分享Repository接口 */
@Repository
public interface CollectionShareRepository extends JpaRepository<CollectionShare, Long> {

  /** 根据分享码查找分享 */
  Optional<CollectionShare> findByShareCode(String shareCode);

  /** 根据收藏夹ID查找分享 */
  List<CollectionShare> findByCollectionId(Long collectionId);

  /** 根据分享类型查找分享 */
  List<CollectionShare> findByShareType(String shareType);

  /** 查找有效的分享（未过期） */
  @Query(
      "SELECT s FROM CollectionShare s WHERE s.shareCode = :shareCode AND (s.expiresAt IS NULL OR s.expiresAt > :now)")
  Optional<CollectionShare> findValidByShareCode(
      @Param("shareCode") String shareCode, @Param("now") LocalDateTime now);

  /** 查找过期的分享 */
  @Query("SELECT s FROM CollectionShare s WHERE s.expiresAt IS NOT NULL AND s.expiresAt <= :now")
  List<CollectionShare> findExpiredShares(@Param("now") LocalDateTime now);

  /** 查找收藏夹的活跃分享 */
  @Query(
      "SELECT s FROM CollectionShare s WHERE s.collectionId = :collectionId AND (s.expiresAt IS NULL OR s.expiresAt > :now)")
  List<CollectionShare> findActiveByCollectionId(
      @Param("collectionId") Long collectionId, @Param("now") LocalDateTime now);

  /** 统计收藏夹的分享数量 */
  long countByCollectionId(Long collectionId);

  /** 统计分享的访问次数 */
  @Query("SELECT SUM(s.accessCount) FROM CollectionShare s WHERE s.collectionId = :collectionId")
  Long sumAccessCountByCollectionId(@Param("collectionId") Long collectionId);

  /** 增加分享访问次数 */
  @Query("UPDATE CollectionShare s SET s.accessCount = s.accessCount + 1 WHERE s.id = :shareId")
  void incrementAccessCount(@Param("shareId") Long shareId);

  /** 删除收藏夹的所有分享 */
  void deleteByCollectionId(Long collectionId);

  /** 删除过期的分享 */
  @Query("DELETE FROM CollectionShare s WHERE s.expiresAt IS NOT NULL AND s.expiresAt <= :now")
  void deleteExpiredShares(@Param("now") LocalDateTime now);

  /** 查找热门分享（按访问次数排序） */
  @Query("SELECT s FROM CollectionShare s WHERE s.shareType = 'public' ORDER BY s.accessCount DESC")
  List<CollectionShare> findPopularShares();

  /** 查找最近创建的分享 */
  @Query("SELECT s FROM CollectionShare s ORDER BY s.createdAt DESC")
  List<CollectionShare> findRecentShares();

  /** 查找用户收藏夹的分享 */
  @Query(
      "SELECT s FROM CollectionShare s JOIN Collection c ON s.collectionId = c.id WHERE c.userId = :userId")
  List<CollectionShare> findByUserId(@Param("userId") Long userId);
}
