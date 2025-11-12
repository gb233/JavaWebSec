package com.javaweb.security.repository;

import com.javaweb.security.entity.TestSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 答题会话数据访问层
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {

  /**
   * 根据会话代码查找会话
   *
   * @param sessionCode 会话代码
   * @return 会话信息
   */
  Optional<TestSession> findBySessionCode(String sessionCode);

  /**
   * 根据用户ID查找会话列表
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 会话列表
   */
  Page<TestSession> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  /**
   * 根据用户ID和状态查找会话列表
   *
   * @param userId 用户ID
   * @param status 会话状态
   * @param pageable 分页参数
   * @return 会话列表
   */
  Page<TestSession> findByUserIdAndStatusOrderByCreatedAtDesc(
      Long userId, TestSession.SessionStatus status, Pageable pageable);

  /**
   * 根据用户ID和模式代码查找会话列表
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @param pageable 分页参数
   * @return 会话列表
   */
  Page<TestSession> findByUserIdAndModeCodeOrderByCreatedAtDesc(
      Long userId, String modeCode, Pageable pageable);

  /**
   * 根据用户ID和分类代码查找会话列表
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @param pageable 分页参数
   * @return 会话列表
   */
  Page<TestSession> findByUserIdAndCategoryCodeOrderByCreatedAtDesc(
      Long userId, String categoryCode, Pageable pageable);

  /**
   * 统计用户的会话数量
   *
   * @param userId 用户ID
   * @return 会话数量
   */
  long countByUserId(Long userId);

  /**
   * 统计用户指定状态的会话数量
   *
   * @param userId 用户ID
   * @param status 会话状态
   * @return 会话数量
   */
  long countByUserIdAndStatus(Long userId, TestSession.SessionStatus status);

  /**
   * 统计用户指定模式的会话数量
   *
   * @param userId 用户ID
   * @param modeCode 模式代码
   * @return 会话数量
   */
  long countByUserIdAndModeCode(Long userId, String modeCode);

  /**
   * 统计用户指定分类的会话数量
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @return 会话数量
   */
  long countByUserIdAndCategoryCode(Long userId, String categoryCode);

  /**
   * 查找用户的活跃会话
   *
   * @param userId 用户ID
   * @return 活跃会话列表
   */
  List<TestSession> findByUserIdAndStatus(Long userId, TestSession.SessionStatus status);

  /**
   * 查找超时的会话
   *
   * @param timeoutTime 超时时间
   * @return 超时会话列表
   */
  @Query("SELECT ts FROM TestSession ts WHERE ts.status = 'ACTIVE' AND ts.startTime < :timeoutTime")
  List<TestSession> findTimeoutSessions(@Param("timeoutTime") LocalDateTime timeoutTime);

  /**
   * 统计用户的总得分
   *
   * @param userId 用户ID
   * @return 总得分
   */
  @Query(
      "SELECT COALESCE(SUM(ts.totalScore), 0) FROM TestSession ts WHERE ts.userId = :userId AND ts.status = 'COMPLETED'")
  Integer getTotalScoreByUserId(@Param("userId") Long userId);

  /**
   * 统计用户的总正确题数
   *
   * @param userId 用户ID
   * @return 总正确题数
   */
  @Query(
      "SELECT COALESCE(SUM(ts.correctAnswers), 0) FROM TestSession ts WHERE ts.userId = :userId AND ts.status = 'COMPLETED'")
  Integer getTotalCorrectAnswersByUserId(@Param("userId") Long userId);

  /**
   * 统计用户的总答题数
   *
   * @param userId 用户ID
   * @return 总答题数
   */
  @Query(
      "SELECT COALESCE(SUM(ts.answeredQuestions), 0) FROM TestSession ts WHERE ts.userId = :userId AND ts.status = 'COMPLETED'")
  Integer getTotalAnsweredQuestionsByUserId(@Param("userId") Long userId);

  /**
   * 查找用户的最佳成绩会话
   *
   * @param userId 用户ID
   * @param categoryCode 分类代码
   * @return 最佳成绩会话
   */
  @Query(
      "SELECT ts FROM TestSession ts WHERE ts.userId = :userId AND ts.categoryCode = :categoryCode AND ts.status = 'COMPLETED' ORDER BY ts.totalScore DESC, ts.correctAnswers DESC")
  List<TestSession> findBestScoreSessionsByUserIdAndCategory(
      @Param("userId") Long userId, @Param("categoryCode") String categoryCode);

  /**
   * 统计用户各分类的答题情况
   *
   * @param userId 用户ID
   * @return 各分类答题统计
   */
  @Query(
      "SELECT ts.categoryCode, COUNT(ts), AVG(ts.totalScore), AVG(ts.correctAnswers) "
          + "FROM TestSession ts WHERE ts.userId = :userId AND ts.status = 'COMPLETED' "
          + "GROUP BY ts.categoryCode")
  List<Object[]> getCategoryStatisticsByUserId(@Param("userId") Long userId);

  /**
   * 根据用户ID查找会话列表（按开始时间倒序）
   *
   * @param userId 用户ID
   * @param pageable 分页参数
   * @return 会话列表
   */
  Page<TestSession> findByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);
}
