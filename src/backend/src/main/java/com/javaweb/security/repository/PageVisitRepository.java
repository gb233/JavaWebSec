package com.javaweb.security.repository;

import com.javaweb.security.entity.PageVisit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 页面访问记录Repository
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Repository
public interface PageVisitRepository extends JpaRepository<PageVisit, Long> {

  List<PageVisit> findByUserIdAndVulnerabilityCode(Long userId, String vulnerabilityCode);

  List<PageVisit> findByUserIdAndVulnerabilityCodeAndPageType(
      Long userId, String vulnerabilityCode, String pageType);

  @Query(
      "SELECT pv FROM PageVisit pv WHERE pv.userId = :userId AND pv.vulnerabilityCode = :vulnerabilityCode ORDER BY pv.visitTime DESC")
  List<PageVisit> findLatestByUserIdAndVulnerabilityCode(
      @Param("userId") Long userId, @Param("vulnerabilityCode") String vulnerabilityCode);
}
