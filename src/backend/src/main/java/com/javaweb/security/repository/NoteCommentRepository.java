package com.javaweb.security.repository;

import com.javaweb.security.entity.NoteComment;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 笔记评论Repository接口 */
@Repository
public interface NoteCommentRepository extends JpaRepository<NoteComment, Long> {

  /** 根据笔记ID查找评论 */
  List<NoteComment> findByNoteId(Long noteId);

  /** 根据笔记ID分页查找评论 */
  Page<NoteComment> findByNoteId(Long noteId, Pageable pageable);

  /** 根据笔记ID查找顶级评论（无父评论） */
  List<NoteComment> findByNoteIdAndParentIdIsNull(Long noteId);

  /** 根据笔记ID查找顶级评论（分页） */
  Page<NoteComment> findByNoteIdAndParentIdIsNull(Long noteId, Pageable pageable);

  /** 根据父评论ID查找子评论 */
  List<NoteComment> findByParentId(Long parentId);

  /** 根据用户ID查找评论 */
  List<NoteComment> findByUserId(Long userId);

  /** 根据用户ID分页查找评论 */
  Page<NoteComment> findByUserId(Long userId, Pageable pageable);

  /** 查找未删除的评论 */
  List<NoteComment> findByNoteIdAndIsDeletedFalse(Long noteId);

  /** 查找未删除的顶级评论 */
  List<NoteComment> findByNoteIdAndParentIdIsNullAndIsDeletedFalse(Long noteId);

  /** 统计笔记评论数量 */
  long countByNoteId(Long noteId);

  /** 统计笔记未删除评论数量 */
  long countByNoteIdAndIsDeletedFalse(Long noteId);

  /** 统计用户评论数量 */
  long countByUserId(Long userId);

  /** 统计用户未删除评论数量 */
  long countByUserIdAndIsDeletedFalse(Long userId);

  /** 根据时间范围查找评论 */
  List<NoteComment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  /** 查找最近评论 */
  @Query("SELECT c FROM NoteComment c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
  List<NoteComment> findRecentComments(Pageable pageable);

  /** 查找用户最近评论 */
  @Query(
      "SELECT c FROM NoteComment c WHERE c.userId = :userId AND c.isDeleted = false ORDER BY c.createdAt DESC")
  List<NoteComment> findRecentCommentsByUserId(@Param("userId") Long userId, Pageable pageable);

  /** 删除笔记的所有评论 */
  void deleteByNoteId(Long noteId);

  /** 删除用户的所有评论 */
  void deleteByUserId(Long userId);

  /** 删除父评论的所有子评论 */
  void deleteByParentId(Long parentId);

  /** 软删除评论 */
  @Query("UPDATE NoteComment c SET c.isDeleted = true WHERE c.id = :commentId")
  void softDeleteComment(@Param("commentId") Long commentId);

  /** 恢复软删除的评论 */
  @Query("UPDATE NoteComment c SET c.isDeleted = false WHERE c.id = :commentId")
  void restoreComment(@Param("commentId") Long commentId);

  /** 增加评论点赞数 */
  @Query("UPDATE NoteComment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
  void incrementLikeCount(@Param("commentId") Long commentId);

  /** 减少评论点赞数 */
  @Query("UPDATE NoteComment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :commentId")
  void decrementLikeCount(@Param("commentId") Long commentId);
}
