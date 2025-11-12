package com.javaweb.security.service;

/**
 * 挑战完成判断服务接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
public interface ChallengeCompletionService {

  /**
   * 判断用户是否完成漏洞挑战
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @return 是否完成挑战
   */
  boolean isVulnerabilityChallengeCompleted(Long userId, String vulnerabilityCode);

  /**
   * 获取挑战完成要求
   *
   * @param vulnerabilityCode 漏洞代码
   * @return 挑战完成要求
   */
  ChallengeCompletionCriteria getChallengeCompletionCriteria(String vulnerabilityCode);

  /**
   * 记录挑战完成
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @param score 得分
   * @param badge 徽章
   */
  void recordChallengeCompletion(
      Long userId, String vulnerabilityCode, Integer score, String badge);

  /** 挑战完成要求 */
  class ChallengeCompletionCriteria {
    private Boolean allStepsCompleted;
    private Double minSuccessRate;
    private Integer timeLimit;

    public ChallengeCompletionCriteria(
        Boolean allStepsCompleted, Double minSuccessRate, Integer timeLimit) {
      this.allStepsCompleted = allStepsCompleted;
      this.minSuccessRate = minSuccessRate;
      this.timeLimit = timeLimit;
    }

    public Boolean getAllStepsCompleted() {
      return allStepsCompleted;
    }

    public Double getMinSuccessRate() {
      return minSuccessRate;
    }

    public Integer getTimeLimit() {
      return timeLimit;
    }
  }
}
