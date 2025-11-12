package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 笔记标签实体类 */
@Entity
@Table(name = "note_tags")
public class NoteTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "tag_name", nullable = false, unique = true, length = 50)
  private String tagName;

  @Column(name = "tag_description", columnDefinition = "TEXT")
  private String tagDescription;

  @Column(name = "usage_count")
  private Integer usageCount = 0;

  @Column(name = "color", length = 7)
  private String color = "#007bff";

  @Column(name = "is_system")
  private Boolean isSystem = false;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // 构造函数
  public NoteTag() {}

  public NoteTag(String tagName, String tagDescription, String color, Boolean isSystem) {
    this.tagName = tagName;
    this.tagDescription = tagDescription;
    this.color = color;
    this.isSystem = isSystem;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // Getter和Setter方法
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTagName() {
    return tagName;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public String getTagDescription() {
    return tagDescription;
  }

  public void setTagDescription(String tagDescription) {
    this.tagDescription = tagDescription;
  }

  public Integer getUsageCount() {
    return usageCount;
  }

  public void setUsageCount(Integer usageCount) {
    this.usageCount = usageCount;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Boolean getIsSystem() {
    return isSystem;
  }

  public void setIsSystem(Boolean isSystem) {
    this.isSystem = isSystem;
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
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
