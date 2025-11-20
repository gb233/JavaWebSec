package com.javaweb.security.service.impl;

import com.javaweb.security.dto.user.*;
import com.javaweb.security.entity.User;
import com.javaweb.security.entity.UserProfile;
import com.javaweb.security.repository.UserProfileRepository;
import com.javaweb.security.repository.UserRepository;
import com.javaweb.security.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;
  private final PasswordEncoder passwordEncoder;

  // TODO: 注入邮件服务
  // private final EmailService emailService;

  @Override
  @Transactional
  public UserResponseDto register(UserRegistrationDto registrationDto) {
    log.info(
        "用户注册请求：username={}, email={}", registrationDto.getUsername(), registrationDto.getEmail());

    // 验证输入参数
    validateRegistrationDto(registrationDto);

    // 检查用户名和邮箱是否已存在
    if (userRepository.existsByUsername(registrationDto.getUsername())) {
      throw new IllegalArgumentException("用户名已存在");
    }
    if (userRepository.existsByEmail(registrationDto.getEmail())) {
      throw new IllegalArgumentException("邮箱已被注册");
    }

    // 验证密码一致性
    if (!registrationDto.isPasswordMatch()) {
      throw new IllegalArgumentException("两次输入的密码不一致");
    }

    try {
      // 创建用户实体
      User user = new User();
      user.setUsername(registrationDto.getUsername());
      user.setEmail(registrationDto.getEmail());
      user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
      user.setFullName(registrationDto.getFullName());
      user.setBio(registrationDto.getBio());
      user.setUserRole(User.UserRole.STUDENT); // 默认为学生角色
      user.setUserStatus(User.UserStatus.ACTIVE); // 默认激活状态
      user.setIsEmailVerified(false); // 邮箱需要验证

      // 保存用户
      User savedUser = userRepository.save(user);
      log.info("用户注册成功：userId={}, username={}", savedUser.getId(), savedUser.getUsername());

      // 创建用户配置文件并初始化统计信息
      UserProfile profile = createUserProfile(savedUser.getId());

      // 验证profile创建成功
      if (profile == null) {
        log.error("用户 {} 的UserProfile创建失败", savedUser.getId());
        throw new RuntimeException("用户配置文件创建失败");
      }

      // TODO: 发送邮箱验证邮件
      // sendEmailVerificationCode(savedUser.getEmail());

      // 返回包含profile信息的UserResponseDto
      return UserResponseDto.fromEntity(savedUser, profile);

    } catch (Exception e) {
      log.error("用户注册失败：username={}, error={}", registrationDto.getUsername(), e.getMessage(), e);
      throw new RuntimeException("用户注册失败：" + e.getMessage());
    }
  }

  @Override
  @Cacheable(value = "users", key = "#id")
  public Optional<UserResponseDto> findById(Long id) {
    log.debug("查找用户：userId={}", id);
    return userRepository
        .findById(id)
        .map(
            user -> {
              UserProfile profile = userProfileRepository.findByUserId(id).orElse(null);
              return UserResponseDto.fromEntity(user, profile);
            });
  }

  @Override
  @Cacheable(value = "users", key = "'username:' + #username")
  public Optional<UserResponseDto> findByUsername(String username) {
    log.debug("根据用户名查找用户：username={}", username);
    return userRepository
        .findByUsername(username)
        .map(
            user -> {
              UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
              return UserResponseDto.fromEntity(user, profile);
            });
  }

  @Override
  @Cacheable(value = "users", key = "'email:' + #email")
  public Optional<UserResponseDto> findByEmail(String email) {
    log.debug("根据邮箱查找用户：email={}", email);
    return userRepository
        .findByEmail(email)
        .map(
            user -> {
              UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
              return UserResponseDto.fromEntity(user, profile);
            });
  }

  @Override
  public Optional<User> findByUsernameOrEmail(String identifier) {
    log.debug("根据用户名或邮箱查找用户：identifier={}", identifier);
    return userRepository.findByUsernameOrEmail(identifier);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public UserResponseDto updateUser(Long userId, UserUpdateDto updateDto) {
    log.info("更新用户信息：userId={}", userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    // 更新用户基本信息
    if (StringUtils.hasText(updateDto.getEmail())
        && !updateDto.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(updateDto.getEmail())) {
        throw new IllegalArgumentException("邮箱已被其他用户使用");
      }
      user.setEmail(updateDto.getEmail());
      user.setIsEmailVerified(false); // 邮箱变更后需要重新验证
    }

    // 更新真实姓名（允许设置为空字符串）
    if (updateDto.getFullName() != null) {
      user.setFullName(updateDto.getFullName());
    }

    // 更新个人简介（允许设置为空字符串）
    if (updateDto.getBio() != null) {
      user.setBio(updateDto.getBio());
    }

    if (StringUtils.hasText(updateDto.getAvatarUrl())) {
      user.setAvatarUrl(updateDto.getAvatarUrl());
    }

    // 保存用户信息
    User savedUser = userRepository.save(user);

    // 更新用户配置文件
    UserProfile profile =
        userProfileRepository.findByUserId(userId).orElseGet(() -> createUserProfile(userId));

    updateUserProfile(profile, updateDto);
    UserProfile savedProfile = userProfileRepository.save(profile);

    log.info("用户信息更新成功：userId={}", userId);
    return UserResponseDto.fromEntity(savedUser, savedProfile);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public void changePassword(Long userId, String oldPassword, String newPassword) {
    log.info("修改用户密码：userId={}", userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    // 验证旧密码
    if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
      throw new IllegalArgumentException("原密码不正确");
    }

    // 验证新密码格式
    validatePassword(newPassword);

    // 更新密码
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    log.info("用户密码修改成功：userId={}", userId);
  }

  // 忘记密码功能暂时注释掉 - 2025-01-15
  // @Override
  // @Transactional
  // public void resetPassword(String email) {
  //   log.info("重置用户密码：email={}", email);

  //   User user =
  //       userRepository.findByEmail(email).orElseThrow(() -> new
  // IllegalArgumentException("邮箱未注册"));

  //   // 生成临时密码
  //   String tempPassword = generateTempPassword();
  //   user.setPasswordHash(passwordEncoder.encode(tempPassword));
  //   userRepository.save(user);

  //   // TODO: 发送临时密码邮件
  //   // emailService.sendPasswordResetEmail(email, tempPassword);

  //   log.info("用户密码重置成功：email={}", email);
  // }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public void verifyEmail(Long userId, String verificationCode) {
    log.info("验证用户邮箱：userId={}, code={}", userId, verificationCode);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    // TODO: 验证验证码的有效性
    // if (!emailService.verifyEmailCode(user.getEmail(), verificationCode)) {
    //     throw new IllegalArgumentException("验证码无效或已过期");
    // }

    user.setIsEmailVerified(true);
    userRepository.save(user);

    log.info("用户邮箱验证成功：userId={}", userId);
  }

  @Override
  public void sendEmailVerificationCode(String email) {
    log.info("发送邮箱验证码：email={}", email);

    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("邮箱未注册"));

    if (user.getIsEmailVerified()) {
      throw new IllegalArgumentException("邮箱已验证");
    }

    // TODO: 生成并发送验证码
    // String code = emailService.generateVerificationCode();
    // emailService.sendEmailVerificationCode(email, code);

    log.info("邮箱验证码发送成功：email={}", email);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public void lockUser(Long userId, String reason) {
    log.info("锁定用户账户：userId={}, reason={}", userId, reason);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    user.setLockedUntil(LocalDateTime.now().plusHours(24)); // 锁定24小时
    userRepository.save(user);

    log.info("用户账户锁定成功：userId={}", userId);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public void unlockUser(Long userId) {
    log.info("解锁用户账户：userId={}", userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    user.setLockedUntil(null);
    user.setFailedLoginAttempts(0);
    userRepository.save(user);

    log.info("用户账户解锁成功：userId={}", userId);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public void disableUser(Long userId) {
    log.info("禁用用户账户：userId={}", userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    user.setUserStatus(User.UserStatus.SUSPENDED);
    userRepository.save(user);

    log.info("用户账户禁用成功：userId={}", userId);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public void enableUser(Long userId) {
    log.info("启用用户账户：userId={}", userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    user.setUserStatus(User.UserStatus.ACTIVE);
    userRepository.save(user);

    log.info("用户账户启用成功：userId={}", userId);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#userId")
  public void deleteUser(Long userId) {
    log.info("删除用户：userId={}", userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    user.setUserStatus(User.UserStatus.BANNED);
    userRepository.save(user);

    log.info("用户删除成功：userId={}", userId);
  }

  @Override
  public boolean isUsernameAvailable(String username) {
    return !userRepository.existsByUsername(username);
  }

  @Override
  public boolean isEmailAvailable(String email) {
    return !userRepository.existsByEmail(email);
  }

  @Override
  public Page<UserResponseDto> findUsers(
      String username,
      String email,
      User.UserRole userRole,
      User.UserStatus userStatus,
      Pageable pageable) {
    log.debug(
        "分页查询用户：username={}, email={}, role={}, status={}", username, email, userRole, userStatus);

    return userRepository
        .findUsersWithFilters(username, email, userRole, userStatus, pageable)
        .map(UserResponseDto::fromEntity);
  }

  @Override
  public Page<UserResponseDto> searchUsers(String keyword, Pageable pageable) {
    log.debug("搜索用户：keyword={}", keyword);

    return userRepository.searchUsers(keyword, pageable).map(UserResponseDto::fromEntity);
  }

  @Override
  public UserStatsDto getUserStats() {
    log.debug("获取用户统计信息");

    // 暂时返回默认值，避免事务问题
    // TODO: 实现混合ORM架构后，使用不同框架处理不同模块
    return new UserStatsDto(0L, 0L, 0L, 0L, 0L);
  }

  @Override
  public List<UserResponseDto> getUserRanking(String rankType, int limit) {
    log.debug("获取用户排行榜：rankType={}, limit={}", rankType, limit);

    List<UserProfile> profiles;
    switch (rankType.toLowerCase()) {
      case "points":
        profiles = userProfileRepository.findTopByPoints(limit);
        break;
      case "studytime":
        profiles = userProfileRepository.findTopByStudyTime(limit);
        break;
      case "vulnerabilities":
        profiles = userProfileRepository.findTopByCompletedVulnerabilities(limit);
        break;
      case "streak":
        profiles = userProfileRepository.findTopByCurrentStreak(limit);
        break;
      default:
        throw new IllegalArgumentException("不支持的排行榜类型: " + rankType);
    }

    return profiles.stream()
        .map(
            profile -> {
              User user = userRepository.findById(profile.getUserId()).orElse(null);
              return user != null ? UserResponseDto.fromEntity(user, profile) : null;
            })
        .filter(dto -> dto != null)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateLastLoginInfo(Long userId, String loginIp) {
    log.debug("更新用户最后登录信息：userId={}, ip={}", userId, loginIp);
    userRepository.updateLastLoginInfo(userId, LocalDateTime.now(), loginIp);
  }

  @Override
  @Transactional
  public void handleLoginFailure(Long userId) {
    log.info("处理用户登录失败：userId={}", userId);
    LocalDateTime lockUntil = LocalDateTime.now().plusMinutes(30);
    userRepository.incrementFailedLoginAttempts(userId, lockUntil);
  }

  @Override
  @Transactional
  public void handleLoginSuccess(Long userId) {
    log.info("处理用户登录成功：userId={}", userId);
    userRepository.resetFailedLoginAttempts(userId);
  }

  @Override
  @Transactional
  public int unlockExpiredAccounts() {
    log.info("解锁过期的用户账户");
    return userRepository.unlockExpiredAccounts(LocalDateTime.now());
  }

  @Override
  public Optional<UserProfile> getUserProfile(Long userId) {
    return userProfileRepository.findByUserId(userId);
  }

  @Override
  public UserResponseDto getFreshUserProfile(Long userId) {
    if (userId == null) {
      throw new IllegalArgumentException("用户ID不能为空");
    }

    log.debug("实时获取用户详细信息：userId={}", userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    UserProfile profile =
        userProfileRepository.findByUserId(userId).orElseGet(() -> createUserProfile(userId));

    return UserResponseDto.fromEntity(user, profile);
  }

  @Override
  @Transactional
  public UserProfile createUserProfile(Long userId) {
    log.info("创建用户配置文件：userId={}", userId);

    // 先查询是否存在
    Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);
    if (existingProfile.isPresent()) {
      return existingProfile.get();
    }

    // 不存在则创建，如果因为并发导致唯一约束违反，重新查询
    try {
      UserProfile profile = new UserProfile();
      profile.setUserId(userId);
      initializeDefaultProfileValues(profile);
      return userProfileRepository.save(profile);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      // 并发创建导致唯一约束违反，重新查询并返回现有记录
      log.debug("并发创建UserProfile，重新查询: userId={}", userId);
      return userProfileRepository
          .findByUserId(userId)
          .orElseThrow(() -> new RuntimeException("创建用户配置文件失败，且重新查询也未找到: userId=" + userId, e));
    }
  }

  private void initializeDefaultProfileValues(UserProfile profile) {
    profile.setSkillLevel(UserProfile.SkillLevel.BEGINNER);
    profile.setTimezone("Asia/Shanghai");
    profile.setPreferredLanguage("zh-CN");

    // 修复：显式设置所有数值字段的默认值，确保数据库插入成功
    profile.setTotalStudyTime(0L);
    profile.setTotalPoints(0);
    profile.setCompletedVulnerabilities(0);
    profile.setPassedTests(0);
    profile.setCompletedChallenges(0);
    profile.setEarnedBadges(0);
    profile.setCurrentStreak(0);
    profile.setLongestStreak(0);
  }

  @Override
  @Transactional
  public void updateUserStudyStats(Long userId, Long additionalTime, Integer additionalPoints) {
    log.debug("更新用户学习统计：userId={}, time={}, points={}", userId, additionalTime, additionalPoints);
    userProfileRepository.updateStudyStats(userId, additionalTime, additionalPoints);
  }

  @Override
  @Transactional
  public void incrementUserCompletedVulnerabilities(Long userId) {
    log.debug("增加用户完成漏洞数量：userId={}", userId);
    userProfileRepository.incrementCompletedVulnerabilities(userId);
  }

  @Override
  @Transactional
  public void incrementUserPassedTests(Long userId) {
    log.debug("增加用户通过测试数量：userId={}", userId);
    userProfileRepository.incrementPassedTests(userId);
  }

  @Override
  @Transactional
  public void incrementUserCompletedChallenges(Long userId) {
    log.debug("增加用户完成挑战数量：userId={}", userId);
    userProfileRepository.incrementCompletedChallenges(userId);
  }

  @Override
  @Transactional
  public void incrementUserEarnedBadges(Long userId) {
    log.debug("增加用户获得徽章数量：userId={}", userId);
    userProfileRepository.incrementEarnedBadges(userId);
  }

  @Override
  @Transactional
  public void updateUserStreak(Long userId, Integer currentStreak) {
    log.debug("更新用户学习连续天数：userId={}, streak={}", userId, currentStreak);
    userProfileRepository.updateStreak(userId, currentStreak);
  }

  // ===========================
  // 私有辅助方法
  // ===========================

  private void validateRegistrationDto(UserRegistrationDto dto) {
    if (!StringUtils.hasText(dto.getUsername())) {
      throw new IllegalArgumentException("用户名不能为空");
    }
    if (!StringUtils.hasText(dto.getEmail())) {
      throw new IllegalArgumentException("邮箱不能为空");
    }
    if (!StringUtils.hasText(dto.getPassword())) {
      throw new IllegalArgumentException("密码不能为空");
    }

    validatePassword(dto.getPassword());
  }

  private void validatePassword(String password) {
    if (password.length() < 8 || password.length() > 32) {
      throw new IllegalArgumentException("密码长度必须在8-32字符之间");
    }
    if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
      throw new IllegalArgumentException("密码必须包含字母和数字");
    }
  }

  private String generateTempPassword() {
    return UUID.randomUUID().toString().substring(0, 12);
  }

  private void updateUserProfile(UserProfile profile, UserUpdateDto updateDto) {
    if (updateDto.getSkillLevel() != null) {
      profile.setSkillLevel(updateDto.getSkillLevel());
    }
    // 允许设置为空字符串
    if (updateDto.getLearningGoals() != null) {
      profile.setLearningGoals(updateDto.getLearningGoals());
    }
    // 允许设置为空字符串
    if (updateDto.getProfessionalBackground() != null) {
      profile.setProfessionalBackground(updateDto.getProfessionalBackground());
    }
    if (updateDto.getYearsOfExperience() != null) {
      profile.setYearsOfExperience(updateDto.getYearsOfExperience());
    }
    if (updateDto.getBirthDate() != null) {
      profile.setBirthDate(updateDto.getBirthDate());
    }
    if (updateDto.getGender() != null) {
      profile.setGender(updateDto.getGender());
    }
    // 允许设置为空字符串
    if (updateDto.getCountry() != null) {
      profile.setCountry(updateDto.getCountry());
    }
    // 允许设置为空字符串
    if (updateDto.getCity() != null) {
      profile.setCity(updateDto.getCity());
    }
    // 允许设置为空字符串
    if (updateDto.getTimezone() != null) {
      profile.setTimezone(updateDto.getTimezone());
    }
    // 允许设置为空字符串
    if (updateDto.getPreferredLanguage() != null) {
      profile.setPreferredLanguage(updateDto.getPreferredLanguage());
    }
    if (updateDto.getEmailNotifications() != null) {
      profile.setEmailNotifications(updateDto.getEmailNotifications());
    }
    if (updateDto.getLearningReminders() != null) {
      profile.setLearningReminders(updateDto.getLearningReminders());
    }
  }
}
