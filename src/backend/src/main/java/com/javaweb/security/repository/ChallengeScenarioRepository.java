package com.javaweb.security.repository;

import com.javaweb.security.entity.ChallengeScenario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 挑战场景配置数据访问层
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Repository
public interface ChallengeScenarioRepository extends JpaRepository<ChallengeScenario, Long> {

  /** 查找所有启用的挑战场景 */
  List<ChallengeScenario> findByIsActiveTrue();

  /** 根据难度等级查找启用的挑战场景 */
  List<ChallengeScenario> findByDifficultyLevelAndIsActiveTrue(String difficultyLevel);

  /** 根据场景名称查找挑战场景 */
  Optional<ChallengeScenario> findByScenarioName(String scenarioName);

  /** 根据难度等级统计挑战场景数量 */
  @Query(
      "SELECT COUNT(c) FROM ChallengeScenario c WHERE c.difficultyLevel = :difficultyLevel AND c.isActive = true")
  Long countByDifficultyLevel(@Param("difficultyLevel") String difficultyLevel);

  /** 获取所有难度等级 */
  @Query("SELECT DISTINCT c.difficultyLevel FROM ChallengeScenario c WHERE c.isActive = true")
  List<String> findAllDifficultyLevels();
}
