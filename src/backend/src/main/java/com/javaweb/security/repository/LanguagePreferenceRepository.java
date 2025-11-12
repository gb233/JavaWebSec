package com.javaweb.security.repository;

import com.javaweb.security.entity.LanguagePreference;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 语言偏好数据访问接口
 *
 * @author JavaWeb Security Team
 * @since 1.0.0
 */
@Repository
public interface LanguagePreferenceRepository extends JpaRepository<LanguagePreference, Long> {

  /** 根据用户ID查找活跃的语言偏好 */
  @Query("SELECT lp FROM LanguagePreference lp WHERE lp.userId = :userId AND lp.isActive = true")
  Optional<LanguagePreference> findByUserIdAndActive(@Param("userId") Long userId);

  /** 根据用户ID和语言代码查找偏好 */
  Optional<LanguagePreference> findByUserIdAndLanguageCode(Long userId, String languageCode);

  /** 根据用户ID查找所有语言偏好 */
  List<LanguagePreference> findByUserId(Long userId);

  /** 检查用户是否已有语言偏好设置 */
  boolean existsByUserId(Long userId);
}
