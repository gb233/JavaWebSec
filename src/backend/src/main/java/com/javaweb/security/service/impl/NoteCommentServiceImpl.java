package com.javaweb.security.service.impl;

import com.javaweb.security.entity.NoteComment;
import com.javaweb.security.repository.NoteCommentRepository;
import com.javaweb.security.service.NoteCommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 笔记评论服务实现类 */
@Service
@Transactional
public class NoteCommentServiceImpl implements NoteCommentService {

  @Autowired private NoteCommentRepository commentRepository;

  @Override
  public NoteComment createComment(NoteComment comment) {
    return commentRepository.save(comment);
  }

  @Override
  public NoteComment updateComment(Long commentId, NoteComment comment) {
    NoteComment existingComment =
        commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("评论不存在"));

    existingComment.setContent(comment.getContent());

    return commentRepository.save(existingComment);
  }

  @Override
  public void deleteComment(Long commentId) {
    commentRepository.deleteById(commentId);
  }

  @Override
  public void softDeleteComment(Long commentId) {
    commentRepository.softDeleteComment(commentId);
  }

  @Override
  public void restoreComment(Long commentId) {
    commentRepository.restoreComment(commentId);
  }

  @Override
  public NoteComment getCommentById(Long commentId) {
    return commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("评论不存在"));
  }

  @Override
  public List<NoteComment> getNoteComments(Long noteId) {
    return commentRepository.findByNoteId(noteId);
  }

  @Override
  public Page<NoteComment> getNoteComments(Long noteId, Pageable pageable) {
    return commentRepository.findByNoteId(noteId, pageable);
  }

  @Override
  public List<NoteComment> getNoteTopComments(Long noteId) {
    return commentRepository.findByNoteIdAndParentIdIsNull(noteId);
  }

  @Override
  public Page<NoteComment> getNoteTopComments(Long noteId, Pageable pageable) {
    return commentRepository.findByNoteIdAndParentIdIsNull(noteId, pageable);
  }

  @Override
  public List<NoteComment> getChildComments(Long parentId) {
    return commentRepository.findByParentId(parentId);
  }

  @Override
  public List<NoteComment> getUserComments(Long userId) {
    return commentRepository.findByUserId(userId);
  }

  @Override
  public Page<NoteComment> getUserComments(Long userId, Pageable pageable) {
    return commentRepository.findByUserId(userId, pageable);
  }

  @Override
  public List<NoteComment> getActiveNoteComments(Long noteId) {
    return commentRepository.findByNoteIdAndIsDeletedFalse(noteId);
  }

  @Override
  public List<NoteComment> getActiveNoteTopComments(Long noteId) {
    return commentRepository.findByNoteIdAndParentIdIsNullAndIsDeletedFalse(noteId);
  }

  @Override
  public long countNoteComments(Long noteId) {
    return commentRepository.countByNoteId(noteId);
  }

  @Override
  public long countActiveNoteComments(Long noteId) {
    return commentRepository.countByNoteIdAndIsDeletedFalse(noteId);
  }

  @Override
  public long countUserComments(Long userId) {
    return commentRepository.countByUserId(userId);
  }

  @Override
  public long countActiveUserComments(Long userId) {
    return commentRepository.countByUserIdAndIsDeletedFalse(userId);
  }

  @Override
  public List<NoteComment> getRecentComments(Pageable pageable) {
    return commentRepository.findRecentComments(pageable);
  }

  @Override
  public List<NoteComment> getUserRecentComments(Long userId, Pageable pageable) {
    return commentRepository.findRecentCommentsByUserId(userId, pageable);
  }

  @Override
  public void deleteNoteComments(Long noteId) {
    commentRepository.deleteByNoteId(noteId);
  }

  @Override
  public void deleteUserComments(Long userId) {
    commentRepository.deleteByUserId(userId);
  }

  @Override
  public void deleteChildComments(Long parentId) {
    commentRepository.deleteByParentId(parentId);
  }

  @Override
  public void incrementCommentLikes(Long commentId) {
    commentRepository.incrementLikeCount(commentId);
  }

  @Override
  public void decrementCommentLikes(Long commentId) {
    commentRepository.decrementLikeCount(commentId);
  }
}
