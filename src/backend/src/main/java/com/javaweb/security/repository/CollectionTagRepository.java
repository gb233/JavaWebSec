package com.javaweb.security.repository;

import com.javaweb.security.entity.CollectionTag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 收藏标签Repository接口 */
@Repository
public interface CollectionTagRepository extends JpaRepository<CollectionTag, Long> {

  /** 根据名称查找标签 */
  Optional<CollectionTag> findByName(String name);

  /** 根据名称模糊搜索标签 */
  List<CollectionTag> findByNameContaining(String keyword);

  /** 根据颜色查找标签 */
  List<CollectionTag> findByColor(String color);

  /** 查找热门标签（按使用次数排序） */
  @Query("SELECT t FROM CollectionTag t ORDER BY t.usageCount DESC")
  List<CollectionTag> findPopularTags();

  /** 查找使用次数大于指定值的标签 */
  List<CollectionTag> findByUsageCountGreaterThan(Integer usageCount);

  /** 根据使用次数范围查找标签 */
  List<CollectionTag> findByUsageCountBetween(Integer minUsage, Integer maxUsage);

  /** 统计标签数量 */
  long count();

  /** 统计使用次数大于指定值的标签数量 */
  long countByUsageCountGreaterThan(Integer usageCount);

  /** 增加标签使用次数 */
  @Query("UPDATE CollectionTag t SET t.usageCount = t.usageCount + 1 WHERE t.id = :tagId")
  void incrementUsageCount(@Param("tagId") Long tagId);

  /** 减少标签使用次数 */
  @Query("UPDATE CollectionTag t SET t.usageCount = t.usageCount - 1 WHERE t.id = :tagId")
  void decrementUsageCount(@Param("tagId") Long tagId);

  /** 查找未使用的标签 */
  @Query("SELECT t FROM CollectionTag t WHERE t.usageCount = 0")
  List<CollectionTag> findUnusedTags();

  /** 查找最常用的标签 */
  @Query("SELECT t FROM CollectionTag t ORDER BY t.usageCount DESC")
  List<CollectionTag> findMostUsedTags();

  /** 根据名称或描述搜索标签 */
  @Query(
      "SELECT t FROM CollectionTag t WHERE t.name LIKE %:keyword% OR t.description LIKE %:keyword%")
  List<CollectionTag> searchByNameOrDescription(@Param("keyword") String keyword);
}
