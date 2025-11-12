package com.javaweb.security.service;

import com.javaweb.security.entity.LearningNote;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** 学习笔记服务接口 */
public interface LearningNoteService {

  /** 创建笔记 */
  LearningNote createNote(LearningNote note);

  /** 更新笔记 */
  LearningNote updateNote(Long noteId, LearningNote note);

  /** 删除笔记 */
  void deleteNote(Long noteId);

  /** 根据ID获取笔记 */
  LearningNote getNoteById(Long noteId);

  /** 根据用户ID获取笔记列表 */
  List<LearningNote> getUserNotes(Long userId);

  /** 根据用户ID分页获取笔记列表 */
  Page<LearningNote> getUserNotes(Long userId, Pageable pageable);

  /** 根据用户ID和类型获取笔记 */
  List<LearningNote> getUserNotesByType(Long userId, String noteType);

  /** 根据用户ID和漏洞代码获取笔记 */
  List<LearningNote> getUserNotesByVulnerability(Long userId, String vulnerabilityCode);

  /** 获取公开笔记 */
  List<LearningNote> getPublicNotes();

  /** 分页获取公开笔记 */
  Page<LearningNote> getPublicNotes(Pageable pageable);

  /** 根据漏洞代码获取公开笔记 */
  List<LearningNote> getPublicNotesByVulnerability(String vulnerabilityCode);

  /** 搜索笔记 */
  List<LearningNote> searchNotes(Long userId, String keyword);

  /** 全文搜索笔记 */
  List<LearningNote> fullTextSearchNotes(Long userId, String keyword);

  /** 根据标签搜索笔记 */
  List<LearningNote> searchNotesByTag(Long userId, String tag);

  /** 获取置顶笔记 */
  List<LearningNote> getPinnedNotes(Long userId);

  /** 获取最近修改的笔记 */
  List<LearningNote> getRecentModifiedNotes(Long userId);

  /** 获取最近创建的笔记 */
  List<LearningNote> getRecentCreatedNotes(Long userId);

  /** 获取热门笔记 */
  List<LearningNote> getPopularNotes(Pageable pageable);

  /** 获取最新笔记 */
  List<LearningNote> getLatestNotes(Pageable pageable);

  /** 增加查看次数 */
  void incrementViewCount(Long noteId);

  /** 置顶/取消置顶笔记 */
  void togglePin(Long noteId, Boolean isPinned);

  /** 公开/私有笔记 */
  void togglePublic(Long noteId, Boolean isPublic);

  /** 统计用户笔记数量 */
  long countUserNotes(Long userId);

  /** 统计用户公开笔记数量 */
  long countUserPublicNotes(Long userId);

  /** 获取用户笔记统计 */
  Map<String, Object> getUserNoteStats(Long userId);

  /** 获取笔记统计 */
  Map<String, Object> getNoteStats();

  /** 检查用户是否有笔记权限 */
  boolean hasNotePermission(Long userId, Long noteId);

  /** 删除用户所有笔记 */
  void deleteUserNotes(Long userId);

  /** 根据类型删除用户笔记 */
  void deleteUserNotesByType(Long userId, String noteType);
}
