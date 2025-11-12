package com.javaweb.security.service;

import com.javaweb.security.entity.NoteLike;
import java.util.List;

/** 笔记点赞服务接口 */
public interface NoteLikeService {

  /** 点赞笔记 */
  NoteLike likeNote(Long userId, Long noteId);

  /** 取消点赞 */
  void unlikeNote(Long userId, Long noteId);

  /** 检查是否已点赞 */
  boolean isLiked(Long userId, Long noteId);

  /** 获取笔记点赞列表 */
  List<NoteLike> getNoteLikes(Long noteId);

  /** 获取用户点赞列表 */
  List<NoteLike> getUserLikes(Long userId);

  /** 统计笔记点赞数 */
  long countNoteLikes(Long noteId);

  /** 统计用户点赞数 */
  long countUserLikes(Long userId);

  /** 删除笔记所有点赞 */
  void deleteNoteLikes(Long noteId);

  /** 删除用户所有点赞 */
  void deleteUserLikes(Long userId);

  /** 获取最近点赞的笔记 */
  List<Long> getRecentLikedNotes(Long userId, int limit);

  /** 获取热门点赞笔记 */
  List<Object[]> getPopularLikedNotes(int limit);
}
