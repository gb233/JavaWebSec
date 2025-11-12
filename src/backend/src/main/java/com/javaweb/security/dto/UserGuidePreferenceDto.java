package com.javaweb.security.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户指引偏好DTO
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGuidePreferenceDto {

  private Long id;
  private Long userId;
  private Boolean hasCompletedInitialGuide;
  private String guideVersion;
  private LocalDateTime lastGuideShownAt;
  private Boolean autoShowGuide;

  /** 从实体创建DTO */
  public static UserGuidePreferenceDto fromEntity(
      com.javaweb.security.entity.UserGuidePreference entity) {
    return UserGuidePreferenceDto.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .hasCompletedInitialGuide(entity.getHasCompletedInitialGuide())
        .guideVersion(entity.getGuideVersion())
        .lastGuideShownAt(entity.getLastGuideShownAt())
        .autoShowGuide(entity.getAutoShowGuide())
        .build();
  }
}
