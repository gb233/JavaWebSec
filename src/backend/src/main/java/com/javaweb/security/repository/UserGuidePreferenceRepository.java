package com.javaweb.security.repository;

import com.javaweb.security.entity.UserGuidePreference;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户指引偏好数据访问接口
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Repository
public interface UserGuidePreferenceRepository extends JpaRepository<UserGuidePreference, Long> {

  /** 根据用户ID查找指引偏好 */
  Optional<UserGuidePreference> findByUserId(Long userId);

  /** 检查用户是否已完成初始指引 */
  @Query(
      "SELECT ugp.hasCompletedInitialGuide FROM UserGuidePreference ugp WHERE ugp.userId = :userId")
  Optional<Boolean> findCompletedInitialGuideByUserId(@Param("userId") Long userId);

  /** 检查用户是否需要显示指引 */
  @Query(
      "SELECT ugp FROM UserGuidePreference ugp WHERE ugp.userId = :userId AND ugp.autoShowGuide = true")
  Optional<UserGuidePreference> findUserNeedingGuide(@Param("userId") Long userId);
}
