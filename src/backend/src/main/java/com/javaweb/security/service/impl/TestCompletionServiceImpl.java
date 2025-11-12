package com.javaweb.security.service.impl;

import com.javaweb.security.entity.UserVulnerabilityProgress;
import com.javaweb.security.enums.VulnerabilityStatus;
import com.javaweb.security.repository.TestRecordRepository;
import com.javaweb.security.repository.UserTestRecordRepository;
import com.javaweb.security.repository.UserVulnerabilityProgressRepository;
import com.javaweb.security.service.TestCompletionService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试完成判断服务实现
 *
 * @author JavaWeb Security Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestCompletionServiceImpl implements TestCompletionService {

  private final TestRecordRepository testRecordRepository;
  private final UserTestRecordRepository userTestRecordRepository;
  private final UserVulnerabilityProgressRepository userVulnerabilityProgressRepository;

  @Override
  public boolean isVulnerabilityTestPassed(Long userId, String vulnerabilityCode) {
    log.info("判断用户漏洞测试是否通过: userId={}, vulnerabilityCode={}", userId, vulnerabilityCode);

    Optional<UserVulnerabilityProgress> progressOpt =
        userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
            userId, vulnerabilityCode);
    if (progressOpt.isEmpty()) {
      return false;
    }

    UserVulnerabilityProgress progress = progressOpt.get();
    return progress.getTestPassed();
  }

  @Override
  public TestCompletionCriteria getTestCompletionCriteria(String vulnerabilityCode) {
    return getTestCompletionCriteriaInternal(vulnerabilityCode);
  }

  @Override
  @Transactional
  public void recordTestCompletion(
      Long userId, String vulnerabilityCode, Integer score, Double accuracy) {
    log.info(
        "记录测试完成: userId={}, vulnerabilityCode={}, score={}, accuracy={}",
        userId,
        vulnerabilityCode,
        score,
        accuracy);

    // 更新漏洞学习进度
    boolean isPassed = score >= 70 && accuracy >= 0.7; // 简单的通过条件
    updateVulnerabilityProgress(userId, vulnerabilityCode, isPassed, score);
  }

  /** 更新漏洞学习进度 */
  private void updateVulnerabilityProgress(
      Long userId, String vulnerabilityCode, Boolean isPassed, Integer score) {
    Optional<UserVulnerabilityProgress> progressOpt =
        userVulnerabilityProgressRepository.findByUserIdAndVulnerabilityCode(
            userId, vulnerabilityCode);

    if (progressOpt.isPresent()) {
      UserVulnerabilityProgress progress = progressOpt.get();
      progress.setTestPassed(isPassed);
      progress.setTestScore(score);

      if (isPassed) {
        // 如果测试通过，检查是否完成所有要求
        if (progress.getLearningCompleted() && progress.getChallengeCompleted()) {
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
      progress.setTestPassed(isPassed);
      progress.setTestScore(score);
      progress.setStatus(VulnerabilityStatus.IN_PROGRESS);
      if (progress.getStartedAt() == null) {
        progress.setStartedAt(LocalDateTime.now());
      }
      userVulnerabilityProgressRepository.save(progress);
    }
  }

  /** 获取测试完成条件 */
  private TestCompletionCriteria getTestCompletionCriteriaInternal(String vulnerabilityCode) {
    // 根据漏洞类型设置不同的测试完成条件
    switch (vulnerabilityCode) {
      case "A01":
        return new TestCompletionCriteria(80, 0.8, 1800); // 80分，80%正确率，30分钟
      case "A02":
        return new TestCompletionCriteria(75, 0.75, 1500); // 75分，75%正确率，25分钟
      case "A03":
        return new TestCompletionCriteria(85, 0.85, 2100); // 85分，85%正确率，35分钟
      default:
        return new TestCompletionCriteria(70, 0.7, 1200); // 70分，70%正确率，20分钟
    }
  }

  /** 检查测试是否满足完成条件 */
  public boolean checkTestCompletion(
      Integer score, Double accuracy, Integer timeSpent, String vulnerabilityCode) {
    TestCompletionCriteria criteria = getTestCompletionCriteriaInternal(vulnerabilityCode);

    return score >= criteria.getMinScore()
        && accuracy >= criteria.getMinAccuracy()
        && timeSpent <= criteria.getTimeLimit();
  }
}
