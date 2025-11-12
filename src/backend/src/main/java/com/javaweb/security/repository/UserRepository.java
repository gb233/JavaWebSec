package com.javaweb.security.repository;

import com.javaweb.security.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问层
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /** 根据用户名查找用户 */
  Optional<User> findByUsername(String username);

  /** 根据邮箱查找用户 */
  Optional<User> findByEmail(String email);

  /** 根据用户名或邮箱查找用户（用于登录） */
  @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
  Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

  /** 检查用户名是否存在 */
  boolean existsByUsername(String username);

  /** 检查邮箱是否存在 */
  boolean existsByEmail(String email);

  /** 根据用户状态查找用户 */
  List<User> findByUserStatus(User.UserStatus userStatus);

  /** 根据用户角色查找用户 */
  List<User> findByUserRole(User.UserRole userRole);

  /** 查找需要解锁的用户（锁定时间已过期） */
  @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil < :now")
  List<User> findUsersToUnlock(@Param("now") LocalDateTime now);

  /** 根据最后登录时间查找不活跃用户 */
  @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate OR u.lastLoginAt IS NULL")
  List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

  /** 分页查询用户 */
  @Query(
      "SELECT u FROM User u WHERE "
          + "(:username IS NULL OR u.username LIKE %:username%) AND "
          + "(:email IS NULL OR u.email LIKE %:email%) AND "
          + "(:userRole IS NULL OR u.userRole = :userRole) AND "
          + "(:userStatus IS NULL OR u.userStatus = :userStatus)")
  Page<User> findUsersWithFilters(
      @Param("username") String username,
      @Param("email") String email,
      @Param("userRole") User.UserRole userRole,
      @Param("userStatus") User.UserStatus userStatus,
      Pageable pageable);

  /** 统计用户数量（按状态） */
  @Query("SELECT u.userStatus, COUNT(u) FROM User u GROUP BY u.userStatus")
  List<Object[]> countUsersByStatus();

  /** 统计用户数量（按角色） */
  @Query("SELECT u.userRole, COUNT(u) FROM User u GROUP BY u.userRole")
  List<Object[]> countUsersByRole();

  /** 统计今日新注册用户 */
  @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE")
  Long countTodayRegistrations();

  /** 统计活跃用户（最近30天有登录） */
  @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt >= :thirtyDaysAgo")
  Long countActiveUsers(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

  /** 更新用户最后登录信息 */
  @Modifying
  @Query(
      "UPDATE User u SET u.lastLoginAt = :loginTime, u.lastLoginIp = :loginIp "
          + "WHERE u.id = :userId")
  void updateLastLoginInfo(
      @Param("userId") Long userId,
      @Param("loginTime") LocalDateTime loginTime,
      @Param("loginIp") String loginIp);

  /** 重置用户登录失败次数 */
  @Modifying
  @Query(
      "UPDATE User u SET u.failedLoginAttempts = 0, u.lockedUntil = NULL " + "WHERE u.id = :userId")
  void resetFailedLoginAttempts(@Param("userId") Long userId);

  /** 增加用户登录失败次数 */
  @Modifying
  @Query(
      "UPDATE User u SET u.failedLoginAttempts = u.failedLoginAttempts + 1, "
          + "u.lockedUntil = CASE WHEN u.failedLoginAttempts + 1 >= 5 THEN :lockUntil ELSE u.lockedUntil END "
          + "WHERE u.id = :userId")
  void incrementFailedLoginAttempts(
      @Param("userId") Long userId, @Param("lockUntil") LocalDateTime lockUntil);

  /** 批量解锁过期的用户账户 */
  @Modifying
  @Query("UPDATE User u SET u.lockedUntil = NULL WHERE u.lockedUntil < :now")
  int unlockExpiredAccounts(@Param("now") LocalDateTime now);

  /** 根据邮箱验证状态查找用户 */
  List<User> findByIsEmailVerified(Boolean isEmailVerified);

  /** 查找最近注册的用户 */
  @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
  Page<User> findRecentUsers(Pageable pageable);

  /** 根据用户名模糊搜索（不区分大小写） */
  @Query(
      "SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
