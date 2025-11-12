package com.javaweb.security.service.impl;

import com.javaweb.security.entity.ChallengeProgress;
import com.javaweb.security.entity.ChallengeScenario;
import com.javaweb.security.entity.UserVulnerabilityProgress;
import com.javaweb.security.enums.VulnerabilityStatus;
import com.javaweb.security.repository.ChallengeProgressRepository;
import com.javaweb.security.repository.ChallengeScenarioRepository;
import com.javaweb.security.repository.UserVulnerabilityProgressRepository;
import com.javaweb.security.service.ChallengeCompletionService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 挑战完成判断服务实现
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeCompletionServiceImpl implements ChallengeCompletionService {

  private final ChallengeProgressRepository challengeProgressRepository;
  private final ChallengeScenarioRepository challengeScenarioRepository;
  private final UserVulnerabilityProgressRepository userVulnerabilityProgressRepository;

  @Override
  public boolean isVulnerabilityChallengeCompleted(Long userId, String vulnerabilityCode) {
    log.info("判断用户漏洞挑战是否完成: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);

    Optional<UserVulnerabilityProgress> progressOpt =
        userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
            userId, vulnerabilityCode);
    if (progressOpt.isEmpty()) {
      return false;
    }

    UserVulnerabilityProgress progress = progressOpt.get();
    return progress.getChallengeCompleted();
  }

  @Override
  public ChallengeCompletionCriteria getChallengeCompletionCriteria(String vulnerabilityCode) {
    return getChallengeCompletionCriteriaInternal(vulnerabilityCode);
  }

  @Override
  @Transactional
  public void recordChallengeCompletion(
      Long userId, String vulnerabilityCode, Integer score, String badge) {
    log.info(
        "记录挑战完成: userId={}, vulnerabilityCode={}, score={}, badge={}",
        userId,
        vulnerabilityCode,
        score,
        badge);

    // 更新漏洞学习进度
    updateVulnerabilityProgress(userId, vulnerabilityCode, true, score);
  }

  /** 更新漏洞学习进度 */
  private void updateVulnerabilityProgress(
      Long userId, String vulnerabilityCode, Boolean isCompleted, Integer score) {
    Optional<UserVulnerabilityProgress> progressOpt =
        userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
            userId, vulnerabilityCode);

    if (progressOpt.isPresent()) {
      UserVulnerabilityProgress progress = progressOpt.get();
      progress.setChallengeCompleted(isCompleted);
      progress.setChallengeScore(score);

      if (isCompleted) {
        // 如果挑战完成，检查是否完成所有要求
        if (progress.getLearningCompleted() && progress.getTestPassed()) {
          progress.setStatus(VulnerabilityStatus.COMPLETED);
          progress.setCompletedAt(LocalDateTime.now());
        }
      }

      userVulnerabilityProgressRepository.save(progress);
    } else {
      // 创建新的学习进度记录
      UserVulnerabilityProgress progress = new UserVulnerabilityProgress();
      progress.setUserId(userId);
      progress.setVulnerabilityCode(vulnerabilityCode);
      progress.setChallengeCompleted(isCompleted);
      progress.setChallengeScore(score);
      progress.setStatus(VulnerabilityStatus.IN_PROGRESS);
      if (progress.getStartedAt() == null) {
        progress.setStartedAt(LocalDateTime.now());
      }
      userVulnerabilityProgressRepository.save(progress);
    }
  }

  /** 获取挑战完成条件 */
  private ChallengeCompletionCriteria getChallengeCompletionCriteriaInternal(
      String vulnerabilityCode) {
    // 根据漏洞类型设置不同的挑战完成条件
    switch (vulnerabilityCode) {
      case "A01":
        return new ChallengeCompletionCriteria(true, 0.8, 3600); // 所有步骤完成，80%成功率，60分钟
      case "A02":
        return new ChallengeCompletionCriteria(true, 0.75, 3000); // 所有步骤完成，75%成功率，50分钟
      case "A03":
        return new ChallengeCompletionCriteria(true, 0.85, 4200); // 所有步骤完成，85%成功率，70分钟
      default:
        return new ChallengeCompletionCriteria(true, 0.7, 2400); // 所有步骤完成，70%成功率，40分钟
    }
  }

  /** 检查挑战是否满足完成条件 */
  public boolean checkChallengeCompletion(
      ChallengeProgress progress, ChallengeScenario scenario, String vulnerabilityCode) {
    ChallengeCompletionCriteria criteria =
        getChallengeCompletionCriteriaInternal(vulnerabilityCode);

    // 检查是否所有步骤都完成
    if (criteria.getAllStepsCompleted() && !progress.getIsCompleted()) {
      return false;
    }

    // 检查成功率
    if (progress.getProgressPercentage().doubleValue() < criteria.getMinSuccessRate() * 100) {
      return false;
    }

    // 检查时间限制
    if (criteria.getTimeLimit() > 0) {
      // 这里需要计算实际耗时，暂时跳过时间检查
    }

    return true;
  }
}
