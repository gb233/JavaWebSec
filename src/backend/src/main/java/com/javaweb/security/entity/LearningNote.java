package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 学习笔记实体类 */
@Entity
@Table(name = "learning_notes")
public class LearningNote {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "title", nullable = false, length = 200)
  private String title;

  @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
  private String content;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Column(name = "note_type", length = 50)
  private String noteType = "PERSONAL";

  @Column(name = "vulnerability_code", length = 10)
  private String vulnerabilityCode;

  @Column(name = "tags", columnDefinition = "JSON")
  private String tags;

  @Column(name = "is_public")
  private Boolean isPublic = false;

  @Column(name = "is_pinned")
  private Boolean isPinned = false;

  @Column(name = "view_count")
  private Integer viewCount = 0;

  @Column(name = "like_count")
  private Integer likeCount = 0;

  @Column(name = "comment_count")
  private Integer commentCount = 0;

  @Column(name = "share_count")
  private Integer shareCount = 0;

  @Column(name = "word_count")
  private Integer wordCount = 0;

  @Column(name = "reading_time")
  private Integer readingTime = 0;

  @Column(name = "last_modified_at")
  private LocalDateTime lastModifiedAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 构造函数
  public LearningNote() {}

  public LearningNote(
      Long userId,
      String title,
      String content,
      String summary,
      String noteType,
      String vulnerabilityCode) {
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.summary = summary;
    this.noteType = noteType;
    this.vulnerabilityCode = vulnerabilityCode;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.lastModifiedAt = LocalDateTime.now();
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

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getNoteType() {
    return noteType;
  }

  public void setNoteType(String noteType) {
    this.noteType = noteType;
  }

  public String getVulnerabilityCode() {
    return vulnerabilityCode;
  }

  public void setVulnerabilityCode(String vulnerabilityCode) {
    this.vulnerabilityCode = vulnerabilityCode;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public Boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  public Boolean getIsPinned() {
    return isPinned;
  }

  public void setIsPinned(Boolean isPinned) {
    this.isPinned = isPinned;
  }

  public Integer getViewCount() {
    return viewCount;
  }

  public void setViewCount(Integer viewCount) {
    this.viewCount = viewCount;
  }

  public Integer getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(Integer likeCount) {
    this.likeCount = likeCount;
  }

  public Integer getCommentCount() {
    return commentCount;
  }

  public void setCommentCount(Integer commentCount) {
    this.commentCount = commentCount;
  }

  public Integer getShareCount() {
    return shareCount;
  }

  public void setShareCount(Integer shareCount) {
    this.shareCount = shareCount;
  }

  public Integer getWordCount() {
    return wordCount;
  }

  public void setWordCount(Integer wordCount) {
    this.wordCount = wordCount;
  }

  public Integer getReadingTime() {
    return readingTime;
  }

  public void setReadingTime(Integer readingTime) {
    this.readingTime = readingTime;
  }

  public LocalDateTime getLastModifiedAt() {
    return lastModifiedAt;
  }

  public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
    this.lastModifiedAt = lastModifiedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
    this.lastModifiedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
    this.lastModifiedAt = LocalDateTime.now();
  }
}
