package com.javaweb.security.repository;

import com.javaweb.security.entity.NoteLike;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 笔记点赞Repository接口 */
@Repository
public interface NoteLikeRepository extends JpaRepository<NoteLike, Long> {

  /** 根据用户ID和笔记ID查找点赞记录 */
  Optional<NoteLike> findByUserIdAndNoteId(Long userId, Long noteId);

  /** 根据笔记ID查找所有点赞记录 */
  List<NoteLike> findByNoteId(Long noteId);

  /** 根据用户ID查找用户点赞记录 */
  List<NoteLike> findByUserId(Long userId);

  /** 检查用户是否已点赞 */
  boolean existsByUserIdAndNoteId(Long userId, Long noteId);

  /** 统计笔记点赞数量 */
  long countByNoteId(Long noteId);

  /** 统计用户点赞数量 */
  long countByUserId(Long userId);

  /** 删除用户对笔记的点赞 */
  void deleteByUserIdAndNoteId(Long userId, Long noteId);

  /** 删除笔记的所有点赞 */
  void deleteByNoteId(Long noteId);

  /** 删除用户的所有点赞 */
  void deleteByUserId(Long userId);

  /** 根据时间范围查找点赞记录 */
  List<NoteLike> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  /** 查找最近点赞的笔记 */
  @Query(
      "SELECT l.noteId, COUNT(l) as likeCount FROM NoteLike l WHERE l.createdAt >= :since GROUP BY l.noteId ORDER BY likeCount DESC")
  List<Object[]> findRecentLikedNotes(@Param("since") LocalDateTime since);

  /** 查找用户最近点赞的笔记 */
  @Query("SELECT l.noteId FROM NoteLike l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
  List<Long> findRecentLikedNoteIdsByUserId(@Param("userId") Long userId);
}
