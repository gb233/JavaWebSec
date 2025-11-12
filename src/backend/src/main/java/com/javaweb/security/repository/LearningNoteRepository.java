package com.javaweb.security.repository;

import com.javaweb.security.entity.LearningNote;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 学习笔记Repository接口 */
@Repository
public interface LearningNoteRepository extends JpaRepository<LearningNote, Long> {

  /** 根据用户ID查找笔记 */
  List<LearningNote> findByUserId(Long userId);

  /** 根据用户ID分页查找笔记 */
  Page<LearningNote> findByUserId(Long userId, Pageable pageable);

  /** 根据用户ID和笔记类型查找笔记 */
  List<LearningNote> findByUserIdAndNoteType(Long userId, String noteType);

  /** 根据用户ID和漏洞代码查找笔记 */
  List<LearningNote> findByUserIdAndVulnerabilityCode(Long userId, String vulnerabilityCode);

  /** 查找公开笔记 */
  List<LearningNote> findByIsPublicTrue();

  /** 分页查找公开笔记 */
  Page<LearningNote> findByIsPublicTrue(Pageable pageable);

  /** 根据漏洞代码查找公开笔记 */
  List<LearningNote> findByIsPublicTrueAndVulnerabilityCode(String vulnerabilityCode);

  /** 根据标题搜索笔记 */
  @Query("SELECT n FROM LearningNote n WHERE n.userId = :userId AND n.title LIKE %:keyword%")
  List<LearningNote> findByUserIdAndTitleContaining(
      @Param("userId") Long userId, @Param("keyword") String keyword);

  /** 根据内容搜索笔记（使用LIKE进行简单搜索） */
  @Query(
      "SELECT n FROM LearningNote n WHERE n.userId = :userId AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%)")
  List<LearningNote> findByUserIdAndContentSearch(
      @Param("userId") Long userId, @Param("keyword") String keyword);

  /** 根据标签搜索笔记 */
  @Query("SELECT n FROM LearningNote n WHERE n.userId = :userId AND n.tags LIKE %:tag%")
  List<LearningNote> findByUserIdAndTag(@Param("userId") Long userId, @Param("tag") String tag);

  /** 查找置顶笔记 */
  List<LearningNote> findByUserIdAndIsPinnedTrue(Long userId);

  /** 查找最近修改的笔记 */
  List<LearningNote> findByUserIdOrderByLastModifiedAtDesc(Long userId);

  /** 查找最近创建的笔记 */
  List<LearningNote> findByUserIdOrderByCreatedAtDesc(Long userId);

  /** 根据创建时间范围查找笔记 */
  List<LearningNote> findByUserIdAndCreatedAtBetween(
      Long userId, LocalDateTime start, LocalDateTime end);

  /** 统计用户笔记数量 */
  long countByUserId(Long userId);

  /** 统计用户公开笔记数量 */
  long countByUserIdAndIsPublicTrue(Long userId);

  /** 统计公开笔记数量 */
  long countByIsPublicTrue();

  /** 统计用户按类型分组的笔记数量 */
  @Query(
      "SELECT n.noteType, COUNT(n) FROM LearningNote n WHERE n.userId = :userId GROUP BY n.noteType")
  List<Object[]> countByUserIdGroupByNoteType(@Param("userId") Long userId);

  /** 统计用户按漏洞代码分组的笔记数量 */
  @Query(
      "SELECT n.vulnerabilityCode, COUNT(n) FROM LearningNote n WHERE n.userId = :userId AND n.vulnerabilityCode IS NOT NULL GROUP BY n.vulnerabilityCode")
  List<Object[]> countByUserIdGroupByVulnerabilityCode(@Param("userId") Long userId);

  /** 查找热门笔记（按查看次数排序） */
  @Query("SELECT n FROM LearningNote n WHERE n.isPublic = true ORDER BY n.viewCount DESC")
  List<LearningNote> findPopularNotes(Pageable pageable);

  /** 查找最新笔记 */
  @Query("SELECT n FROM LearningNote n WHERE n.isPublic = true ORDER BY n.createdAt DESC")
  List<LearningNote> findLatestNotes(Pageable pageable);

  /** 根据用户ID和笔记ID查找笔记 */
  Optional<LearningNote> findByUserIdAndId(Long userId, Long id);

  /** 检查用户是否有笔记权限 */
  boolean existsByUserIdAndId(Long userId, Long id);

  /** 删除用户的所有笔记 */
  void deleteByUserId(Long userId);

  /** 根据笔记类型删除笔记 */
  void deleteByUserIdAndNoteType(Long userId, String noteType);
}
