package com.javaweb.security.repository;

import com.javaweb.security.entity.TestAnswer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 答题记录数据访问层
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Repository
public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {

  /**
   * 根据会话ID查找答题记录列表
   *
   * @param sessionId 会话ID
   * @return 答题记录列表
   */
  List<TestAnswer> findBySessionIdOrderByQuestionIndex(Long sessionId);

  /**
   * 根据会话ID和题目索引查找答题记录
   *
   * @param sessionId 会话ID
   * @param questionIndex 题目索引
   * @return 答题记录
   */
  Optional<TestAnswer> findBySessionIdAndQuestionIndex(Long sessionId, Integer questionIndex);

  /**
   * 根据会话ID和题目ID查找答题记录
   *
   * @param sessionId 会话ID
   * @param questionId 题目ID
   * @return 答题记录
   */
  Optional<TestAnswer> findBySessionIdAndQuestionId(Long sessionId, Long questionId);

  /**
   * 根据会话ID查找已答题记录列表
   *
   * @param sessionId 会话ID
   * @return 已答题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.userAnswer IS NOT NULL AND ta.userAnswer != ''")
  List<TestAnswer> findAnsweredBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID查找未答题记录列表
   *
   * @param sessionId 会话ID
   * @return 未答题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND (ta.userAnswer IS NULL OR ta.userAnswer = '')")
  List<TestAnswer> findUnansweredBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID查找正确答题记录列表
   *
   * @param sessionId 会话ID
   * @return 正确答题记录列表
   */
  @Query("SELECT ta FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.isCorrect = true")
  List<TestAnswer> findCorrectAnswersBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID查找错误答题记录列表
   *
   * @param sessionId 会话ID
   * @return 错误答题记录列表
   */
  @Query("SELECT ta FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.isCorrect = false")
  List<TestAnswer> findIncorrectAnswersBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID统计答题数量
   *
   * @param sessionId 会话ID
   * @return 答题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.userAnswer IS NOT NULL AND ta.userAnswer != ''")
  long countAnsweredBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID统计正确答题数量
   *
   * @param sessionId 会话ID
   * @return 正确答题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.isCorrect = true")
  long countCorrectAnswersBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID统计错误答题数量
   *
   * @param sessionId 会话ID
   * @return 错误答题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.isCorrect = false")
  long countIncorrectAnswersBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID统计总得分
   *
   * @param sessionId 会话ID
   * @return 总得分
   */
  @Query("SELECT COALESCE(SUM(ta.score), 0) FROM TestAnswer ta WHERE ta.sessionId = :sessionId")
  Integer getTotalScoreBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID统计已显示反馈的答题数量
   *
   * @param sessionId 会话ID
   * @return 已显示反馈的答题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.feedbackShown = true")
  long countFeedbackShownBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据会话ID查找未显示反馈的答题记录列表
   *
   * @param sessionId 会话ID
   * @return 未显示反馈的答题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta WHERE ta.sessionId = :sessionId AND ta.feedbackShown = false")
  List<TestAnswer> findUnshownFeedbackBySessionId(@Param("sessionId") Long sessionId);

  /**
   * 根据用户ID查找答题记录列表
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 答题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId ORDER BY ta.answeredAt DESC")
  Page<TestAnswer> findByUserId(@Param("userId") Long userId, Pageable pageable);

  /**
   * 根据用户ID和分类代码查找答题记录列表
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 答题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ts.categoryCode = :categoryCode ORDER BY ta.answeredAt DESC")
  Page<TestAnswer> findByUserIdAndCategoryCode(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode, Pageable pageable);

  /**
   * 根据用户ID和模式代码查找答题记录列表
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @param pageable 分页参数
   * @return 答题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ts.modeCode = :modeCode ORDER BY ta.answeredAt DESC")
  Page<TestAnswer> findByUserIdAndModeCode(
      @Param("userId") Long userId, @Param("modeCode") String modeCode, Pageable pageable);

  /**
   * 根据用户ID统计答题数量
   *
   * @param userId 用户ID
   * @return 答题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId")
  long countByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计正确答题数量
   *
   * @param userId 用户ID
   * @return 正确答题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ta.isCorrect = true")
  long countCorrectAnswersByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID统计总得分
   *
   * @param userId 用户ID
   * @return 总得分
   */
  @Query(
      "SELECT COALESCE(SUM(ta.score), 0) FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId")
  Integer getTotalScoreByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID和分类代码统计答题情况
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @return 答题统计
   */
  @Query(
      "SELECT COUNT(ta), COUNT(CASE WHEN ta.isCorrect = true THEN 1 END), COALESCE(SUM(ta.score), 0) "
          + "FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id "
          + "WHERE ts.userId = :userId AND ts.categoryCode = :categoryCode")
  Object[] getStatisticsByUserIdAndCategory(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode);

  /**
   * 根据用户ID查找错题记录列表
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 错题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ta.isCorrect = false ORDER BY ta.answeredAt DESC")
  Page<TestAnswer> findWrongAnswersByUserId(@Param("userId") Long userId, Pageable pageable);

  /**
   * 根据用户ID和分类代码查找错题记录列表
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 错题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ts.categoryCode = :categoryCode AND ta.isCorrect = false ORDER BY ta.answeredAt DESC")
  Page<TestAnswer> findWrongAnswersByUserIdAndCategory(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode, Pageable pageable);

  /**
   * 根据用户ID统计错题数量
   *
   * @param userId 用户ID
   * @return 错题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ta.isCorrect = false")
  long countWrongAnswersByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID和分类代码统计错题数量
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @return 错题数量
   */
  @Query(
      "SELECT COUNT(ta) FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ts.categoryCode = :categoryCode AND ta.isCorrect = false")
  long countWrongAnswersByUserIdAndCategory(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode);

  /**
   * 根据用户ID查找错题记录列表（按时间倒序）
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 错题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ta.isCorrect = false ORDER BY ta.answeredAt DESC")
  Page<TestAnswer> findByUserIdAndIsCorrectFalseOrderByAnsweredAtDesc(
      @Param("userId") Long userId, Pageable pageable);

  /**
   * 根据用户ID和分类代码查找错题记录列表（按时间倒序）
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 错题记录列表
   */
  @Query(
      "SELECT ta FROM TestAnswer ta JOIN TestSession ts ON ta.sessionId = ts.id WHERE ts.userId = :userId AND ts.categoryCode = :categoryCode AND ta.isCorrect = false ORDER BY ta.answeredAt DESC")
  Page<TestAnswer> findByUserIdAndCategoryCodeAndIsCorrectFalseOrderByAnsweredAtDesc(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode, Pageable pageable);
}
