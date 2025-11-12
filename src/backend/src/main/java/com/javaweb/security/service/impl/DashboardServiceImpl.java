package com.javaweb.security.service.impl;

import com.javaweb.security.dto.dashboard.DashboardOverviewDto;
import com.javaweb.security.entity.TestRecord;
import com.javaweb.security.entity.UserProfile;
import com.javaweb.security.entity.VulnerabilityContent;
import com.javaweb.security.repository.TestRecordRepository;
import com.javaweb.security.repository.UserProfileRepository;
import com.javaweb.security.repository.VulnerabilityContentRepository;
import com.javaweb.security.service.DashboardService;
import com.javaweb.security.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

  private final UserProfileRepository userProfileRepository;
  private final TestRecordRepository testRecordRepository;
  private final VulnerabilityContentRepository vulnerabilityContentRepository;
  private final UserService userService;

  @Override
  public DashboardOverviewDto getOverview(Long userId) {
    if (userId == null) {
      log.warn("getOverview接收到的userId为null，返回空数据");
      return DashboardOverviewDto.builder()
          .userStats(DashboardOverviewDto.UserStatsDto.builder().build())
          .recentActivities(List.of())
          .quickLinks(buildQuickLinks())
          .highlights(buildHighlights())
          .build();
    }
    DashboardOverviewDto.UserStatsDto userStats = buildUserStats(userId);
    List<DashboardOverviewDto.ActivityItemDto> activities = buildRecentActivities(userId);
    List<DashboardOverviewDto.QuickLinkDto> quickLinks = buildQuickLinks();
    List<DashboardOverviewDto.HighlightCardDto> highlights = buildHighlights();

    return DashboardOverviewDto.builder()
        .userStats(userStats)
        .recentActivities(activities)
        .quickLinks(quickLinks)
        .highlights(highlights)
        .build();
  }

  private DashboardOverviewDto.UserStatsDto buildUserStats(Long userId) {
    if (userId == null) {
      log.warn("buildUserStats接收到的userId为null，返回空统计数据");
      return DashboardOverviewDto.UserStatsDto.builder().build();
    }
    // 确保用户有UserProfile，如果没有则创建
    UserProfile profile =
        userProfileRepository
            .findByUserId(userId)
            .orElseGet(
                () -> {
                  log.info("用户没有UserProfile，创建默认配置：userId={}", userId);
                  return userService.createUserProfile(userId);
                });

    return DashboardOverviewDto.UserStatsDto.builder()
        .completedVulnerabilities(
            profile.getCompletedVulnerabilities() != null
                ? profile.getCompletedVulnerabilities()
                : 0)
        .passedTests(profile.getPassedTests() != null ? profile.getPassedTests() : 0)
        .completedChallenges(
            profile.getCompletedChallenges() != null ? profile.getCompletedChallenges() : 0)
        .earnedBadges(profile.getEarnedBadges() != null ? profile.getEarnedBadges() : 0)
        .totalPoints(profile.getTotalPoints() != null ? profile.getTotalPoints() : 0)
        .totalStudyTime(profile.getTotalStudyTime() != null ? profile.getTotalStudyTime() : 0L)
        .currentStreak(profile.getCurrentStreak() != null ? profile.getCurrentStreak() : 0)
        .longestStreak(profile.getLongestStreak() != null ? profile.getLongestStreak() : 0)
        .build();
  }

  private List<DashboardOverviewDto.ActivityItemDto> buildRecentActivities(Long userId) {
    if (userId == null) {
      log.warn("buildRecentActivities接收到的userId为null，返回空列表");
      return List.of();
    }
    LocalDateTime since = LocalDateTime.now().minusDays(7);
    List<TestRecord> recentRecords = testRecordRepository.findRecentRecordsByUserId(userId, since);

    // 如果没有真实的活动记录，返回空列表，不显示示例数据
    if (CollectionUtils.isEmpty(recentRecords)) {
      return List.of();
    }

    return recentRecords.stream()
        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
        .limit(5)
        .map(
            record ->
                DashboardOverviewDto.ActivityItemDto.builder()
                    .type(record.getIsPassed() ? "test" : "learning")
                    .icon(record.getIsPassed() ? "Trophy" : "EditPen")
                    .testName(record.getTestName())
                    .isPassed(record.getIsPassed())
                    .timestamp(record.getCreatedAt())
                    .build())
        .collect(Collectors.toList());
  }

  private List<DashboardOverviewDto.QuickLinkDto> buildQuickLinks() {
    return List.of(
        DashboardOverviewDto.QuickLinkDto.builder()
            .title("漏洞知识中心")
            .description("按分类查阅核心漏洞知识")
            .route("/knowledge/center")
            .icon("Reading")
            .build(),
        DashboardOverviewDto.QuickLinkDto.builder()
            .title("知识测试")
            .description("检验学习成果并积累积分")
            .route("/test/categories")
            .icon("EditPen")
            .build(),
        DashboardOverviewDto.QuickLinkDto.builder()
            .title("互动实验")
            .description("复现高频漏洞，理解攻防细节")
            .route("/knowledge/center")
            .icon("Monitor")
            .build(),
        DashboardOverviewDto.QuickLinkDto.builder()
            .title("挑战模式")
            .description("通过实战题巩固安全技能")
            .route("/challenge/list")
            .icon("Trophy")
            .build());
  }

  private List<DashboardOverviewDto.HighlightCardDto> buildHighlights() {
    List<VulnerabilityContent> contents =
        vulnerabilityContentRepository.findTop5ByActiveTrueOrderByViewCountDesc();

    if (CollectionUtils.isEmpty(contents)) {
      return List.of(
          DashboardOverviewDto.HighlightCardDto.builder()
              .id(0L)
              .title("SQL 注入从入门到防御")
              .subtitle("经典注入攻击与参数化修复示例")
              .categoryCode("A03")
              .difficultyLevel("beginner")
              .estimatedTime(40)
              .viewCount(320)
              .build());
    }

    return contents.stream()
        .limit(4)
        .map(
            content ->
                DashboardOverviewDto.HighlightCardDto.builder()
                    .id(content.getId())
                    .title(content.getTitle())
                    .subtitle(content.getSubtitle())
                    .categoryCode(
                        content.getCategory() != null
                            ? content.getCategory().getCategoryCode()
                            : null)
                    .difficultyLevel(content.getDifficultyLevel())
                    .estimatedTime(content.getEstimatedTime())
                    .viewCount(content.getViewCount())
                    .build())
        .collect(Collectors.toList());
  }
}
