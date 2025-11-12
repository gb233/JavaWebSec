package com.javaweb.security.repository;

import com.javaweb.security.entity.NoteShare;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 笔记分享Repository接口 */
@Repository
public interface NoteShareRepository extends JpaRepository<NoteShare, Long> {

  /** 根据分享令牌查找分享记录 */
  Optional<NoteShare> findByShareToken(String shareToken);

  /** 根据笔记ID查找分享记录 */
  List<NoteShare> findByNoteId(Long noteId);

  /** 根据用户ID查找分享记录 */
  List<NoteShare> findByUserId(Long userId);

  /** 根据分享类型查找分享记录 */
  List<NoteShare> findByShareType(String shareType);

  /** 查找活跃的分享记录 */
  List<NoteShare> findByIsActiveTrue();

  /** 查找过期的分享记录 */
  @Query("SELECT s FROM NoteShare s WHERE s.expiresAt < :now")
  List<NoteShare> findExpiredShares(@Param("now") LocalDateTime now);

  /** 查找未过期的分享记录 */
  @Query("SELECT s FROM NoteShare s WHERE s.expiresAt IS NULL OR s.expiresAt > :now")
  List<NoteShare> findValidShares(@Param("now") LocalDateTime now);

  /** 根据笔记ID和用户ID查找分享记录 */
  List<NoteShare> findByNoteIdAndUserId(Long noteId, Long userId);

  /** 根据笔记ID查找活跃分享记录 */
  List<NoteShare> findByNoteIdAndIsActiveTrue(Long noteId);

  /** 统计笔记分享次数 */
  long countByNoteId(Long noteId);

  /** 统计用户分享次数 */
  long countByUserId(Long userId);

  /** 统计活跃分享数量 */
  long countByIsActiveTrue();

  /** 增加分享访问次数 */
  @Query("UPDATE NoteShare s SET s.accessCount = s.accessCount + 1 WHERE s.id = :shareId")
  void incrementAccessCount(@Param("shareId") Long shareId);

  /** 停用分享 */
  @Query("UPDATE NoteShare s SET s.isActive = false WHERE s.id = :shareId")
  void deactivateShare(@Param("shareId") Long shareId);

  /** 激活分享 */
  @Query("UPDATE NoteShare s SET s.isActive = true WHERE s.id = :shareId")
  void activateShare(@Param("shareId") Long shareId);

  /** 删除笔记的所有分享 */
  void deleteByNoteId(Long noteId);

  /** 删除用户的所有分享 */
  void deleteByUserId(Long userId);

  /** 删除过期的分享 */
  @Query("DELETE FROM NoteShare s WHERE s.expiresAt < :now")
  void deleteExpiredShares(@Param("now") LocalDateTime now);

  /** 查找最近分享的笔记 */
  @Query(
      "SELECT s.noteId, COUNT(s) as shareCount FROM NoteShare s WHERE s.createdAt >= :since GROUP BY s.noteId ORDER BY shareCount DESC")
  List<Object[]> findRecentSharedNotes(@Param("since") LocalDateTime since);

  /** 查找用户最近分享的笔记 */
  @Query("SELECT s.noteId FROM NoteShare s WHERE s.userId = :userId ORDER BY s.createdAt DESC")
  List<Long> findRecentSharedNoteIdsByUserId(@Param("userId") Long userId);

  /** 检查分享令牌是否存在 */
  boolean existsByShareToken(String shareToken);

  /** 根据时间范围查找分享记录 */
  List<NoteShare> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
