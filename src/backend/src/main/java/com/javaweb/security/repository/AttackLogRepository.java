package com.javaweb.security.repository;

import com.javaweb.security.entity.AttackLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AttackLogRepository
    extends JpaRepository<AttackLog, Long>, JpaSpecificationExecutor<AttackLog> {

  @Query(
      "SELECT a.module AS module, COUNT(a) AS totalCount,"
          + " SUM(CASE WHEN a.successful = true THEN 1 ELSE 0 END) AS successCount"
          + " FROM AttackLog a GROUP BY a.module")
  List<ModuleStatsProjection> findModuleStatistics();

  interface ModuleStatsProjection {
    String getModule();

    Long getTotalCount();

    Long getSuccessCount();
  }
}
