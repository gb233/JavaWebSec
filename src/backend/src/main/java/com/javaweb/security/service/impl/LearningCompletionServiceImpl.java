package com.javaweb.security.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.entity.PageVisit;
import com.javaweb.security.entity.UserInteraction;
import com.javaweb.security.entity.UserVulnerabilityProgress;
import com.javaweb.security.enums.VulnerabilityStatus;
import com.javaweb.security.repository.PageVisitRepository;
import com.javaweb.security.repository.UserInteractionRepository;
import com.javaweb.security.repository.UserVulnerabilityProgressRepository;
import com.javaweb.security.service.LearningCompletionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学习完成判断服务实现
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LearningCompletionServiceImpl implements LearningCompletionService {

  private final UserVulnerabilityProgressRepository userVulnerabilityProgressRepository;
  private final PageVisitRepository pageVisitRepository;
  private final UserInteractionRepository userInteractionRepository;
  private final ObjectMapper objectMapper;

  @Override
  public boolean isVulnerabilityLearningCompleted(Long userId, String vulnerabilityCode) {
    log.info("判断用户漏洞学习是否完成: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);

    Optional<UserVulnerabilityProgress> progressOpt =
        userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
            userId, vulnerabilityCode);
    if (progressOpt.isEmpty()) {
      return false;
    }

    UserVulnerabilityProgress progress = progressOpt.get();
    return progress.getLearningCompleted();
  }

  @Override
  @Transactional
  public void recordPageVisit(
      Long userId, String vulnerabilityCode, String pageType, Integer duration) {
    log.info(
        "记录用户页面访问: userId={}, vulnerabilityCode={}, pageType={}, duration={}",
        userId,
        vulnerabilityCode,
        pageType,
        duration);

    // 查找或创建页面访问记录
    List<PageVisit> visits =
        pageVisitRepository.findByUserIdAndVulnerabilityCodeAndPageType(
            userId, vulnerabilityCode, pageType);

    PageVisit pageVisit;
    if (!visits.isEmpty()) {
      pageVisit = visits.get(0); // 取第一个记录
      pageVisit.setDuration(pageVisit.getDuration() + duration);
    } else {
      pageVisit = new PageVisit();
      pageVisit.setUserId(userId);
      pageVisit.setVulnerabilityCode(vulnerabilityCode);
      pageVisit.setPageType(pageType);
      pageVisit.setVisitTime(LocalDateTime.now());
      pageVisit.setDuration(duration);
    }

    pageVisitRepository.save(pageVisit);

    // 检查学习完成条件
    checkAndUpdateLearningCompletion(userId, vulnerabilityCode);
  }

  @Override
  @Transactional
  public void recordUserInteraction(
      Long userId, String vulnerabilityCode, String interactionType, Map<String, Object> data) {
    log.info(
        "记录用户交互: userId={}, vulnerabilityCode={}, interactionType={}",
        userId,
        vulnerabilityCode,
        interactionType);

    UserInteraction interaction = new UserInteraction();
    interaction.setUserId(userId);
    interaction.setVulnerabilityCode(vulnerabilityCode);
    interaction.setInteractionType(interactionType);
    interaction.setTimestamp(LocalDateTime.now());

    try {
      interaction.setInteractionDetail(objectMapper.writeValueAsString(data));
    } catch (JsonProcessingException e) {
      log.error("序列化交互详情失败", e);
    }

    userInteractionRepository.save(interaction);

    // 检查学习完成条件
    checkAndUpdateLearningCompletion(userId, vulnerabilityCode);
  }

  /** 检查并更新学习完成状态 */
  private void checkAndUpdateLearningCompletion(Long userId, String vulnerabilityCode) {
    Optional<UserVulnerabilityProgress> progressOpt =
        userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
            userId, vulnerabilityCode);

    if (progressOpt.isEmpty()) {
      return;
    }

    UserVulnerabilityProgress progress = progressOpt.get();
    if (progress.getLearningCompleted()) {
      return; // 已经完成，无需重复检查
    }

    // 检查学习完成条件
    LearningCompletionCriteria criteria = getLearningCompletionCriteria(vulnerabilityCode);
    boolean isCompleted = checkLearningCompletion(userId, vulnerabilityCode, criteria);

    if (isCompleted) {
      progress.setLearningCompleted(true);
      progress.setStatus(VulnerabilityStatus.COMPLETED);
      if (progress.getCompletedAt() == null) {
        progress.setCompletedAt(LocalDateTime.now());
      }
      userVulnerabilityProgressRepository.save(progress);
      log.info("用户漏洞学习完成: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);
    }
  }

  /** 获取学习完成条件 */
  private LearningCompletionCriteria getLearningCompletionCriteria(String vulnerabilityCode) {
    // 根据漏洞类型设置不同的完成条件
    switch (vulnerabilityCode) {
      case "A01":
        return new LearningCompletionCriteria(true, true, true, true, 30, 60, 80, 5, true);
      case "A02":
        return new LearningCompletionCriteria(true, true, true, true, 25, 50, 70, 4, true);
      case "A03":
        return new LearningCompletionCriteria(true, true, true, true, 35, 70, 90, 6, true);
      default:
        return new LearningCompletionCriteria(true, true, true, true, 20, 40, 60, 3, true);
    }
  }

  /** 检查学习完成条件 */
  private boolean checkLearningCompletion(
      Long userId, String vulnerabilityCode, LearningCompletionCriteria criteria) {

    // 检查页面访问
    List<PageVisit> pageVisits =
        pageVisitRepository.findByUserIdAndVulnerabilityCode(userId, vulnerabilityCode);
    boolean theoryVisited = pageVisits.stream().anyMatch(v -> "theory".equals(v.getPageType()));
    boolean knowledgeVisited =
        pageVisits.stream().anyMatch(v -> "knowledge".equals(v.getPageType()));
    boolean demoVisited = pageVisits.stream().anyMatch(v -> "demo".equals(v.getPageType()));
    boolean repairVisited = pageVisits.stream().anyMatch(v -> "repair".equals(v.getPageType()));

    if (criteria.getTheory() && !theoryVisited) return false;
    if (criteria.getKnowledge() && !knowledgeVisited) return false;
    if (criteria.getDemo() && !demoVisited) return false;
    if (criteria.getRepair() && !repairVisited) return false;

    // 检查学习时长
    int totalStudyTime = pageVisits.stream().mapToInt(PageVisit::getDuration).sum();
    if (totalStudyTime < criteria.getMinStudyTime()) return false;

    // 检查交互次数
    Long interactionCount =
        userInteractionRepository.countByUserIdAndVulnerabilityCode(userId, vulnerabilityCode);
    if (interactionCount < criteria.getClickCount()) return false;

    return true;
  }

  /** 学习完成条件 */
  public static class LearningCompletionCriteria {
    private Boolean theory;
    private Boolean knowledge;
    private Boolean demo;
    private Boolean repair;
    private Integer minStudyTime;
    private Integer minPageTime;
    private Integer scrollDepth;
    private Integer clickCount;
    private Boolean demoExecution;

    public LearningCompletionCriteria(
        Boolean theory,
        Boolean knowledge,
        Boolean demo,
        Boolean repair,
        Integer minStudyTime,
        Integer minPageTime,
        Integer scrollDepth,
        Integer clickCount,
        Boolean demoExecution) {
      this.theory = theory;
      this.knowledge = knowledge;
      this.demo = demo;
      this.repair = repair;
      this.minStudyTime = minStudyTime;
      this.minPageTime = minPageTime;
      this.scrollDepth = scrollDepth;
      this.clickCount = clickCount;
      this.demoExecution = demoExecution;
    }

    // Getters
    public Boolean getTheory() {
      return theory;
    }

    public Boolean getKnowledge() {
      return knowledge;
    }

    public Boolean getDemo() {
      return demo;
    }

    public Boolean getRepair() {
      return repair;
    }

    public Integer getMinStudyTime() {
      return minStudyTime;
    }

    public Integer getMinPageTime() {
      return minPageTime;
    }

    public Integer getScrollDepth() {
      return scrollDepth;
    }

    public Integer getClickCount() {
      return clickCount;
    }

    public Boolean getDemoExecution() {
      return demoExecution;
    }
  }
}
