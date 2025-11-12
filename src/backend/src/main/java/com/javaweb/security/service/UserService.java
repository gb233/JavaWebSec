package com.javaweb.security.service;

import com.javaweb.security.dto.user.*;
import com.javaweb.security.entity.User;
import com.javaweb.security.entity.UserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户服务接口
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
public interface UserService {

  /** 用户注册 */
  UserResponseDto register(UserRegistrationDto registrationDto);

  /** 根据ID查找用户 */
  Optional<UserResponseDto> findById(Long id);

  /** 根据用户名查找用户 */
  Optional<UserResponseDto> findByUsername(String username);

  /** 根据邮箱查找用户 */
  Optional<UserResponseDto> findByEmail(String email);

  /** 根据用户名或邮箱查找用户（用于登录） */
  Optional<User> findByUsernameOrEmail(String identifier);

  /** 更新用户信息 */
  UserResponseDto updateUser(Long userId, UserUpdateDto updateDto);

  /** 修改用户密码 */
  void changePassword(Long userId, String oldPassword, String newPassword);

  // 忘记密码功能暂时注释掉 - 2025-01-15
  /** 重置用户密码 */
  // void resetPassword(String email);

  /** 验证用户邮箱 */
  void verifyEmail(Long userId, String verificationCode);

  /** 发送邮箱验证码 */
  void sendEmailVerificationCode(String email);

  /** 锁定用户账户 */
  void lockUser(Long userId, String reason);

  /** 解锁用户账户 */
  void unlockUser(Long userId);

  /** 禁用用户账户 */
  void disableUser(Long userId);

  /** 启用用户账户 */
  void enableUser(Long userId);

  /** 删除用户（软删除） */
  void deleteUser(Long userId);

  /** 检查用户名是否可用 */
  boolean isUsernameAvailable(String username);

  /** 检查邮箱是否可用 */
  boolean isEmailAvailable(String email);

  /** 分页查询用户 */
  Page<UserResponseDto> findUsers(
      String username,
      String email,
      User.UserRole userRole,
      User.UserStatus userStatus,
      Pageable pageable);

  /** 搜索用户 */
  Page<UserResponseDto> searchUsers(String keyword, Pageable pageable);

  /** 获取用户统计信息 */
  UserStatsDto getUserStats();

  /** 获取用户排行榜 */
  List<UserResponseDto> getUserRanking(String rankType, int limit);

  /** 更新用户最后登录信息 */
  void updateLastLoginInfo(Long userId, String loginIp);

  /** 处理登录失败 */
  void handleLoginFailure(Long userId);

  /** 处理登录成功 */
  void handleLoginSuccess(Long userId);

  /** 解锁过期的账户 */
  int unlockExpiredAccounts();

  /** 获取用户配置文件 */
  Optional<UserProfile> getUserProfile(Long userId);

  /** 实时获取用户详细信息（包含最新统计数据） */
  UserResponseDto getFreshUserProfile(Long userId);

  /** 创建用户配置文件 */
  UserProfile createUserProfile(Long userId);

  /** 更新用户学习统计 */
  void updateUserStudyStats(Long userId, Long additionalTime, Integer additionalPoints);

  /** 增加用户完成的漏洞数量 */
  void incrementUserCompletedVulnerabilities(Long userId);

  /** 增加用户通过的测试数量 */
  void incrementUserPassedTests(Long userId);

  /** 增加用户完成的挑战数量 */
  void incrementUserCompletedChallenges(Long userId);

  /** 增加用户获得的徽章数量 */
  void incrementUserEarnedBadges(Long userId);

  /** 更新用户学习连续天数 */
  void updateUserStreak(Long userId, Integer currentStreak);

  /** 用户统计DTO */
  class UserStatsDto {
    private Long totalUsers;
    private Long activeUsers;
    private Long todayRegistrations;
    private Long verifiedUsers;
    private Long lockedUsers;

    // 构造函数、getter和setter
    public UserStatsDto(
        Long totalUsers,
        Long activeUsers,
        Long todayRegistrations,
        Long verifiedUsers,
        Long lockedUsers) {
      this.totalUsers = totalUsers;
      this.activeUsers = activeUsers;
      this.todayRegistrations = todayRegistrations;
      this.verifiedUsers = verifiedUsers;
      this.lockedUsers = lockedUsers;
    }

    // Getters and Setters
    public Long getTotalUsers() {
      return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
      this.totalUsers = totalUsers;
    }

    public Long getActiveUsers() {
      return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
      this.activeUsers = activeUsers;
    }

    public Long getTodayRegistrations() {
      return todayRegistrations;
    }

    public void setTodayRegistrations(Long todayRegistrations) {
      this.todayRegistrations = todayRegistrations;
    }

    public Long getVerifiedUsers() {
      return verifiedUsers;
    }

    public void setVerifiedUsers(Long verifiedUsers) {
      this.verifiedUsers = verifiedUsers;
    }

    public Long getLockedUsers() {
      return lockedUsers;
    }

    public void setLockedUsers(Long lockedUsers) {
      this.lockedUsers = lockedUsers;
    }
  }
}
