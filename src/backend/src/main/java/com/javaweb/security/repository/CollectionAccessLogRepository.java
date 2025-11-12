package com.javaweb.security.repository;

import com.javaweb.security.entity.CollectionAccessLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 收藏访问记录Repository接口 */
@Repository
public interface CollectionAccessLogRepository extends JpaRepository<CollectionAccessLog, Long> {

  /** 根据收藏夹ID查找访问记录 */
  List<CollectionAccessLog> findByCollectionId(Long collectionId);

  /** 根据用户ID查找访问记录 */
  List<CollectionAccessLog> findByUserId(Long userId);

  /** 根据访问类型查找访问记录 */
  List<CollectionAccessLog> findByAccessType(String accessType);

  /** 根据IP地址查找访问记录 */
  List<CollectionAccessLog> findByIpAddress(String ipAddress);

  /** 查找指定时间范围内的访问记录 */
  List<CollectionAccessLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  /** 统计收藏夹的访问次数 */
  long countByCollectionId(Long collectionId);

  /** 统计收藏夹的指定类型访问次数 */
  long countByCollectionIdAndAccessType(Long collectionId, String accessType);

  /** 统计用户在指定时间范围内的访问次数 */
  long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

  /** 统计IP地址的访问次数 */
  long countByIpAddress(String ipAddress);

  /** 查找收藏夹的最近访问记录 */
  @Query(
      "SELECT l FROM CollectionAccessLog l WHERE l.collectionId = :collectionId ORDER BY l.createdAt DESC")
  List<CollectionAccessLog> findRecentByCollectionId(@Param("collectionId") Long collectionId);

  /** 查找用户的最近访问记录 */
  @Query("SELECT l FROM CollectionAccessLog l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
  List<CollectionAccessLog> findRecentByUserId(@Param("userId") Long userId);

  /** 查找热门收藏夹（按访问次数排序） */
  @Query(
      "SELECT l.collectionId, COUNT(l) as accessCount FROM CollectionAccessLog l GROUP BY l.collectionId ORDER BY accessCount DESC")
  List<Object[]> findPopularCollections();

  /** 查找访问统计信息 */
  @Query(
      "SELECT l.accessType, COUNT(l) FROM CollectionAccessLog l WHERE l.collectionId = :collectionId GROUP BY l.accessType")
  List<Object[]> findAccessStatsByCollectionId(@Param("collectionId") Long collectionId);

  /** 查找用户访问统计信息 */
  @Query(
      "SELECT l.accessType, COUNT(l) FROM CollectionAccessLog l WHERE l.userId = :userId GROUP BY l.accessType")
  List<Object[]> findAccessStatsByUserId(@Param("userId") Long userId);

  /** 删除收藏夹的所有访问记录 */
  void deleteByCollectionId(Long collectionId);

  /** 删除用户的访问记录 */
  void deleteByUserId(Long userId);

  /** 删除指定时间之前的访问记录 */
  void deleteByCreatedAtBefore(LocalDateTime before);

  /** 查找重复访问（同一IP短时间内多次访问） */
  @Query(
      "SELECT l FROM CollectionAccessLog l WHERE l.ipAddress = :ipAddress AND l.createdAt > :since")
  List<CollectionAccessLog> findRecentByIpAddress(
      @Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}
