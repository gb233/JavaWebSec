package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;

/** 徽章分类实体类 */
@Entity
@Table(name = "badge_categories")
public class BadgeCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "category_code", unique = true, nullable = false)
  private String categoryCode;

  @Column(name = "category_name", nullable = false)
  private String categoryName;

  @Column(name = "category_description", columnDefinition = "TEXT")
  private String categoryDescription;

  @Column(name = "category_icon")
  private String categoryIcon;

  @Column(name = "sort_order")
  private Integer sortOrder = 0;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // 构造函数
  public BadgeCategory() {}

  public BadgeCategory(
      String categoryCode,
      String categoryName,
      String categoryDescription,
      String categoryIcon,
      Integer sortOrder) {
    this.categoryCode = categoryCode;
    this.categoryName = categoryName;
    this.categoryDescription = categoryDescription;
    this.categoryIcon = categoryIcon;
    this.sortOrder = sortOrder;
    this.createdAt = LocalDateTime.now();
  }

  // Getter和Setter方法
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCategoryCode() {
    return categoryCode;
  }

  public void setCategoryCode(String categoryCode) {
    this.categoryCode = categoryCode;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String getCategoryDescription() {
    return categoryDescription;
  }

  public void setCategoryDescription(String categoryDescription) {
    this.categoryDescription = categoryDescription;
  }

  public String getCategoryIcon() {
    return categoryIcon;
  }

  public void setCategoryIcon(String categoryIcon) {
    this.categoryIcon = categoryIcon;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
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
}
