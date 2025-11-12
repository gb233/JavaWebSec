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
 * 用户指引偏好实体
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Entity
@Table(name = "user_guide_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGuidePreference {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "has_completed_initial_guide", nullable = false)
  @Builder.Default
  private Boolean hasCompletedInitialGuide = false;

  @Column(name = "guide_version", length = 20)
  private String guideVersion; // 指引版本，用于检测更新

  @Column(name = "last_guide_shown_at")
  private LocalDateTime lastGuideShownAt;

  @Column(name = "auto_show_guide", nullable = false)
  @Builder.Default
  private Boolean autoShowGuide = true; // 是否自动显示指引

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
