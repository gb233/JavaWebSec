package com.javaweb.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 语言偏好DTO
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguagePreferenceDto {

  private Long id;
  private Long userId;
  private String languageCode;
  private String languageName;
  private String displayName;
  private Boolean isActive;

  /** 从实体创建DTO */
  public static LanguagePreferenceDto fromEntity(
      com.javaweb.security.entity.LanguagePreference entity) {
    return LanguagePreferenceDto.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .languageCode(entity.getLanguageCode())
        .languageName(entity.getLanguageName())
        .displayName(entity.getDisplayName())
        .isActive(entity.getIsActive())
        .build();
  }
}
