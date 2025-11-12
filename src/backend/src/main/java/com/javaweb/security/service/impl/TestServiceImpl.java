package com.javaweb.security.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweb.security.entity.*;
import com.javaweb.security.repository.*;
import com.javaweb.security.service.BadgeService;
import com.javaweb.security.service.TestService;
import com.javaweb.security.service.UserStatsUpdateService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试服务实现类
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestServiceImpl implements TestService {

  private final TestSessionRepository testSessionRepository;
  private final QuestionRepository questionRepository;
  private final TestAnswerRepository testAnswerRepository;
  private final UserTestRecordRepository userTestRecordRepository;
  private final BadgeService badgeService;
  private final UserStatsUpdateService userStatsUpdateService;

  @Override
  @Transactional
  public TestSession startTestSession(Long userId, String modeCode, String categoryCode) {
    log.info("开始测试会话 - 用户ID: {}, 模式: {}, 分类: {}", userId, modeCode, categoryCode);

    // 生成会话代码
    String sessionCode = generateSessionCode();

    // 根据模式获取题目
    List<Question> questions = getQuestionsByMode(modeCode, categoryCode);

    // 创建测试会话
    TestSession session = new TestSession();
    session.setUserId(userId);
    session.setModeCode(modeCode);
    session.setCategoryCode(categoryCode);
    session.setSessionCode(sessionCode);
    session.setTotalQuestions(questions.size());
    session.setStatus(TestSession.SessionStatus.ACTIVE);
    session.setStartTime(LocalDateTime.now());

    TestSession savedSession = testSessionRepository.save(session);

    // 创建答题记录
    createTestAnswers(savedSession.getId(), questions);

    log.info("测试会话创建成功 - 会话ID: {}, 题目数量: {}", savedSession.getId(), questions.size());
    return savedSession;
  }

  @Override
  public TestSession getTestSession(String sessionCode) {
    return testSessionRepository
        .findBySessionCode(sessionCode)
        .orElseThrow(() -> new RuntimeException("测试会话不存在"));
  }

  @Override
  public List<Question> getTestQuestions(String sessionCode) {
    TestSession session = getTestSession(sessionCode);

    // 获取会话的答题记录，按question_index排序，确保题目顺序与创建时一致
    List<TestAnswer> answers =
        testAnswerRepository.findBySessionIdOrderByQuestionIndex(session.getId());

    // 根据答题记录中的题目ID获取题目信息，保持原始顺序，并包含答题状态
    List<Question> questions = new ArrayList<>();
    for (TestAnswer answer : answers) {
      Question question =
          questionRepository
              .findById(answer.getQuestionId())
              .orElseThrow(() -> new RuntimeException("题目不存在: " + answer.getQuestionId()));

      // 设置答题状态信息
      question.setIsAnswered(answer.isAnswered());
      question.setIsCorrect(answer.isCorrectAnswer());
      question.setUserAnswer(answer.getUserAnswer());
      // 只有当答案有实际得分时才覆盖题目分数，否则保持题目原有分数
      if (answer.getScore() != null && answer.getScore() > 0) {
        question.setScore(answer.getScore());
      }
      question.setAnsweredAt(answer.getAnsweredAt());

      questions.add(question);
    }

    return questions;
  }

  @Override
  public Question getCurrentQuestion(String sessionCode, Integer questionIndex) {
    TestSession session = getTestSession(sessionCode);

    // 直接根据questionIndex获取对应的答题记录，然后获取题目
    Optional<TestAnswer> answerOpt =
        testAnswerRepository.findBySessionIdAndQuestionIndex(session.getId(), questionIndex);
    if (answerOpt.isPresent()) {
      TestAnswer answer = answerOpt.get();
      return questionRepository
          .findById(answer.getQuestionId())
          .orElseThrow(() -> new RuntimeException("题目不存在: " + answer.getQuestionId()));
    }

    throw new RuntimeException("题目索引超出范围: " + questionIndex);
  }

  @Override
  public Question getQuestionById(Long questionId) {
    return questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("题目不存在"));
  }

  @Override
  @Transactional
  public TestAnswer submitAnswer(String sessionCode, Long questionId, String userAnswer) {
    log.info("提交答案 - 会话代码: {}, 题目ID: {}, 答案: {}", sessionCode, questionId, userAnswer);

    TestSession session = getTestSession(sessionCode);
    Question question =
        questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("题目不存在"));

    // 处理空答案：确保空答案被正确保存
    String processedAnswer =
        (userAnswer == null || userAnswer.trim().isEmpty()) ? "" : userAnswer.trim();

    // 判断答案是否正确
    boolean isCorrect = checkAnswer(question, processedAnswer);
    int score = isCorrect ? question.getScore() : 0;

    // 获取题目在会话中的索引
    int questionIndex = getQuestionIndexInSession(session, questionId);

    // 修复：检查是否已存在答案记录，避免重复创建
    Optional<TestAnswer> existingAnswer =
        testAnswerRepository.findBySessionIdAndQuestionId(session.getId(), questionId);

    TestAnswer answer;
    if (existingAnswer.isPresent()) {
      // 更新现有答案记录
      answer = existingAnswer.get();
      answer.setUserAnswer(processedAnswer);
      answer.setIsCorrect(isCorrect);
      answer.setScore(score);
      answer.setAnsweredAt(LocalDateTime.now());
      log.info("更新现有答案记录 - 题目ID: {}, 答案: {}", questionId, processedAnswer);
    } else {
      // 创建新的答案记录
      answer = new TestAnswer();
      answer.setSessionId(session.getId());
      answer.setQuestionId(questionId);
      answer.setQuestionIndex(questionIndex);
      answer.setUserAnswer(processedAnswer);
      answer.setIsCorrect(isCorrect);
      answer.setScore(score);
      answer.setAnsweredAt(LocalDateTime.now());
      log.info("创建新答案记录 - 题目ID: {}, 答案: {}", questionId, processedAnswer);
    }

    TestAnswer savedAnswer = testAnswerRepository.save(answer);

    // 更新会话统计
    updateSessionStatistics(session.getId());

    log.info(
        "答案提交成功 - 是否正确: {}, 得分: {}, 模式: {}, 处理后的答案: '{}'",
        isCorrect,
        score,
        session.getModeCode(),
        processedAnswer);
    return savedAnswer;
  }

  @Override
  @Transactional
  public List<TestAnswer> submitAnswers(String sessionCode, Map<Long, String> answers) {
    List<TestAnswer> result = new ArrayList<>();
    for (Map.Entry<Long, String> entry : answers.entrySet()) {
      TestAnswer answer = submitAnswer(sessionCode, entry.getKey(), entry.getValue());
      result.add(answer);
    }
    return result;
  }

  @Override
  public TestAnswer getAnswerFeedback(String sessionCode, Long questionId) {
    TestSession session = getTestSession(sessionCode);
    return testAnswerRepository
        .findBySessionIdAndQuestionId(session.getId(), questionId)
        .orElseThrow(() -> new RuntimeException("答题记录不存在"));
  }

  @Override
  @Transactional
  public TestSession endTestSession(String sessionCode) {
    log.info("结束测试会话 - 会话代码: {}", sessionCode);

    TestSession session = getTestSession(sessionCode);
    session.setStatus(TestSession.SessionStatus.COMPLETED);
    session.setEndTime(LocalDateTime.now());

    // 计算最终统计
    calculateFinalStatistics(session);

    TestSession savedSession = testSessionRepository.save(session);

    // 创建用户测试记录
    createUserTestRecord(savedSession);

    log.info("测试会话结束成功 - 会话ID: {}, 总得分: {}", session.getId(), session.getTotalScore());
    return savedSession;
  }

  @Override
  public Map<String, Object> getTestResult(String sessionCode) {
    TestSession session = getTestSession(sessionCode);
    List<TestAnswer> answers =
        testAnswerRepository.findBySessionIdOrderByQuestionIndex(session.getId());

    // 查找对应的用户测试记录
    UserTestRecord userRecord =
        userTestRecordRepository.findBySessionId(session.getId()).orElse(null);

    Map<String, Object> result = new HashMap<>();
    result.put("sessionId", session.getId());
    result.put("sessionCode", sessionCode);
    result.put("modeCode", session.getModeCode());
    result.put("categoryCode", session.getCategoryCode());
    result.put("totalScore", session.getTotalScore());
    result.put("correctCount", session.getCorrectAnswers());
    result.put("totalQuestions", session.getTotalQuestions());
    result.put("completionRate", calculateCompletionRate(session));
    result.put("timeSpent", calculateTimeSpent(session));
    result.put("answers", convertToResultItems(answers));

    // 添加记录ID，用于前端跳转到结果页面
    if (userRecord != null) {
      result.put("recordId", userRecord.getId());
    }

    return result;
  }

  @Override
  public Page<UserTestRecord> getUserTestRecords(Long userId, Pageable pageable) {
    return userTestRecordRepository.findByUserIdOrderByCompletedAtDesc(userId, pageable);
  }

  @Override
  public Map<String, Object> getUserTestStatistics(Long userId) {
    Map<String, Object> statistics = new HashMap<>();

    // 总测试次数
    long totalTests = userTestRecordRepository.countByUserId(userId);
    statistics.put("totalTests", totalTests);

    // 通过次数（完成率>=60%）
    long passedTests = userTestRecordRepository.countPassedTestsByUserId(userId);
    statistics.put("passedTests", passedTests);

    // 总得分
    Integer totalScore = userTestRecordRepository.getTotalScoreByUserId(userId);
    statistics.put("totalScore", totalScore != null ? totalScore : 0);

    // 平均得分
    java.math.BigDecimal avgScore = userTestRecordRepository.getAverageScoreByUserId(userId);
    double averageScore = avgScore != null ? avgScore.doubleValue() : 0.0;
    statistics.put("averageScore", averageScore);

    // 平均正确率
    java.math.BigDecimal avgAccuracy = userTestRecordRepository.getAverageAccuracyByUserId(userId);
    double averageAccuracy = avgAccuracy != null ? avgAccuracy.doubleValue() : 0.0;
    statistics.put("averageAccuracy", averageAccuracy);

    return statistics;
  }

  @Override
  public Map<String, Object> getUserLearningProgress(Long userId) {
    Map<String, Object> progress = new HashMap<>();

    // 获取学习进度趋势
    List<Object[]> progressData =
        userTestRecordRepository.getLearningProgressByUserId(userId, Pageable.ofSize(10));
    List<Map<String, Object>> trends = new ArrayList<>();

    for (Object[] data : progressData) {
      Map<String, Object> trend = new HashMap<>();
      trend.put("completedAt", data[0]);
      trend.put("totalScore", data[1]);
      trend.put("accuracy", data[2]);
      trend.put("completionRate", data[3]);
      trends.add(trend);
    }

    progress.put("trends", trends);
    progress.put("totalTests", userTestRecordRepository.countByUserId(userId));

    return progress;
  }

  @Override
  public List<Map<String, Object>> getUserWeakAreas(Long userId) {
    List<Object[]> weakAreasData = userTestRecordRepository.getWeakAreasByUserId(userId);
    List<Map<String, Object>> weakAreas = new ArrayList<>();

    for (Object[] data : weakAreasData) {
      Map<String, Object> weakArea = new HashMap<>();
      weakArea.put("categoryCode", data[0]);
      weakArea.put("averageAccuracy", data[1]);
      weakAreas.add(weakArea);
    }

    return weakAreas;
  }

  @Override
  public List<Map<String, Object>> getUserLearningSuggestions(Long userId) {
    List<Object[]> suggestionsData =
        userTestRecordRepository.getLearningSuggestionsByUserId(userId);
    List<Map<String, Object>> suggestions = new ArrayList<>();

    for (Object[] data : suggestionsData) {
      Map<String, Object> suggestion = new HashMap<>();
      suggestion.put("categoryCode", data[0]);
      suggestion.put("testCount", data[1]);
      suggestion.put("averageAccuracy", data[2]);
      suggestion.put("averageCompletionRate", data[3]);
      suggestions.add(suggestion);
    }

    return suggestions;
  }

  @Override
  public Page<TestAnswer> getUserWrongAnswers(Long userId, Pageable pageable) {
    return testAnswerRepository.findByUserIdAndIsCorrectFalseOrderByAnsweredAtDesc(
        userId, pageable);
  }

  @Override
  public Page<TestAnswer> getUserWrongAnswersByCategory(
      Long userId, String categoryCode, Pageable pageable) {
    return testAnswerRepository.findByUserIdAndCategoryCodeAndIsCorrectFalseOrderByAnsweredAtDesc(
        userId, categoryCode, pageable);
  }

  @Override
  public Page<Map<String, Object>> getTestLeaderboard(
      String modeCode, String categoryCode, Pageable pageable) {
    // 获取排行榜数据 - 基于用户测试记录
    Page<UserTestRecord> records;
    if (categoryCode != null && modeCode != null) {
      records =
          userTestRecordRepository.findByUserIdAndModeCodeAndCategoryCodeOrderByCompletedAtDesc(
              null, modeCode, categoryCode, pageable);
    } else if (categoryCode != null) {
      records =
          userTestRecordRepository.findByUserIdAndCategoryCodeOrderByCompletedAtDesc(
              null, categoryCode, pageable);
    } else if (modeCode != null) {
      records =
          userTestRecordRepository.findByUserIdAndModeCodeOrderByCompletedAtDesc(
              null, modeCode, pageable);
    } else {
      records = userTestRecordRepository.findByUserIdOrderByCompletedAtDesc(null, pageable);
    }

    return records.map(
        record -> {
          Map<String, Object> leaderboardItem = new HashMap<>();
          leaderboardItem.put("userId", record.getUserId());
          leaderboardItem.put("totalScore", record.getTotalScore());
          leaderboardItem.put("correctCount", record.getCorrectCount());
          leaderboardItem.put("totalQuestions", record.getTotalQuestions());
          leaderboardItem.put("completionRate", record.getCompletionRate());
          leaderboardItem.put("timeSpent", record.getTimeSpent());
          leaderboardItem.put("completedAt", record.getCompletedAt());
          return leaderboardItem;
        });
  }

  @Override
  public Map<String, Object> getTestAnalysisReport(String sessionCode) {
    TestSession session = getTestSession(sessionCode);
    List<TestAnswer> answers =
        testAnswerRepository.findBySessionIdOrderByQuestionIndex(session.getId());

    Map<String, Object> report = new HashMap<>();
    report.put("sessionId", session.getId());
    report.put("sessionCode", sessionCode);
    report.put("totalQuestions", session.getTotalQuestions());
    report.put("answeredQuestions", session.getAnsweredQuestions());
    report.put("correctAnswers", session.getCorrectAnswers());
    report.put("totalScore", session.getTotalScore());
    report.put("completionRate", calculateCompletionRate(session));
    report.put("timeSpent", calculateTimeSpent(session));

    // 按题目类型统计
    Map<String, Integer> typeStats = new HashMap<>();
    Map<String, Integer> typeCorrect = new HashMap<>();

    for (TestAnswer answer : answers) {
      Question question = questionRepository.findById(answer.getQuestionId()).orElse(null);
      if (question != null) {
        String type = question.getQuestionType().toString();
        typeStats.put(type, typeStats.getOrDefault(type, 0) + 1);
        if (Boolean.TRUE.equals(answer.getIsCorrect())) {
          typeCorrect.put(type, typeCorrect.getOrDefault(type, 0) + 1);
        }
      }
    }

    report.put("typeStatistics", typeStats);
    report.put("typeCorrectCount", typeCorrect);

    return report;
  }

  @Override
  public boolean isTestSessionValid(String sessionCode) {
    try {
      TestSession session = getTestSession(sessionCode);
      return session.getStatus() == TestSession.SessionStatus.ACTIVE;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean isTestSessionCompleted(String sessionCode) {
    try {
      TestSession session = getTestSession(sessionCode);
      return session.getStatus() == TestSession.SessionStatus.COMPLETED;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean isTestSessionTimeout(String sessionCode) {
    try {
      TestSession session = getTestSession(sessionCode);
      if (session.getStatus() != TestSession.SessionStatus.ACTIVE) {
        return false;
      }

      // 检查是否超时（假设超时时间为2小时）
      LocalDateTime timeoutTime = session.getStartTime().plusHours(2);
      return LocalDateTime.now().isAfter(timeoutTime);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public int autoEndTimeoutSessions() {
    // 查找超时的会话（2小时前开始的活跃会话）
    LocalDateTime timeoutTime = LocalDateTime.now().minusHours(2);
    List<TestSession> timeoutSessions = testSessionRepository.findTimeoutSessions(timeoutTime);

    int endedCount = 0;
    for (TestSession session : timeoutSessions) {
      session.setStatus(TestSession.SessionStatus.COMPLETED);
      session.setEndTime(LocalDateTime.now());
      testSessionRepository.save(session);
      endedCount++;
    }

    log.info("自动结束超时会话数量: {}", endedCount);
    return endedCount;
  }

  @Override
  public Map<String, Object> getTestSessionStatistics(Long userId) {
    Map<String, Object> statistics = new HashMap<>();

    // 总会话数
    long totalSessions = testSessionRepository.countByUserId(userId);
    statistics.put("totalSessions", totalSessions);

    // 活跃会话数
    long activeSessions =
        testSessionRepository.countByUserIdAndStatus(userId, TestSession.SessionStatus.ACTIVE);
    statistics.put("activeSessions", activeSessions);

    // 已完成会话数
    long completedSessions =
        testSessionRepository.countByUserIdAndStatus(userId, TestSession.SessionStatus.COMPLETED);
    statistics.put("completedSessions", completedSessions);

    // 总得分
    Integer totalScore = testSessionRepository.getTotalScoreByUserId(userId);
    statistics.put("totalScore", totalScore != null ? totalScore : 0);

    // 总正确题数
    Integer totalCorrect = testSessionRepository.getTotalCorrectAnswersByUserId(userId);
    statistics.put("totalCorrectAnswers", totalCorrect != null ? totalCorrect : 0);

    // 总答题数
    Integer totalAnswered = testSessionRepository.getTotalAnsweredQuestionsByUserId(userId);
    statistics.put("totalAnsweredQuestions", totalAnswered != null ? totalAnswered : 0);

    return statistics;
  }

  @Override
  public Page<TestSession> getTestSessionHistory(Long userId, Pageable pageable) {
    return testSessionRepository.findByUserIdOrderByStartTimeDesc(userId, pageable);
  }

  @Override
  public Map<String, Object> getTestSessionDetails(String sessionCode) {
    TestSession session = getTestSession(sessionCode);
    Map<String, Object> details = new HashMap<>();
    details.put("sessionId", session.getId());
    details.put("sessionCode", sessionCode);
    details.put("modeCode", session.getModeCode());
    details.put("categoryCode", session.getCategoryCode());
    details.put("status", session.getStatus());
    details.put("totalQuestions", session.getTotalQuestions());
    details.put("answeredQuestions", session.getAnsweredQuestions());
    details.put("correctAnswers", session.getCorrectAnswers());
    details.put("totalScore", session.getTotalScore());
    details.put("startTime", session.getStartTime());
    details.put("endTime", session.getEndTime());
    return details;
  }

  @Override
  public TestSession restartTest(String sessionCode) {
    TestSession session = getTestSession(sessionCode);

    // 重置会话状态
    session.setStatus(TestSession.SessionStatus.ACTIVE);
    session.setStartTime(LocalDateTime.now());
    session.setEndTime(null);
    session.setAnsweredQuestions(0);
    session.setCorrectAnswers(0);
    session.setTotalScore(0);

    // 清除所有答题记录
    List<TestAnswer> answers =
        testAnswerRepository.findBySessionIdOrderByQuestionIndex(session.getId());
    for (TestAnswer answer : answers) {
      answer.setUserAnswer(null);
      answer.setIsCorrect(null);
      answer.setScore(0);
      answer.setAnsweredAt(null);
      testAnswerRepository.save(answer);
    }

    TestSession savedSession = testSessionRepository.save(session);
    log.info("测试会话重新开始 - 会话ID: {}", savedSession.getId());
    return savedSession;
  }

  @Override
  public boolean pauseTestSession(String sessionCode) {
    try {
      TestSession session = getTestSession(sessionCode);
      if (session.getStatus() == TestSession.SessionStatus.ACTIVE) {
        // 这里可以添加暂停逻辑，比如记录暂停时间
        // 由于当前实体没有暂停状态，暂时返回true表示操作成功
        log.info("测试会话暂停 - 会话代码: {}", sessionCode);
        return true;
      }
      return false;
    } catch (Exception e) {
      log.error("暂停测试会话失败", e);
      return false;
    }
  }

  @Override
  public boolean resumeTestSession(String sessionCode) {
    try {
      TestSession session = getTestSession(sessionCode);
      if (session.getStatus() == TestSession.SessionStatus.ACTIVE) {
        // 这里可以添加恢复逻辑，比如计算暂停时间
        // 由于当前实体没有暂停状态，暂时返回true表示操作成功
        log.info("测试会话恢复 - 会话代码: {}", sessionCode);
        return true;
      }
      return false;
    } catch (Exception e) {
      log.error("恢复测试会话失败", e);
      return false;
    }
  }

  @Override
  public List<Question> getRandomQuestions(String categoryCode, int count) {
    try {
      log.info("获取随机题目 - 分类: {}, 数量: {}", categoryCode, count);
      return questionRepository.findRandomQuestionsByCategory(categoryCode, count);
    } catch (Exception e) {
      log.error("获取随机题目失败", e);
      throw new RuntimeException("获取随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<TestAnswer> getSessionAnswers(Long sessionId) {
    try {
      log.info("获取会话答题记录 - 会话ID: {}", sessionId);
      return testAnswerRepository.findBySessionIdOrderByQuestionIndex(sessionId);
    } catch (Exception e) {
      log.error("获取会话答题记录失败", e);
      throw new RuntimeException("获取会话答题记录失败: " + e.getMessage());
    }
  }

  // 私有方法
  private String generateSessionCode() {
    return "T" + System.currentTimeMillis() % 100000 + (int) (Math.random() * 100);
  }

  private List<Question> getQuestionsByMode(String modeCode, String categoryCode) {
    List<Question> questions = new ArrayList<>();

    // 先获取该分类的实际题目数量
    long availableCount = questionRepository.countByCategoryCode(categoryCode);
    log.info("分类 {} 可用题目数量: {}", categoryCode, availableCount);

    switch (modeCode) {
      case "realtime":
      case "REALTIME_FEEDBACK":
        // 实时反馈模式：获取该分类的所有题目，适合学习
        questions =
            questionRepository.findRandomQuestionsByCategory(categoryCode, (int) availableCount);
        log.info("实时反馈模式 - 获取题目数量: {} (分类: {})", questions.size(), categoryCode);
        break;
      case "exam":
      case "EXAM_MODE":
        // 考试模式：获取该分类的所有题目，完整测试
        questions =
            questionRepository.findRandomQuestionsByCategory(categoryCode, (int) availableCount);
        log.info("考试模式 - 获取题目数量: {} (分类: {})", questions.size(), categoryCode);
        break;
      case "random":
      case "RANDOM_COMPREHENSIVE":
        // 随机综合模式：从所有分类中随机获取题目，综合练习
        long totalCount = questionRepository.count();
        questions = questionRepository.findRandomQuestionsAcrossCategories((int) totalCount);
        log.info("随机综合模式 - 获取题目数量: {} (全部分类)", questions.size());
        break;
      default:
        throw new RuntimeException("不支持的测试模式: " + modeCode);
    }

    if (questions.isEmpty()) {
      log.warn("没有找到可用的题目 - 分类: {}, 模式: {}", categoryCode, modeCode);
    } else {
      log.info("获取到题目数量: {} - 分类: {}, 模式: {}", questions.size(), categoryCode, modeCode);
    }

    return questions;
  }

  private void createTestAnswers(Long sessionId, List<Question> questions) {
    for (int i = 0; i < questions.size(); i++) {
      TestAnswer answer = new TestAnswer();
      answer.setSessionId(sessionId);
      answer.setQuestionId(questions.get(i).getId());
      answer.setQuestionIndex(i);
      testAnswerRepository.save(answer);
    }
  }

  private boolean checkAnswer(Question question, String userAnswer) {
    if (userAnswer == null || userAnswer.trim().isEmpty()) {
      log.warn("用户答案为空 - 题目ID: {}", question.getId());
      return false;
    }

    String correctAnswer = question.getCorrectAnswer();
    log.info(
        "答案检查 - 题目ID: {}, 用户答案: '{}', 正确答案: '{}', 题目类型: {}",
        question.getId(),
        userAnswer,
        correctAnswer,
        question.getQuestionType());

    switch (question.getQuestionType()) {
      case SINGLE:
      case JUDGE:
        boolean isCorrect = correctAnswer.equalsIgnoreCase(userAnswer.trim());
        log.info("单选题/判断题答案比较结果: {}", isCorrect);
        return isCorrect;
      case MULTIPLE:
        // 多选题需要解析JSON格式的答案
        try {
          // 解析选项JSON
          String optionsJson = question.getOptions();
          if (optionsJson != null && !optionsJson.isEmpty()) {
            // 解析选项数组
            ObjectMapper mapper = new ObjectMapper();
            String[] options = mapper.readValue(optionsJson, String[].class);

            // 将用户答案转换为字母格式
            String userAnswerLetters = convertUserAnswerToLetters(userAnswer, options);
            log.info("多选题答案处理 - 原始用户答案: '{}', 转换后: '{}'", userAnswer, userAnswerLetters);

            // 解析正确答案（可能是JSON数组格式["A","B","C"]或字符串格式"ABC"）
            String normalizedCorrectAnswer = parseCorrectAnswer(correctAnswer, mapper);
            String normalizedUserAnswer = normalizeAnswer(userAnswerLetters);
            boolean result = normalizedCorrectAnswer.equalsIgnoreCase(normalizedUserAnswer);
            log.info(
                "多选题答案比较 - 标准化正确答案: '{}', 标准化用户答案: '{}', 是否相等: {}",
                normalizedCorrectAnswer,
                normalizedUserAnswer,
                result);
            return result;
          }
          // 如果没有选项，直接比较字符串
          boolean directResult = correctAnswer.equalsIgnoreCase(userAnswer.trim());
          log.info("多选题直接比较结果: {}", directResult);
          return directResult;
        } catch (Exception e) {
          log.error("解析多选题答案失败 - 题目ID: {}, 错误: {}", question.getId(), e.getMessage(), e);
          return false;
        }
      default:
        log.warn("未知题目类型: {}", question.getQuestionType());
        return false;
    }
  }

  /** 解析正确答案（支持JSON数组格式和字符串格式） */
  private String parseCorrectAnswer(String correctAnswer, ObjectMapper mapper) {
    if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
      return "";
    }

    try {
      // 尝试解析为JSON数组格式（如["A","B","C"]）
      if (correctAnswer.trim().startsWith("[")) {
        String[] answerArray = mapper.readValue(correctAnswer, String[].class);
        // 将数组转换为排序后的字符串
        StringBuilder sb = new StringBuilder();
        for (String item : answerArray) {
          // 移除引号和空格，提取字母
          String clean = item.replaceAll("[\"\\[\\] ]", "").toUpperCase();
          if (!clean.isEmpty()) {
            sb.append(clean);
          }
        }
        // 排序后返回
        char[] chars = sb.toString().toCharArray();
        java.util.Arrays.sort(chars);
        return new String(chars);
      }
    } catch (Exception e) {
      log.debug("正确答案不是JSON数组格式，尝试字符串格式解析: {}", e.getMessage());
    }

    // 如果不是JSON数组，按字符串格式处理
    return normalizeAnswer(correctAnswer);
  }

  /** 标准化答案格式，处理逗号分隔和连续字母两种格式 */
  private String normalizeAnswer(String answer) {
    if (answer == null || answer.trim().isEmpty()) {
      return "";
    }

    // 移除所有逗号、空格、引号和方括号，然后按字母顺序排列
    String cleanAnswer = answer.replaceAll("[, \\[\\]\"']", "").toUpperCase();

    // 将字符转换为数组，排序，然后重新组合
    char[] chars = cleanAnswer.toCharArray();
    java.util.Arrays.sort(chars);
    return new String(chars);
  }

  /** 将用户答案转换为字母格式 */
  private String convertUserAnswerToLetters(String userAnswer, String[] options) {
    if (userAnswer == null || userAnswer.trim().isEmpty()) {
      log.warn("用户答案为空，无法转换");
      return "";
    }

    log.info("转换用户答案 - 原始答案: '{}', 选项数量: {}", userAnswer, options.length);

    // 如果用户答案已经是字母格式（如"ABC"或"A,B,C"），直接处理
    if (userAnswer.matches("^[A-Z,]+$")) {
      // 移除逗号，只保留字母
      String result = userAnswer.replaceAll(",", "");
      log.info("用户答案已是字母格式，处理后: '{}'", result);
      return result;
    }

    // 如果用户答案是选项文本格式，需要转换
    StringBuilder result = new StringBuilder();
    String[] userAnswers = userAnswer.split(",");

    for (String answer : userAnswers) {
      String trimmedAnswer = answer.trim();
      log.info("处理用户答案项: '{}'", trimmedAnswer);

      // 查找匹配的选项索引
      boolean found = false;
      for (int i = 0; i < options.length; i++) {
        if (options[i].equals(trimmedAnswer)) {
          char letter = (char) ('A' + i);
          result.append(letter);
          log.info("找到匹配选项，索引: {}, 字母: {}", i, letter);
          found = true;
          break;
        }
      }

      if (!found) {
        log.warn("未找到匹配的选项: '{}'", trimmedAnswer);
      }
    }

    String finalResult = result.toString();
    log.info("用户答案转换完成: '{}' -> '{}'", userAnswer, finalResult);
    return finalResult;
  }

  /** 获取题目在会话中的索引 */
  private int getQuestionIndexInSession(TestSession session, Long questionId) {
    // 获取会话的所有答题记录，按question_index排序
    List<TestAnswer> answers =
        testAnswerRepository.findBySessionIdOrderByQuestionIndex(session.getId());

    // 查找题目在答题记录中的索引
    for (TestAnswer answer : answers) {
      if (answer.getQuestionId().equals(questionId)) {
        return answer.getQuestionIndex();
      }
    }

    // 如果找不到，返回当前答题记录数量作为新索引
    int nextIndex = answers.size();
    log.info("题目不在现有答题记录中，使用新索引: {}, 题目ID: {}, 会话ID: {}", nextIndex, questionId, session.getId());
    return nextIndex;
  }

  private void updateSessionStatistics(Long sessionId) {
    TestSession session =
        testSessionRepository
            .findById(sessionId)
            .orElseThrow(() -> new RuntimeException("测试会话不存在"));

    // 统计已答题数量 - 修复：包括空答案在内的所有提交的答案
    List<TestAnswer> answers = testAnswerRepository.findBySessionIdOrderByQuestionIndex(sessionId);
    long answeredCount =
        answers.stream()
            .filter(answer -> answer.getUserAnswer() != null) // 只要有userAnswer记录就算已答题
            .count();
    session.setAnsweredQuestions((int) answeredCount);

    // 统计正确答题数量
    long correctCount =
        answers.stream().filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect())).count();
    session.setCorrectAnswers((int) correctCount);

    // 统计总得分
    int totalScore =
        answers.stream()
            .filter(answer -> answer.getScore() != null)
            .mapToInt(TestAnswer::getScore)
            .sum();
    session.setTotalScore(totalScore);

    log.info(
        "更新会话统计 - 会话ID: {}, 已答题: {}, 正确题: {}, 总得分: {}",
        sessionId,
        answeredCount,
        correctCount,
        totalScore);

    testSessionRepository.save(session);
  }

  private void calculateFinalStatistics(TestSession session) {
    // 获取所有答题记录
    List<TestAnswer> answers =
        testAnswerRepository.findBySessionIdOrderByQuestionIndex(session.getId());

    int totalQuestions = session.getTotalQuestions();
    int answeredQuestions = 0;
    int correctAnswers = 0;
    int totalScore = 0;

    for (TestAnswer answer : answers) {
      // 修复：只要有userAnswer记录就算已答题，包括空答案
      if (answer.getUserAnswer() != null) {
        answeredQuestions++;
        if (Boolean.TRUE.equals(answer.getIsCorrect())) {
          correctAnswers++;
        }
        totalScore += answer.getScore() != null ? answer.getScore() : 0;
      }
    }

    // 修复分数计算：确保分数计算正确
    session.setAnsweredQuestions(answeredQuestions);
    session.setCorrectAnswers(correctAnswers);
    session.setTotalScore(totalScore);

    // 计算完成率（百分比）- 修复：应该基于已答题数而不是正确题数
    double completionRate =
        totalQuestions > 0 ? (double) answeredQuestions / totalQuestions * 100 : 0.0;

    log.info(
        "最终统计计算 - 总题数: {}, 已答题: {}, 正确题: {}, 总得分: {}, 完成率: {}%",
        totalQuestions, answeredQuestions, correctAnswers, totalScore, completionRate);
  }

  private void createUserTestRecord(TestSession session) {
    UserTestRecord record = new UserTestRecord();
    record.setUserId(session.getUserId());
    record.setSessionId(session.getId());
    record.setModeCode(session.getModeCode());
    record.setCategoryCode(session.getCategoryCode());
    record.setTotalScore(session.getTotalScore());
    record.setCorrectCount(session.getCorrectAnswers());
    record.setTotalQuestions(session.getTotalQuestions());

    // 计算正确率（百分比分数）
    if (session.getTotalQuestions() > 0) {
      double accuracyRate =
          (double) session.getCorrectAnswers() / session.getTotalQuestions() * 100;
      record.setCompletionRate(java.math.BigDecimal.valueOf(accuracyRate));
    }

    // 计算用时
    if (session.getStartTime() != null && session.getEndTime() != null) {
      long timeSpent =
          java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds();
      record.setTimeSpent((int) timeSpent);
    }

    record.setStartedAt(session.getStartTime());
    record.setCompletedAt(session.getEndTime());

    userTestRecordRepository.save(record);
    log.info("用户测试记录创建成功 - 用户ID: {}, 得分: {}", session.getUserId(), session.getTotalScore());

    // 计算是否通过（完成率>=60%）
    boolean passed =
        record.getCompletionRate() != null && record.getCompletionRate().doubleValue() >= 60.0;

    // 计算积分（根据得分和是否通过）
    int points = passed ? session.getTotalScore() : 0;

    // 更新用户测试统计（同步更新UserProfile.passedTests）
    try {
      userStatsUpdateService.updateTestStats(
          session.getUserId(),
          session.getId(), // 使用sessionId作为testId
          passed,
          session.getTotalScore(),
          points);
      log.info(
          "用户测试统计更新成功 - 用户ID: {}, 通过: {}, 得分: {}, 积分: {}",
          session.getUserId(),
          passed,
          session.getTotalScore(),
          points);
    } catch (Exception e) {
      log.error("更新用户测试统计失败 - 用户ID: {}, 错误: {}", session.getUserId(), e.getMessage(), e);
    }

    // 触发徽章检测
    try {
      Map<String, Object> badgeData = new HashMap<>();
      badgeData.put("score", session.getTotalScore());
      badgeData.put("accuracy", record.getCompletionRate().doubleValue());
      badgeData.put("testCount", 1); // 本次测试计数

      badgeService.checkAndAwardBadges(session.getUserId(), "TEST", badgeData);
      log.info("徽章检测完成 - 用户ID: {}", session.getUserId());
    } catch (Exception e) {
      log.error("徽章检测失败 - 用户ID: {}, 错误: {}", session.getUserId(), e.getMessage());
    }
  }

  private double calculateCompletionRate(TestSession session) {
    return (double) session.getAnsweredQuestions() / session.getTotalQuestions() * 100;
  }

  private int calculateTimeSpent(TestSession session) {
    if (session.getEndTime() != null) {
      return (int)
          java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds();
    } else {
      return (int)
          java.time.Duration.between(session.getStartTime(), LocalDateTime.now()).getSeconds();
    }
  }

  private List<Map<String, Object>> convertToResultItems(List<TestAnswer> answers) {
    return answers.stream()
        .map(
            answer -> {
              // 获取题目信息
              Question question =
                  questionRepository
                      .findById(answer.getQuestionId())
                      .orElseThrow(() -> new RuntimeException("题目不存在"));

              Map<String, Object> item = new HashMap<>();
              item.put("id", answer.getId());
              item.put("questionId", answer.getQuestionId());
              item.put("questionText", question.getQuestionText());

              // 修复答案显示问题：确保userAnswer不为空
              String userAnswer = answer.getUserAnswer();
              if (userAnswer == null || userAnswer.trim().isEmpty()) {
                // 如果用户答案为空，检查是否已答题
                if (answer.isAnswered()) {
                  userAnswer = "已答题但答案为空";
                } else {
                  userAnswer = "未作答";
                }
              }
              item.put("userAnswer", userAnswer);

              item.put("correctAnswer", question.getCorrectAnswer());
              item.put("explanation", question.getExplanation());
              item.put("isCorrect", answer.getIsCorrect());
              item.put("score", answer.getScore());
              item.put("answeredAt", answer.getAnsweredAt());
              return item;
            })
        .collect(Collectors.toList());
  }

  @Override
  public Map<String, Object> getTestRecordDetail(Long recordId, Long userId) {
    log.info("获取测试记录详情 - 记录ID: {}, 用户ID: {}", recordId, userId);

    // 获取用户测试记录
    UserTestRecord record =
        userTestRecordRepository
            .findById(recordId)
            .orElseThrow(() -> new RuntimeException("测试记录不存在"));

    // 验证记录属于当前用户
    if (!record.getUserId().equals(userId)) {
      throw new RuntimeException("无权访问该测试记录");
    }

    // 获取测试会话
    TestSession session =
        testSessionRepository
            .findById(record.getSessionId())
            .orElseThrow(() -> new RuntimeException("测试会话不存在"));

    // 获取答题详情
    List<TestAnswer> answers =
        testAnswerRepository.findBySessionIdOrderByQuestionIndex(session.getId());
    List<Map<String, Object>> answerDetails = convertToResultItems(answers);

    // 构建返回数据
    Map<String, Object> result = new HashMap<>();
    result.put("id", record.getId());
    result.put("userId", record.getUserId());
    result.put("testName", "安全知识测试");
    result.put("categoryCode", record.getCategoryCode());
    result.put("categoryName", getCategoryName(record.getCategoryCode()));
    result.put("totalQuestions", record.getTotalQuestions());
    result.put("correctAnswers", record.getCorrectCount());
    result.put("wrongAnswers", record.getTotalQuestions() - record.getCorrectCount());
    result.put("score", record.getTotalScore());
    result.put("maxScore", record.getTotalQuestions() * 10); // 假设每题10分
    // 计算正确的百分比分数（正确率）
    double percentage =
        record.getTotalQuestions() > 0
            ? (double) record.getCorrectCount() / record.getTotalQuestions() * 100
            : 0.0;
    result.put("percentage", percentage);
    result.put("timeTaken", record.getTimeSpent());
    result.put("timeLimit", 30 * 60); // 30分钟
    result.put("isPassed", percentage >= 60.0);
    result.put("passThreshold", 60.0);
    result.put("startedAt", record.getStartedAt().toString());
    result.put(
        "completedAt", record.getCompletedAt() != null ? record.getCompletedAt().toString() : "");
    result.put("createdAt", record.getStartedAt().toString());
    result.put("answerDetails", answerDetails);

    log.info("测试记录详情获取成功 - 记录ID: {}, 答题数量: {}", recordId, answerDetails.size());
    return result;
  }

  private String getCategoryName(String categoryCode) {
    switch (categoryCode) {
      case "A01":
        return "越权访问";
      case "A02":
        return "加密失败";
      case "A03":
        return "注入漏洞";
      case "A04":
        return "不安全设计";
      case "A05":
        return "安全配置错误";
      case "A06":
        return "易受攻击组件";
      case "A07":
        return "身份验证失败";
      case "A08":
        return "软件和数据完整性失效";
      case "A09":
        return "日志监控失效";
      case "A10":
        return "服务端请求伪造";
      default:
        return "未知分类";
    }
  }
}
