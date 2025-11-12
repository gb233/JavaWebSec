package com.javaweb.security.repository;

import com.javaweb.security.entity.ChallengeTask;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 挑战任务Repository
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Repository
public interface ChallengeTaskRepository extends JpaRepository<ChallengeTask, Long> {

  /** 根据分类查找任务 */
  Page<ChallengeTask> findByCategoryCodeAndIsActiveTrue(String categoryCode, Pageable pageable);

  /** 根据难度查找任务 */
  Page<ChallengeTask> findByDifficultyLevelAndIsActiveTrue(
      String difficultyLevel, Pageable pageable);

  /** 根据分类和难度查找任务 */
  Page<ChallengeTask> findByCategoryCodeAndDifficultyLevelAndIsActiveTrue(
      String categoryCode, String difficultyLevel, Pageable pageable);

  /** 统计分类下的任务数量 */
  @Query(
      "SELECT COUNT(t) FROM ChallengeTask t WHERE t.categoryCode = :categoryCode AND t.isActive = true")
  Long countByCategoryCode(@Param("categoryCode") String categoryCode);

  /** 统计难度下的任务数量 */
  @Query(
      "SELECT COUNT(t) FROM ChallengeTask t WHERE t.difficultyLevel = :difficultyLevel AND t.isActive = true")
  Long countByDifficultyLevel(@Param("difficultyLevel") String difficultyLevel);

  /** 获取用户未完成的任务 */
  @Query(
      "SELECT t FROM ChallengeTask t WHERE t.isActive = true AND t.id NOT IN "
          + "(SELECT r.taskId FROM ChallengeRecord r WHERE r.userId = :userId AND r.isCorrect = true)")
  List<ChallengeTask> findUncompletedTasksByUserId(@Param("userId") Long userId);

  /** 获取用户已完成的任务 */
  @Query(
      "SELECT DISTINCT t FROM ChallengeTask t JOIN ChallengeRecord r ON t.id = r.taskId "
          + "WHERE r.userId = :userId AND r.isCorrect = true AND t.isActive = true")
  List<ChallengeTask> findCompletedTasksByUserId(@Param("userId") Long userId);
}
