package com.javaweb.security.service;

import com.javaweb.security.entity.NoteComment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** 笔记评论服务接口 */
public interface NoteCommentService {

  /** 创建评论 */
  NoteComment createComment(NoteComment comment);

  /** 更新评论 */
  NoteComment updateComment(Long commentId, NoteComment comment);

  /** 删除评论 */
  void deleteComment(Long commentId);

  /** 软删除评论 */
  void softDeleteComment(Long commentId);

  /** 恢复评论 */
  void restoreComment(Long commentId);

  /** 根据ID获取评论 */
  NoteComment getCommentById(Long commentId);

  /** 获取笔记评论 */
  List<NoteComment> getNoteComments(Long noteId);

  /** 分页获取笔记评论 */
  Page<NoteComment> getNoteComments(Long noteId, Pageable pageable);

  /** 获取笔记顶级评论 */
  List<NoteComment> getNoteTopComments(Long noteId);

  /** 分页获取笔记顶级评论 */
  Page<NoteComment> getNoteTopComments(Long noteId, Pageable pageable);

  /** 获取子评论 */
  List<NoteComment> getChildComments(Long parentId);

  /** 获取用户评论 */
  List<NoteComment> getUserComments(Long userId);

  /** 分页获取用户评论 */
  Page<NoteComment> getUserComments(Long userId, Pageable pageable);

  /** 获取未删除评论 */
  List<NoteComment> getActiveNoteComments(Long noteId);

  /** 获取未删除顶级评论 */
  List<NoteComment> getActiveNoteTopComments(Long noteId);

  /** 统计笔记评论数 */
  long countNoteComments(Long noteId);

  /** 统计笔记活跃评论数 */
  long countActiveNoteComments(Long noteId);

  /** 统计用户评论数 */
  long countUserComments(Long userId);

  /** 统计用户活跃评论数 */
  long countActiveUserComments(Long userId);

  /** 获取最近评论 */
  List<NoteComment> getRecentComments(Pageable pageable);

  /** 获取用户最近评论 */
  List<NoteComment> getUserRecentComments(Long userId, Pageable pageable);

  /** 删除笔记所有评论 */
  void deleteNoteComments(Long noteId);

  /** 删除用户所有评论 */
  void deleteUserComments(Long userId);

  /** 删除父评论的所有子评论 */
  void deleteChildComments(Long parentId);

  /** 增加评论点赞数 */
  void incrementCommentLikes(Long commentId);

  /** 减少评论点赞数 */
  void decrementCommentLikes(Long commentId);
}
