package com.javaweb.security.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.entity.ChallengeProgress;
import com.javaweb.security.entity.ChallengeScenario;
import com.javaweb.security.repository.ChallengeProgressRepository;
import com.javaweb.security.repository.ChallengeScenarioRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 挑战场景服务类
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChallengeService {

  private final ChallengeScenarioRepository scenarioRepository;
  private final ChallengeProgressRepository progressRepository;

  // 注入挑战验证服务
  private final ChallengeValidationService validationService;

  // 注入统计更新服务和活动服务
  private final UserStatsUpdateService userStatsUpdateService;
  private final UserActivityService userActivityService;

  private final ObjectMapper objectMapper;

  /** 获取挑战场景列表 */
  public List<ChallengeScenario> getScenarios(String difficultyLevel) {
    if (difficultyLevel != null && !difficultyLevel.isEmpty()) {
      return scenarioRepository.findByDifficultyLevelAndIsActiveTrue(difficultyLevel);
    }
    return scenarioRepository.findByIsActiveTrue();
  }

  /** 获取挑战场景详情 */
  public ChallengeScenario getScenario(Long id) {
    return scenarioRepository.findById(id).orElseThrow(() -> new RuntimeException("挑战场景不存在"));
  }

  /** 开始挑战 */
  public ChallengeProgress startChallenge(Long userId, Long scenarioId) {
    ChallengeScenario scenario = getScenario(scenarioId);

    // 检查是否已有进行中的挑战
    Optional<ChallengeProgress> existingProgress =
        progressRepository.findByUserIdAndScenarioId(userId, scenarioId);

    if (existingProgress.isPresent()) {
      return existingProgress.get();
    }

    // 创建新的挑战进度
    ChallengeProgress progress = new ChallengeProgress();
    progress.setUserId(userId);
    progress.setScenarioId(scenarioId);
    progress.setCurrentStep(0);
    progress.setProgressPercentage(BigDecimal.ZERO);
    progress.setIsCompleted(false);
    progress.setStartedAt(LocalDateTime.now());

    return progressRepository.save(progress);
  }

  /** 执行挑战步骤 */
  public ChallengeResult executeStep(
      Long userId, Long scenarioId, String step, Map<String, Object> params) {
    ChallengeProgress progress = getProgress(userId, scenarioId);
    ChallengeScenario scenario = getScenario(scenarioId);

    // 根据步骤调用对应的现有漏洞服务
    ChallengeResult result = executeVulnerabilityStep(scenario, step, params);

    // 更新进度
    if (result.isSuccess()) {
      progress.setCurrentStep(progress.getCurrentStep() + 1);
      progress.setProgressPercentage(calculateProgress(progress, scenario));

      // 检查挑战是否完成
      boolean wasCompleted = progress.getIsCompleted();
      if (progress.getProgressPercentage().compareTo(BigDecimal.valueOf(100)) >= 0
          && !wasCompleted) {
        // 挑战刚刚完成，更新统计数据
        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        // 计算积分（根据挑战难度和完成情况）
        Integer points = calculateChallengePoints(scenario);

        // 获取漏洞代码（从vulnerabilityChain中提取第一个）
        String vulnerabilityCode = extractVulnerabilityCode(scenario);

        // 更新用户挑战统计
        userStatsUpdateService.updateChallengeStats(userId, scenario.getId(), true, points, null);

        // 记录挑战完成活动
        try {
          userActivityService.recordChallengeCompleted(userId, vulnerabilityCode, points, null);
          log.info(
              "挑战完成活动已记录: userId={}, scenarioId={}, vulnerabilityCode={}, points={}",
              userId,
              scenario.getId(),
              vulnerabilityCode,
              points);
        } catch (Exception e) {
          log.error("记录挑战完成活动失败: userId={}, scenarioId={}", userId, scenario.getId(), e);
        }

        log.info(
            "挑战完成: userId={}, scenarioId={}, points={}, vulnerabilityCode={}",
            userId,
            scenario.getId(),
            points,
            vulnerabilityCode);
      }

      progressRepository.save(progress);
    }

    return result;
  }

  /** 获取挑战进度 */
  public ChallengeProgress getProgress(Long userId, Long scenarioId) {
    return progressRepository
        .findByUserIdAndScenarioId(userId, scenarioId)
        .orElseThrow(() -> new RuntimeException("挑战进度不存在"));
  }

  /** 重置挑战进度 */
  public ChallengeProgress resetChallenge(Long userId, Long scenarioId) {
    ChallengeProgress progress = getProgress(userId, scenarioId);
    progress.setCurrentStep(0);
    progress.setCompletedSteps("[]");
    progress.setProgressPercentage(BigDecimal.ZERO);
    progress.setIsCompleted(false);
    progress.setCompletedAt(null);
    progress.setStartedAt(LocalDateTime.now());

    return progressRepository.save(progress);
  }

  /** 根据漏洞类型执行对应的服务 */
  private ChallengeResult executeVulnerabilityStep(
      ChallengeScenario scenario, String step, Map<String, Object> params) {
    try {
      List<String> vulnerabilityChain =
          objectMapper.readValue(
              scenario.getVulnerabilityChain(), new TypeReference<List<String>>() {});
      int stepIndex = Integer.parseInt(step.replace("step", "")) - 1;

      if (stepIndex >= 0 && stepIndex < vulnerabilityChain.size()) {
        String vulnerabilityType = vulnerabilityChain.get(stepIndex);

        // 使用新的验证服务验证payload
        ChallengeValidationService.ValidationResult validationResult =
            validationService.validatePayload(vulnerabilityType, step, params);

        if (validationResult.isSuccess()) {
          return ChallengeResult.success(validationResult.getMessage(), validationResult.getData());
        } else {
          return ChallengeResult.failure(validationResult.getMessage());
        }
      } else {
        return ChallengeResult.failure("无效的步骤索引: " + stepIndex);
      }
    } catch (Exception e) {
      log.error("执行挑战步骤失败", e);
      return ChallengeResult.failure("执行步骤失败: " + e.getMessage());
    }
  }

  /** 计算进度百分比 */
  private BigDecimal calculateProgress(ChallengeProgress progress, ChallengeScenario scenario) {
    try {
      List<String> vulnerabilityChain =
          objectMapper.readValue(
              scenario.getVulnerabilityChain(), new TypeReference<List<String>>() {});
      int totalSteps = vulnerabilityChain.size();
      int completedSteps = progress.getCurrentStep();
      return BigDecimal.valueOf((double) completedSteps / totalSteps * 100);
    } catch (Exception e) {
      log.error("计算进度失败", e);
      return BigDecimal.ZERO;
    }
  }

  /** 计算挑战积分 */
  private Integer calculateChallengePoints(ChallengeScenario scenario) {
    // 根据难度级别计算积分
    String difficulty = scenario.getDifficultyLevel();
    switch (difficulty != null ? difficulty.toUpperCase() : "BEGINNER") {
      case "BEGINNER":
        return 10;
      case "INTERMEDIATE":
        return 20;
      case "ADVANCED":
        return 30;
      case "EXPERT":
        return 50;
      default:
        return 10;
    }
  }

  /** 从挑战场景中提取漏洞代码 */
  private String extractVulnerabilityCode(ChallengeScenario scenario) {
    try {
      List<String> vulnerabilityChain =
          objectMapper.readValue(
              scenario.getVulnerabilityChain(), new TypeReference<List<String>>() {});
      if (!vulnerabilityChain.isEmpty()) {
        // 提取第一个漏洞代码（例如 "A03-SQL注入" -> "A03"）
        String firstVuln = vulnerabilityChain.get(0);
        // 如果包含"-"，提取前面的部分
        if (firstVuln.contains("-")) {
          return firstVuln.substring(0, firstVuln.indexOf("-"));
        }
        // 如果以"A"开头，提取前3个字符（A01, A02等）
        if (firstVuln.startsWith("A") && firstVuln.length() >= 3) {
          return firstVuln.substring(0, 3);
        }
        return firstVuln;
      }
    } catch (Exception e) {
      log.error("提取漏洞代码失败", e);
    }
    // 默认返回通用代码
    return "CHALLENGE";
  }

  /** 挑战结果类 */
  public static class ChallengeResult {
    private boolean success;
    private String message;
    private Map<String, Object> data;

    public static ChallengeResult success(String message) {
      ChallengeResult result = new ChallengeResult();
      result.success = true;
      result.message = message;
      return result;
    }

    public static ChallengeResult success(String message, Map<String, Object> data) {
      ChallengeResult result = new ChallengeResult();
      result.success = true;
      result.message = message;
      result.data = data;
      return result;
    }

    public static ChallengeResult failure(String message) {
      ChallengeResult result = new ChallengeResult();
      result.success = false;
      result.message = message;
      return result;
    }

    // Getters and Setters
    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Map<String, Object> getData() {
      return data;
    }

    public void setData(Map<String, Object> data) {
      this.data = data;
    }
  }
}
