package com.javaweb.security.repository;

import com.javaweb.security.entity.NoteVersion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 笔记版本历史Repository接口 */
@Repository
public interface NoteVersionRepository extends JpaRepository<NoteVersion, Long> {

  /** 根据笔记ID查找所有版本 */
  List<NoteVersion> findByNoteId(Long noteId);

  /** 根据笔记ID查找所有版本（按版本号排序） */
  List<NoteVersion> findByNoteIdOrderByVersionNumberDesc(Long noteId);

  /** 根据笔记ID和版本号查找版本 */
  Optional<NoteVersion> findByNoteIdAndVersionNumber(Long noteId, Integer versionNumber);

  /** 根据创建者查找版本 */
  List<NoteVersion> findByCreatedBy(Long createdBy);

  /** 查找笔记的最新版本 */
  @Query("SELECT v FROM NoteVersion v WHERE v.noteId = :noteId ORDER BY v.versionNumber DESC")
  List<NoteVersion> findLatestVersionByNoteId(@Param("noteId") Long noteId);

  /** 查找笔记的指定版本范围 */
  @Query(
      "SELECT v FROM NoteVersion v WHERE v.noteId = :noteId AND v.versionNumber BETWEEN :startVersion AND :endVersion ORDER BY v.versionNumber DESC")
  List<NoteVersion> findByNoteIdAndVersionRange(
      @Param("noteId") Long noteId,
      @Param("startVersion") Integer startVersion,
      @Param("endVersion") Integer endVersion);

  /** 统计笔记版本数量 */
  long countByNoteId(Long noteId);

  /** 统计用户创建的版本数量 */
  long countByCreatedBy(Long createdBy);

  /** 查找最近创建的版本 */
  @Query("SELECT v FROM NoteVersion v ORDER BY v.createdAt DESC")
  List<NoteVersion> findRecentVersions();

  /** 查找用户最近创建的版本 */
  @Query("SELECT v FROM NoteVersion v WHERE v.createdBy = :userId ORDER BY v.createdAt DESC")
  List<NoteVersion> findRecentVersionsByUser(@Param("userId") Long userId);

  /** 根据时间范围查找版本 */
  List<NoteVersion> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  /** 删除笔记的所有版本 */
  void deleteByNoteId(Long noteId);

  /** 删除用户创建的所有版本 */
  void deleteByCreatedBy(Long createdBy);

  /** 删除指定版本号之前的版本 */
  @Query("DELETE FROM NoteVersion v WHERE v.noteId = :noteId AND v.versionNumber < :versionNumber")
  void deleteVersionsBefore(
      @Param("noteId") Long noteId, @Param("versionNumber") Integer versionNumber);

  /** 查找笔记的版本历史（分页） */
  @Query("SELECT v FROM NoteVersion v WHERE v.noteId = :noteId ORDER BY v.versionNumber DESC")
  List<NoteVersion> findVersionHistory(@Param("noteId") Long noteId);

  /** 查找版本差异 */
  @Query(
      "SELECT v FROM NoteVersion v WHERE v.noteId = :noteId AND v.versionNumber BETWEEN :fromVersion AND :toVersion ORDER BY v.versionNumber")
  List<NoteVersion> findVersionDiff(
      @Param("noteId") Long noteId,
      @Param("fromVersion") Integer fromVersion,
      @Param("toVersion") Integer toVersion);

  /** 查找指定时间之前的版本 */
  @Query(
      "SELECT v FROM NoteVersion v WHERE v.noteId = :noteId AND v.createdAt < :before ORDER BY v.versionNumber DESC")
  List<NoteVersion> findVersionsBefore(
      @Param("noteId") Long noteId, @Param("before") LocalDateTime before);

  /** 查找指定时间之后的版本 */
  @Query(
      "SELECT v FROM NoteVersion v WHERE v.noteId = :noteId AND v.createdAt > :after ORDER BY v.versionNumber ASC")
  List<NoteVersion> findVersionsAfter(
      @Param("noteId") Long noteId, @Param("after") LocalDateTime after);
}
