package com.javaweb.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 指引步骤DTO
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideStepDto {

  private Long id;
  private String stepKey;
  private String title;
  private String description;
  private String targetElement;
  private String position;
  private Integer orderIndex;
  private Boolean isActive;
  private String guideVersion;

  /** 从实体创建DTO */
  public static GuideStepDto fromEntity(
      com.javaweb.security.entity.GuideStep entity, String language) {
    return GuideStepDto.builder()
        .id(entity.getId())
        .stepKey(entity.getStepKey())
        .title(entity.getTitle(language))
        .description(entity.getDescription(language))
        .targetElement(entity.getTargetElement())
        .position(entity.getPosition())
        .orderIndex(entity.getOrderIndex())
        .isActive(entity.getIsActive())
        .guideVersion(entity.getGuideVersion())
        .build();
  }
}
