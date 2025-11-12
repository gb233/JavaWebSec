package com.javaweb.security.repository;

import com.javaweb.security.entity.UserActivity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户活动记录Repository
 *
 * @author JavaWeb Security Team
 * @since 2024-01-15
 */
@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

  List<UserActivity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  List<UserActivity> findByUserIdAndActivityTypeOrderByCreatedAtDesc(
      Long userId, UserActivity.ActivityType activityType, Pageable pageable);

  @Query(
      "SELECT ua.activityType, COUNT(ua) FROM UserActivity ua WHERE ua.userId = :userId GROUP BY ua.activityType")
  List<Object[]> countByUserIdAndActivityType(@Param("userId") Long userId);

  @Query(
      "SELECT DATE(ua.createdAt), COUNT(ua) FROM UserActivity ua WHERE ua.userId = :userId AND ua.createdAt >= :startDate GROUP BY DATE(ua.createdAt)")
  List<Object[]> countByUserIdAndCreatedAtAfter(
      @Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

  List<UserActivity> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime dateTime);

  List<UserActivity> findByUserIdAndActivityType(
      Long userId, UserActivity.ActivityType activityType);
}
