package com.javaweb.security.repository;

import com.javaweb.security.entity.UserInteraction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户交互记录Repository
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

  List<UserInteraction> findByUserIdAndVulnerabilityCode(Long userId, String vulnerabilityCode);

  List<UserInteraction> findByUserIdAndVulnerabilityCodeAndInteractionType(
      Long userId, String vulnerabilityCode, String interactionType);

  boolean existsByUserIdAndVulnerabilityCodeAndInteractionType(
      Long userId, String vulnerabilityCode, String interactionType);

  Long countByUserIdAndVulnerabilityCode(Long userId, String vulnerabilityCode);
}
