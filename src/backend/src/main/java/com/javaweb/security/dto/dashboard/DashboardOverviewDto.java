package com.javaweb.security.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 仪表盘概览数据传输对象 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardOverviewDto {

  @Builder.Default private UserStatsDto userStats = UserStatsDto.builder().build();

  @Builder.Default private List<ActivityItemDto> recentActivities = Collections.emptyList();

  @Builder.Default private List<QuickLinkDto> quickLinks = Collections.emptyList();

  @Builder.Default private List<HighlightCardDto> highlights = Collections.emptyList();

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserStatsDto {
    @Builder.Default private Integer completedVulnerabilities = 0;
    @Builder.Default private Integer passedTests = 0;
    @Builder.Default private Integer completedChallenges = 0;
    @Builder.Default private Integer earnedBadges = 0;
    @Builder.Default private Integer totalPoints = 0;
    @Builder.Default private Long totalStudyTime = 0L;
    @Builder.Default private Integer currentStreak = 0;
    @Builder.Default private Integer longestStreak = 0;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ActivityItemDto {
    private String type;
    private String title;
    private String icon;
    private String timeAgo;
    private LocalDateTime timestamp;
    // 国际化支持：原始数据字段
    private String testName; // 测试名称（用于动态生成title）
    private Boolean isPassed; // 是否通过（用于动态生成title）
    private String activityKey; // 国际化key（用于默认活动）
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class QuickLinkDto {
    private String title;
    private String description;
    private String route;
    private String icon;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class HighlightCardDto {
    private Long id;
    private String title;
    private String subtitle;
    private String categoryCode;
    private String difficultyLevel;
    private Integer estimatedTime;
    private Integer viewCount;
  }
}
