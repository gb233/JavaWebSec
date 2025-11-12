package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 笔记版本历史实体类 */
@Entity
@Table(name = "note_versions")
public class NoteVersion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "note_id", nullable = false)
  private Long noteId;

  @Column(name = "version_number", nullable = false)
  private Integer versionNumber;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
  private String content;

  @Column(name = "change_summary", columnDefinition = "TEXT")
  private String changeSummary;

  @Column(name = "created_by", nullable = false)
  private Long createdBy;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // 构造函数
  public NoteVersion() {}

  public NoteVersion(
      Long noteId, Integer versionNumber, String title, String content, Long createdBy) {
    this.noteId = noteId;
    this.versionNumber = versionNumber;
    this.title = title;
    this.content = content;
    this.createdBy = createdBy;
    this.createdAt = LocalDateTime.now();
  }

  public NoteVersion(
      Long noteId,
      Integer versionNumber,
      String title,
      String content,
      String changeSummary,
      Long createdBy) {
    this.noteId = noteId;
    this.versionNumber = versionNumber;
    this.title = title;
    this.content = content;
    this.changeSummary = changeSummary;
    this.createdBy = createdBy;
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

  public Integer getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(Integer versionNumber) {
    this.versionNumber = versionNumber;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getChangeSummary() {
    return changeSummary;
  }

  public void setChangeSummary(String changeSummary) {
    this.changeSummary = changeSummary;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
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
