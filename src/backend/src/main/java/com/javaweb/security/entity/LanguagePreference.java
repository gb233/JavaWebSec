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
 * 用户语言偏好实体
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Entity
@Table(name = "language_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguagePreference {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "language_code", nullable = false, length = 10)
  private String languageCode; // zh-CN, en-US

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // 语言名称映射
  public String getLanguageName() {
    return switch (languageCode) {
      case "zh-CN" -> "中文";
      case "en-US" -> "English";
      default -> languageCode;
    };
  }

  // 语言显示名称（用于UI）
  public String getDisplayName() {
    return switch (languageCode) {
      case "zh-CN" -> "简体中文";
      case "en-US" -> "English";
      default -> languageCode;
    };
  }
}
