package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 笔记点赞实体类 */
@Entity
@Table(name = "note_likes")
public class NoteLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "note_id", nullable = false)
  private Long noteId;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // 构造函数
  public NoteLike() {}

  public NoteLike(Long userId, Long noteId) {
    this.userId = userId;
    this.noteId = noteId;
    this.createdAt = LocalDateTime.now();
  }

  // Getter和Setter方法
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getNoteId() {
    return noteId;
  }

  public void setNoteId(Long noteId) {
    this.noteId = noteId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
