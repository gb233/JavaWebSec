package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 用户实体类
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(
    name = "users",
    indexes = {
      @Index(name = "idx_username", columnList = "username", unique = true),
      @Index(name = "idx_email", columnList = "email", unique = true),
      @Index(name = "idx_user_status", columnList = "user_status"),
      @Index(name = "idx_created_at", columnList = "created_at")
    })
@EntityListeners(AuditingEntityListener.class)
public class User {

  /** 用户ID（主键，自增） */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  /** 用户名（唯一，3-20字符） */
  @Column(name = "username", nullable = false, unique = true, length = 20)
  private String username;

  /** 邮箱地址（唯一，用于登录和通知） */
  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  /** 密码哈希值（BCrypt加密） */
  @Column(name = "password_hash", nullable = false, length = 60)
  private String passwordHash;

  /** 用户真实姓名 */
  @Column(name = "full_name", length = 50)
  private String fullName;

  /** 用户角色（统一权限模式：所有注册用户拥有相同权限） */
  @Column(name = "user_role", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private UserRole userRole = UserRole.STUDENT;

  /** 用户状态 */
  @Column(name = "user_status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private UserStatus userStatus = UserStatus.ACTIVE;

  /** 邮箱是否已验证 */
  @Column(name = "is_email_verified", nullable = false)
  private Boolean isEmailVerified = false;

  /** 头像URL */
  @Column(name = "avatar_url", length = 200)
  private String avatarUrl;

  /** 个人简介 */
  @Column(name = "bio", length = 500)
  private String bio;

  /** 最后登录时间 */
  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  /** 最后登录IP */
  @Column(name = "last_login_ip", length = 45)
  private String lastLoginIp;

  /** 登录失败次数（用于账户锁定） */
  @Column(name = "failed_login_attempts", nullable = false)
  private Integer failedLoginAttempts = 0;

  /** 账户锁定到期时间 */
  @Column(name = "locked_until")
  private LocalDateTime lockedUntil;

  /** 创建时间（自动设置） */
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** 更新时间（自动更新） */
  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  /** 用户角色枚举 */
  public enum UserRole {
    ADMIN("管理员"),
    TEACHER("教师"),
    STUDENT("学生");

    private final String description;

    UserRole(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  /** 用户状态枚举 */
  public enum UserStatus {
    ACTIVE("正常"),
    INACTIVE("未激活"),
    SUSPENDED("已暂停"),
    BANNED("已封禁");

    private final String description;

    UserStatus(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  /** 检查账户是否被锁定 */
  public boolean isAccountLocked() {
    return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
  }

  /** 检查账户是否启用 */
  public boolean isAccountEnabled() {
    return userStatus == UserStatus.ACTIVE && !isAccountLocked();
  }

  /** 重置登录失败次数 */
  public void resetFailedLoginAttempts() {
    this.failedLoginAttempts = 0;
    this.lockedUntil = null;
  }

  /** 增加登录失败次数 */
  public void incrementFailedLoginAttempts() {
    this.failedLoginAttempts++;
    // 失败5次后锁定账户30分钟
    if (this.failedLoginAttempts >= 5) {
      this.lockedUntil = LocalDateTime.now().plusMinutes(30);
    }
  }
}
