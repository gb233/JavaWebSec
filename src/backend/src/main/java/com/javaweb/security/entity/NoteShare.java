package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 笔记分享实体类 */
@Entity
@Table(name = "note_shares")
public class NoteShare {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "note_id", nullable = false)
  private Long noteId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "share_type", length = 20)
  private String shareType = "LINK";

  @Column(name = "share_token", length = 100, unique = true)
  private String shareToken;

  @Column(name = "access_count")
  private Integer accessCount = 0;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // 构造函数
  public NoteShare() {}

  public NoteShare(Long noteId, Long userId, String shareType, String shareToken) {
    this.noteId = noteId;
    this.userId = userId;
    this.shareType = shareType;
    this.shareToken = shareToken;
    this.createdAt = LocalDateTime.now();
  }

  public NoteShare(
      Long noteId, Long userId, String shareType, String shareToken, LocalDateTime expiresAt) {
    this.noteId = noteId;
    this.userId = userId;
    this.shareType = shareType;
    this.shareToken = shareToken;
    this.expiresAt = expiresAt;
    this.createdAt = LocalDateTime.now();
  }

  // Getter和Setter方法
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getNoteId() {
    return noteId;
  }

  public void setNoteId(Long noteId) {
    this.noteId = noteId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getShareType() {
    return shareType;
  }

  public void setShareType(String shareType) {
    this.shareType = shareType;
  }

  public String getShareToken() {
    return shareToken;
  }

  public void setShareToken(String shareToken) {
    this.shareToken = shareToken;
  }

  public Integer getAccessCount() {
    return accessCount;
  }

  public void setAccessCount(Integer accessCount) {
    this.accessCount = accessCount;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
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
