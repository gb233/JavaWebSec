package com.javaweb.security.repository;

import com.javaweb.security.entity.AdminUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 管理员用户Repository
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

  /** 根据用户名查找管理员 */
  Optional<AdminUser> findByUsername(String username);

  /** 根据邮箱查找管理员 */
  Optional<AdminUser> findByEmail(String email);

  /** 根据角色查找管理员 */
  Page<AdminUser> findByRole(String role, Pageable pageable);

  /** 根据状态查找管理员 */
  Page<AdminUser> findByIsActive(Boolean isActive, Pageable pageable);

  /** 根据角色和状态查找管理员 */
  Page<AdminUser> findByRoleAndIsActive(String role, Boolean isActive, Pageable pageable);

  /** 统计活跃管理员数量 */
  @Query("SELECT COUNT(a) FROM AdminUser a WHERE a.isActive = true")
  Long countActiveAdmins();

  /** 获取最近登录的管理员 */
  @Query("SELECT a FROM AdminUser a WHERE a.lastLoginAt >= :since ORDER BY a.lastLoginAt DESC")
  List<AdminUser> findRecentLogins(@Param("since") LocalDateTime since);

  /** 根据创建者查找管理员 */
  Page<AdminUser> findByCreatedBy(String createdBy, Pageable pageable);
}
