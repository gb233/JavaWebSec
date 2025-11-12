package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 笔记协作者实体类 */
@Entity
@Table(name = "note_collaborators")
public class NoteCollaborator {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "note_id", nullable = false)
  private Long noteId;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "permission", length = 20)
  private String permission = "READ";

  @Column(name = "invited_by", nullable = false)
  private Long invitedBy;

  @Column(name = "joined_at")
  private LocalDateTime joinedAt;

  // 构造函数
  public NoteCollaborator() {}

  public NoteCollaborator(Long noteId, Long userId, String permission, Long invitedBy) {
    this.noteId = noteId;
    this.userId = userId;
    this.permission = permission;
    this.invitedBy = invitedBy;
    this.joinedAt = LocalDateTime.now();
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

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Long getInvitedBy() {
    return invitedBy;
  }

  public void setInvitedBy(Long invitedBy) {
    this.invitedBy = invitedBy;
  }

  public LocalDateTime getJoinedAt() {
    return joinedAt;
  }

  public void setJoinedAt(LocalDateTime joinedAt) {
    this.joinedAt = joinedAt;
  }

  @PrePersist
  public void prePersist() {
    this.joinedAt = LocalDateTime.now();
  }
}
