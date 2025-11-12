package com.javaweb.security.repository;

import com.javaweb.security.entity.NoteTag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 笔记标签Repository接口 */
@Repository
public interface NoteTagRepository extends JpaRepository<NoteTag, Long> {

  /** 根据标签名查找标签 */
  Optional<NoteTag> findByTagName(String tagName);

  /** 查找系统标签 */
  List<NoteTag> findByIsSystemTrue();

  /** 查找用户标签 */
  List<NoteTag> findByIsSystemFalse();

  /** 根据使用次数排序查找标签 */
  List<NoteTag> findByOrderByUsageCountDesc();

  /** 查找热门标签（使用次数大于指定值） */
  List<NoteTag> findByUsageCountGreaterThan(Integer minUsageCount);

  /** 根据标签名模糊搜索 */
  List<NoteTag> findByTagNameContainingIgnoreCase(String keyword);

  /** 统计标签使用次数 */
  long countByTagName(String tagName);

  /** 增加标签使用次数 */
  @Query("UPDATE NoteTag t SET t.usageCount = t.usageCount + 1 WHERE t.tagName = :tagName")
  void incrementUsageCount(@Param("tagName") String tagName);

  /** 减少标签使用次数 */
  @Query("UPDATE NoteTag t SET t.usageCount = t.usageCount - 1 WHERE t.tagName = :tagName")
  void decrementUsageCount(@Param("tagName") String tagName);

  /** 查找最常用的标签 */
  @Query("SELECT t FROM NoteTag t ORDER BY t.usageCount DESC")
  List<NoteTag> findMostUsedTags();

  /** 根据颜色查找标签 */
  List<NoteTag> findByColor(String color);

  /** 检查标签是否存在 */
  boolean existsByTagName(String tagName);

  /** 删除未使用的标签 */
  void deleteByUsageCountAndIsSystemFalse(Integer usageCount);

  /** 根据标签名删除标签 */
  void deleteByTagName(String tagName);
}
