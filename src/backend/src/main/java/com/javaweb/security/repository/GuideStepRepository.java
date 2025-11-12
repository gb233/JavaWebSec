package com.javaweb.security.repository;

import com.javaweb.security.entity.GuideStep;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 指引步骤数据访问接口
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Repository
public interface GuideStepRepository extends JpaRepository<GuideStep, Long> {

  /** 根据版本和活跃状态查找指引步骤 */
  @Query(
      "SELECT gs FROM GuideStep gs WHERE gs.guideVersion = :version AND gs.isActive = true ORDER BY gs.orderIndex ASC")
  List<GuideStep> findByVersionAndActiveOrderByOrderIndex(@Param("version") String version);

  /** 查找所有活跃的指引步骤 */
  @Query("SELECT gs FROM GuideStep gs WHERE gs.isActive = true ORDER BY gs.orderIndex ASC")
  List<GuideStep> findAllActiveOrderByOrderIndex();

  /** 根据步骤键查找指引步骤 */
  Optional<GuideStep> findByStepKey(String stepKey);

  /** 检查是否存在指定版本的指引 */
  boolean existsByGuideVersion(String guideVersion);
}
