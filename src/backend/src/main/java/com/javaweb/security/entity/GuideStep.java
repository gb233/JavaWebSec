package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 指引步骤实体
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Entity
@Table(name = "guide_steps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideStep {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "step_key", nullable = false, length = 50)
  private String stepKey; // 步骤唯一标识

  @Column(name = "title_zh", nullable = false, length = 200)
  private String titleZh; // 中文标题

  @Column(name = "title_en", nullable = false, length = 200)
  private String titleEn; // 英文标题

  @Column(name = "description_zh", columnDefinition = "TEXT")
  private String descriptionZh; // 中文描述

  @Column(name = "description_en", columnDefinition = "TEXT")
  private String descriptionEn; // 英文描述

  @Column(name = "target_element", length = 100)
  private String targetElement; // 目标元素选择器

  @Column(name = "position", length = 20)
  private String position; // 提示框位置：top, bottom, left, right

  @Column(name = "order_index", nullable = false)
  private Integer orderIndex; // 显示顺序

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @Column(name = "guide_version", length = 20)
  private String guideVersion; // 指引版本

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // 根据语言获取标题
  public String getTitle(String language) {
    if (language == null || language.isEmpty()) {
      return titleZh; // 默认返回中文
    }
    // 支持多种中文语言代码格式
    if ("zh".equals(language) || "zh-CN".equals(language) || "zh_CN".equals(language)) {
      return titleZh;
    }
    return titleEn;
  }

  // 根据语言获取描述
  public String getDescription(String language) {
    if (language == null || language.isEmpty()) {
      return descriptionZh; // 默认返回中文
    }
    // 支持多种中文语言代码格式
    if ("zh".equals(language) || "zh-CN".equals(language) || "zh_CN".equals(language)) {
      return descriptionZh;
    }
    return descriptionEn;
  }
}
