package com.javaweb.security.repository;

import com.javaweb.security.entity.ChallengeRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 挑战记录Repository
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Repository
public interface ChallengeRecordRepository extends JpaRepository<ChallengeRecord, Long> {

  /** 根据用户ID查找挑战记录 */
  Page<ChallengeRecord> findByUserIdOrderBySubmittedAtDesc(Long userId, Pageable pageable);

  /** 根据用户ID和任务ID查找挑战记录 */
  List<ChallengeRecord> findByUserIdAndTaskIdOrderBySubmittedAtDesc(Long userId, Long taskId);

  /** 统计用户的挑战次数 */
  @Query("SELECT COUNT(r) FROM ChallengeRecord r WHERE r.userId = :userId")
  Long countByUserId(@Param("userId") Long userId);

  /** 统计用户的成功次数 */
  @Query("SELECT COUNT(r) FROM ChallengeRecord r WHERE r.userId = :userId AND r.isCorrect = true")
  Long countCorrectByUserId(@Param("userId") Long userId);

  /** 统计用户的总得分 */
  @Query("SELECT SUM(r.pointsEarned) FROM ChallengeRecord r WHERE r.userId = :userId")
  Integer getTotalPointsByUserId(@Param("userId") Long userId);

  /** 获取用户最近N天的挑战记录 */
  @Query(
      "SELECT r FROM ChallengeRecord r WHERE r.userId = :userId AND r.submittedAt >= :startDate ORDER BY r.submittedAt DESC")
  List<ChallengeRecord> findRecentRecordsByUserId(
      @Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

  /** 获取排行榜数据 */
  @Query(
      "SELECT r.userId, COUNT(r), SUM(r.pointsEarned) FROM ChallengeRecord r "
          + "WHERE r.isCorrect = true GROUP BY r.userId ORDER BY SUM(r.pointsEarned) DESC")
  List<Object[]> getLeaderboardData();

  /** 检查用户是否已完成某个任务 */
  @Query(
      "SELECT COUNT(r) > 0 FROM ChallengeRecord r WHERE r.userId = :userId AND r.taskId = :taskId AND r.isCorrect = true")
  Boolean isTaskCompletedByUser(@Param("userId") Long userId, @Param("taskId") Long taskId);

  /** 获取用户对某个任务的尝试次数 */
  @Query("SELECT COUNT(r) FROM ChallengeRecord r WHERE r.userId = :userId AND r.taskId = :taskId")
  Integer getAttemptCountByUserAndTask(@Param("userId") Long userId, @Param("taskId") Long taskId);
}
