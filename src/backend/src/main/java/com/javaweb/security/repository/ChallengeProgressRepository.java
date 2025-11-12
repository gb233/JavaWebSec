package com.javaweb.security.repository;

import com.javaweb.security.entity.ChallengeProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 挑战进度数据访问层
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Repository
public interface ChallengeProgressRepository extends JpaRepository<ChallengeProgress, Long> {

  /** 根据用户ID和场景ID查找挑战进度 */
  Optional<ChallengeProgress> findByUserIdAndScenarioId(Long userId, Long scenarioId);

  /** 查找用户已完成的挑战进度 */
  List<ChallengeProgress> findByUserIdAndIsCompletedTrue(Long userId);

  /** 查找场景的完成记录 */
  List<ChallengeProgress> findByScenarioIdAndIsCompletedTrue(Long scenarioId);

  /** 统计用户完成的挑战数量 */
  @Query(
      "SELECT COUNT(cp) FROM ChallengeProgress cp WHERE cp.userId = :userId AND cp.isCompleted = true")
  Long countCompletedByUserId(@Param("userId") Long userId);

  /** 统计场景的完成次数 */
  @Query(
      "SELECT COUNT(cp) FROM ChallengeProgress cp WHERE cp.scenarioId = :scenarioId AND cp.isCompleted = true")
  Long countCompletedByScenarioId(@Param("scenarioId") Long scenarioId);

  /** 获取用户的挑战进度统计 */
  @Query(
      "SELECT "
          + "COUNT(cp) as totalAttempts, "
          + "COUNT(CASE WHEN cp.isCompleted = true THEN 1 END) as completedCount, "
          + "AVG(cp.progressPercentage) as averageProgress "
          + "FROM ChallengeProgress cp WHERE cp.userId = :userId")
  Object[] getUserProgressStats(@Param("userId") Long userId);
}
