package com.javaweb.security.repository;

import com.javaweb.security.entity.SystemLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 系统日志Repository
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

  /** 根据用户ID查找日志 */
  Page<SystemLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  /** 根据用户名查找日志 */
  Page<SystemLog> findByUsernameOrderByCreatedAtDesc(String username, Pageable pageable);

  /** 根据级别查找日志 */
  Page<SystemLog> findByLevelOrderByCreatedAtDesc(String level, Pageable pageable);

  /** 根据模块查找日志 */
  Page<SystemLog> findByModuleOrderByCreatedAtDesc(String module, Pageable pageable);

  /** 根据时间范围查找日志 */
  @Query(
      "SELECT l FROM SystemLog l WHERE l.createdAt >= :startTime AND l.createdAt <= :endTime ORDER BY l.createdAt DESC")
  Page<SystemLog> findByTimeRange(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      Pageable pageable);

  /** 统计各级别日志数量 */
  @Query("SELECT l.level, COUNT(l) FROM SystemLog l GROUP BY l.level")
  List<Object[]> countByLevel();

  /** 统计各模块日志数量 */
  @Query("SELECT l.module, COUNT(l) FROM SystemLog l WHERE l.module IS NOT NULL GROUP BY l.module")
  List<Object[]> countByModule();

  /** 获取最近N天的日志统计 */
  @Query(
      "SELECT DATE(l.createdAt) as date, COUNT(l) as count FROM SystemLog l WHERE l.createdAt >= :since GROUP BY DATE(l.createdAt) ORDER BY date DESC")
  List<Object[]> getDailyLogStats(@Param("since") LocalDateTime since);

  /** 清理过期日志 */
  @Query("DELETE FROM SystemLog l WHERE l.createdAt < :before")
  void deleteLogsBefore(@Param("before") LocalDateTime before);
}
