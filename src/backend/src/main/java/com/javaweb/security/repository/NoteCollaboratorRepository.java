package com.javaweb.security.repository;

import com.javaweb.security.entity.NoteCollaborator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 笔记协作者Repository接口 */
@Repository
public interface NoteCollaboratorRepository extends JpaRepository<NoteCollaborator, Long> {

  /** 根据笔记ID查找协作者 */
  List<NoteCollaborator> findByNoteId(Long noteId);

  /** 根据用户ID查找协作笔记 */
  List<NoteCollaborator> findByUserId(Long userId);

  /** 根据笔记ID和用户ID查找协作记录 */
  Optional<NoteCollaborator> findByNoteIdAndUserId(Long noteId, Long userId);

  /** 根据权限查找协作者 */
  List<NoteCollaborator> findByPermission(String permission);

  /** 根据笔记ID和权限查找协作者 */
  List<NoteCollaborator> findByNoteIdAndPermission(Long noteId, String permission);

  /** 根据邀请者查找协作者 */
  List<NoteCollaborator> findByInvitedBy(Long invitedBy);

  /** 检查用户是否是笔记协作者 */
  boolean existsByNoteIdAndUserId(Long noteId, Long userId);

  /** 检查用户是否有指定权限 */
  boolean existsByNoteIdAndUserIdAndPermission(Long noteId, Long userId, String permission);

  /** 统计笔记协作者数量 */
  long countByNoteId(Long noteId);

  /** 统计用户协作笔记数量 */
  long countByUserId(Long userId);

  /** 统计按权限分组的协作者数量 */
  @Query(
      "SELECT c.permission, COUNT(c) FROM NoteCollaborator c WHERE c.noteId = :noteId GROUP BY c.permission")
  List<Object[]> countByNoteIdGroupByPermission(@Param("noteId") Long noteId);

  /** 查找用户有权限的笔记 */
  @Query("SELECT c.noteId FROM NoteCollaborator c WHERE c.userId = :userId")
  List<Long> findNoteIdsByUserId(@Param("userId") Long userId);

  /** 查找用户有指定权限的笔记 */
  @Query(
      "SELECT c.noteId FROM NoteCollaborator c WHERE c.userId = :userId AND c.permission = :permission")
  List<Long> findNoteIdsByUserIdAndPermission(
      @Param("userId") Long userId, @Param("permission") String permission);

  /** 查找笔记的协作者（按权限排序） */
  @Query(
      "SELECT c FROM NoteCollaborator c WHERE c.noteId = :noteId ORDER BY CASE c.permission WHEN 'ADMIN' THEN 1 WHEN 'WRITE' THEN 2 WHEN 'READ' THEN 3 END")
  List<NoteCollaborator> findByNoteIdOrderByPermission(@Param("noteId") Long noteId);

  /** 查找最近加入的协作者 */
  @Query("SELECT c FROM NoteCollaborator c ORDER BY c.joinedAt DESC")
  List<NoteCollaborator> findRecentCollaborators();

  /** 查找用户最近加入的协作 */
  @Query("SELECT c FROM NoteCollaborator c WHERE c.userId = :userId ORDER BY c.joinedAt DESC")
  List<NoteCollaborator> findRecentCollaborationsByUser(@Param("userId") Long userId);

  /** 根据时间范围查找协作记录 */
  List<NoteCollaborator> findByJoinedAtBetween(LocalDateTime start, LocalDateTime end);

  /** 删除笔记的所有协作者 */
  void deleteByNoteId(Long noteId);

  /** 删除用户的所有协作 */
  void deleteByUserId(Long userId);

  /** 删除用户对指定笔记的协作 */
  void deleteByNoteIdAndUserId(Long noteId, Long userId);

  /** 更新协作者权限 */
  @Query(
      "UPDATE NoteCollaborator c SET c.permission = :permission WHERE c.noteId = :noteId AND c.userId = :userId")
  void updatePermission(
      @Param("noteId") Long noteId,
      @Param("userId") Long userId,
      @Param("permission") String permission);

  /** 查找笔记的管理员 */
  @Query("SELECT c FROM NoteCollaborator c WHERE c.noteId = :noteId AND c.permission = 'ADMIN'")
  List<NoteCollaborator> findAdminsByNoteId(@Param("noteId") Long noteId);

  /** 查找笔记的写作者 */
  @Query("SELECT c FROM NoteCollaborator c WHERE c.noteId = :noteId AND c.permission = 'WRITE'")
  List<NoteCollaborator> findWritersByNoteId(@Param("noteId") Long noteId);

  /** 查找笔记的读者 */
  @Query("SELECT c FROM NoteCollaborator c WHERE c.noteId = :noteId AND c.permission = 'READ'")
  List<NoteCollaborator> findReadersByNoteId(@Param("noteId") Long noteId);
}
