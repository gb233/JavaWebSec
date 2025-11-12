package com.javaweb.security.service;

import com.javaweb.security.entity.Question;
import com.javaweb.security.entity.TestAnswer;
import com.javaweb.security.entity.TestSession;
import com.javaweb.security.entity.UserTestRecord;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 测试服务接口
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
public interface TestService {

  /**
   * 开始测试会话
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @param categoryCode 分类代码（可选）
   * @return 测试会话
   */
  TestSession startTestSession(Long userId, String modeCode, String categoryCode);

  /**
   * 获取测试会话
   *
   * @param sessionCode 会话代码
   * @return 测试会话
   */
  TestSession getTestSession(String sessionCode);

  /**
   * 获取测试题目列表
   *
   * @param sessionCode 会话代码
   * @return 题目列表
   */
  List<Question> getTestQuestions(String sessionCode);

  /**
   * 获取当前题目
   *
   * @param sessionCode 会话代码
   * @param questionIndex 题目索引
   * @return 题目信息
   */
  Question getCurrentQuestion(String sessionCode, Integer questionIndex);

  /**
   * 根据ID获取题目
   *
   * @param questionId 题目ID
   * @return 题目信息
   */
  Question getQuestionById(Long questionId);

  /**
   * 提交答案（实时反馈模式）
   *
   * @param sessionCode 会话代码
   * @param questionId 题目ID
   * @param userAnswer 用户答案
   * @return 答题结果
   */
  TestAnswer submitAnswer(String sessionCode, Long questionId, String userAnswer);

  /**
   * 提交答案（考试模式）
   *
   * @param sessionCode 会话代码
   * @param answers 答案列表
   * @return 答题结果列表
   */
  List<TestAnswer> submitAnswers(String sessionCode, Map<Long, String> answers);

  /**
   * 获取答题反馈
   *
   * @param sessionCode 会话代码
   * @param questionId 题目ID
   * @return 答题反馈
   */
  TestAnswer getAnswerFeedback(String sessionCode, Long questionId);

  /**
   * 结束测试会话
   *
   * @param sessionCode 会话代码
   * @return 测试结果
   */
  TestSession endTestSession(String sessionCode);

  /**
   * 获取测试结果
   *
   * @param sessionCode 会话代码
   * @return 测试结果
   */
  Map<String, Object> getTestResult(String sessionCode);

  /**
   * 获取用户测试记录
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 测试记录列表
   */
  Page<UserTestRecord> getUserTestRecords(Long userId, Pageable pageable);

  /**
   * 获取用户测试统计
   *
   * @param userId 用户ID
   * @return 测试统计
   */
  Map<String, Object> getUserTestStatistics(Long userId);

  /**
   * 获取用户学习进度
   *
   * @param userId 用户ID
   * @return 学习进度
   */
  Map<String, Object> getUserLearningProgress(Long userId);

  /**
   * 获取用户薄弱环节
   *
   * @param userId 用户ID
   * @return 薄弱环节
   */
  List<Map<String, Object>> getUserWeakAreas(Long userId);

  /**
   * 获取用户学习建议
   *
   * @param userId 用户ID
   * @return 学习建议
   */
  List<Map<String, Object>> getUserLearningSuggestions(Long userId);

  /**
   * 获取错题本
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 错题列表
   */
  Page<TestAnswer> getUserWrongAnswers(Long userId, Pageable pageable);

  /**
   * 获取错题本（按分类）
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 错题列表
   */
  Page<TestAnswer> getUserWrongAnswersByCategory(
      Long userId, String categoryCode, Pageable pageable);

  /**
   * 获取测试排行榜
   *
   * @param modeCode 模式代码
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 排行榜
   */
  Page<Map<String, Object>> getTestLeaderboard(
      String modeCode, String categoryCode, Pageable pageable);

  /**
   * 获取测试分析报告
   *
   * @param sessionCode 会话代码
   * @return 分析报告
   */
  Map<String, Object> getTestAnalysisReport(String sessionCode);

  /**
   * 检查测试会话是否有效
   *
   * @param sessionCode 会话代码
   * @return 是否有效
   */
  boolean isTestSessionValid(String sessionCode);

  /**
   * 检查测试会话是否已完成
   *
   * @param sessionCode 会话代码
   * @return 是否已完成
   */
  boolean isTestSessionCompleted(String sessionCode);

  /**
   * 检查测试会话是否已超时
   *
   * @param sessionCode 会话代码
   * @return 是否已超时
   */
  boolean isTestSessionTimeout(String sessionCode);

  /**
   * 自动结束超时的测试会话
   *
   * @return 结束的会话数量
   */
  int autoEndTimeoutSessions();

  /**
   * 获取测试会话统计
   *
   * @param userId 用户ID
   * @return 会话统计
   */
  Map<String, Object> getTestSessionStatistics(Long userId);

  /**
   * 获取测试会话历史
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 会话历史
   */
  Page<TestSession> getTestSessionHistory(Long userId, Pageable pageable);

  /**
   * 获取测试会话详情
   *
   * @param sessionCode 会话代码
   * @return 会话详情
   */
  Map<String, Object> getTestSessionDetails(String sessionCode);

  /**
   * 重新开始测试
   *
   * @param sessionCode 会话代码
   * @return 新的测试会话
   */
  TestSession restartTest(String sessionCode);

  /**
   * 暂停测试会话
   *
   * @param sessionCode 会话代码
   * @return 是否成功
   */
  boolean pauseTestSession(String sessionCode);

  /**
   * 恢复测试会话
   *
   * @param sessionCode 会话代码
   * @return 是否成功
   */
  boolean resumeTestSession(String sessionCode);

  /**
   * 获取随机题目
   *
   * @param categoryCode 分类代码
   * @param count 题目数量
   * @return 题目列表
   */
  List<Question> getRandomQuestions(String categoryCode, int count);

  /**
   * 获取会话答题记录
   *
   * @param sessionId 会话ID
   * @return 答题记录列表
   */
  List<TestAnswer> getSessionAnswers(Long sessionId);

  /**
   * 获取测试记录详情
   *
   * @param recordId 记录ID
   * @param userId 用户ID
   * @return 测试记录详情
   */
  Map<String, Object> getTestRecordDetail(Long recordId, Long userId);
}
