package com.javaweb.security.service;

/**
 * 测试完成判断服务接口
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
public interface TestCompletionService {

  /**
   * 判断用户是否通过漏洞测试
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @return 是否通过测试
   */
  boolean isVulnerabilityTestPassed(Long userId, String vulnerabilityCode);

  /**
   * 获取测试完成要求
   *
   * @param vulnerabilityCode 漏洞代码
   * @return 测试完成要求
   */
  TestCompletionCriteria getTestCompletionCriteria(String vulnerabilityCode);

  /**
   * 记录测试完成
   *
   * @param userId 用户ID
   * @param vulnerabilityCode 漏洞代码
   * @param score 得分
   * @param accuracy 正确率
   */
  void recordTestCompletion(Long userId, String vulnerabilityCode, Integer score, Double accuracy);

  /** 测试完成要求 */
  class TestCompletionCriteria {
    private Integer minScore;
    private Double minAccuracy;
    private Integer timeLimit;

    public TestCompletionCriteria(Integer minScore, Double minAccuracy, Integer timeLimit) {
      this.minScore = minScore;
      this.minAccuracy = minAccuracy;
      this.timeLimit = timeLimit;
    }

    public Integer getMinScore() {
      return minScore;
    }

    public Double getMinAccuracy() {
      return minAccuracy;
    }

    public Integer getTimeLimit() {
      return timeLimit;
    }
  }
}
