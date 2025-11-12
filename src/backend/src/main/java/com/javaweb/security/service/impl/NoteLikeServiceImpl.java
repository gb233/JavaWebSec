package com.javaweb.security.service.impl;

import com.javaweb.security.entity.NoteLike;
import com.javaweb.security.repository.NoteLikeRepository;
import com.javaweb.security.service.NoteLikeService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 笔记点赞服务实现类 */
@Service
@Transactional
public class NoteLikeServiceImpl implements NoteLikeService {

  @Autowired private NoteLikeRepository likeRepository;

  @Override
  public NoteLike likeNote(Long userId, Long noteId) {
    // 检查是否已点赞
    if (likeRepository.existsByUserIdAndNoteId(userId, noteId)) {
      throw new RuntimeException("已经点赞过了");
    }

    NoteLike like = new NoteLike(userId, noteId);
    return likeRepository.save(like);
  }

  @Override
  public void unlikeNote(Long userId, Long noteId) {
    likeRepository.deleteByUserIdAndNoteId(userId, noteId);
  }

  @Override
  public boolean isLiked(Long userId, Long noteId) {
    return likeRepository.existsByUserIdAndNoteId(userId, noteId);
  }

  @Override
  public List<NoteLike> getNoteLikes(Long noteId) {
    return likeRepository.findByNoteId(noteId);
  }

  @Override
  public List<NoteLike> getUserLikes(Long userId) {
    return likeRepository.findByUserId(userId);
  }

  @Override
  public long countNoteLikes(Long noteId) {
    return likeRepository.countByNoteId(noteId);
  }

  @Override
  public long countUserLikes(Long userId) {
    return likeRepository.countByUserId(userId);
  }

  @Override
  public void deleteNoteLikes(Long noteId) {
    likeRepository.deleteByNoteId(noteId);
  }

  @Override
  public void deleteUserLikes(Long userId) {
    likeRepository.deleteByUserId(userId);
  }

  @Override
  public List<Long> getRecentLikedNotes(Long userId, int limit) {
    LocalDateTime since = LocalDateTime.now().minusDays(30); // 最近30天
    List<Object[]> results = likeRepository.findRecentLikedNotes(since);
    return results.stream().map(result -> (Long) result[0]).limit(limit).toList();
  }

  @Override
  public List<Object[]> getPopularLikedNotes(int limit) {
    LocalDateTime since = LocalDateTime.now().minusDays(7); // 最近7天
    return likeRepository.findRecentLikedNotes(since).stream().limit(limit).toList();
  }
}
