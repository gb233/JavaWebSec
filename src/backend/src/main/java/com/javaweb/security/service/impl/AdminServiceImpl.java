package com.javaweb.security.service.impl;

import com.javaweb.security.common.result.PageResult;
import com.javaweb.security.dto.admin.AdminUserDto;
import com.javaweb.security.dto.admin.SystemLogDto;
import com.javaweb.security.entity.AdminUser;
import com.javaweb.security.entity.SystemLog;
import com.javaweb.security.repository.AdminUserRepository;
import com.javaweb.security.repository.SystemLogRepository;
import com.javaweb.security.service.AdminService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 管理后台服务实现
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final AdminUserRepository adminUserRepository;
  private final SystemLogRepository systemLogRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public PageResult<AdminUserDto> getAdminUsers(String role, Boolean isActive, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<AdminUser> userPage;

    if (role != null && isActive != null) {
      userPage = adminUserRepository.findByRoleAndIsActive(role, isActive, pageable);
    } else if (role != null) {
      userPage = adminUserRepository.findByRole(role, pageable);
    } else if (isActive != null) {
      userPage = adminUserRepository.findByIsActive(isActive, pageable);
    } else {
      userPage = adminUserRepository.findAll(pageable);
    }

    List<AdminUserDto> users =
        userPage.getContent().stream().map(this::convertToDto).collect(Collectors.toList());

    return PageResult.<AdminUserDto>builder()
        .content(users)
        .totalElements(userPage.getTotalElements())
        .totalPages(userPage.getTotalPages())
        .size(userPage.getSize())
        .number(userPage.getNumber())
        .build();
  }

  @Override
  @Transactional
  public AdminUserDto createAdminUser(AdminUserDto adminUserDto) {
    AdminUser adminUser =
        new AdminUser()
            .setUsername(adminUserDto.getUsername())
            .setPassword(passwordEncoder.encode(adminUserDto.getPassword()))
            .setEmail(adminUserDto.getEmail())
            .setFullName(adminUserDto.getFullName())
            .setRole(adminUserDto.getRole())
            .setIsActive(adminUserDto.getIsActive())
            .setCreatedBy(adminUserDto.getCreatedBy());

    adminUser = adminUserRepository.save(adminUser);
    log.info("创建管理员用户: {}", adminUser.getUsername());
    return convertToDto(adminUser);
  }

  @Override
  @Transactional
  public AdminUserDto updateAdminUser(Long id, AdminUserDto adminUserDto) {
    AdminUser adminUser =
        adminUserRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("管理员用户不存在"));

    adminUser
        .setEmail(adminUserDto.getEmail())
        .setFullName(adminUserDto.getFullName())
        .setRole(adminUserDto.getRole())
        .setIsActive(adminUserDto.getIsActive());

    if (adminUserDto.getPassword() != null && !adminUserDto.getPassword().isEmpty()) {
      adminUser.setPassword(passwordEncoder.encode(adminUserDto.getPassword()));
    }

    adminUser = adminUserRepository.save(adminUser);
    log.info("更新管理员用户: {}", adminUser.getUsername());
    return convertToDto(adminUser);
  }

  @Override
  @Transactional
  public void deleteAdminUser(Long id) {
    AdminUser adminUser =
        adminUserRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("管理员用户不存在"));

    adminUserRepository.delete(adminUser);
    log.info("删除管理员用户: {}", adminUser.getUsername());
  }

  @Override
  public PageResult<SystemLogDto> getSystemLogs(
      String level, String module, Long userId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<SystemLog> logPage;

    if (userId != null) {
      logPage = systemLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    } else if (level != null && module != null) {
      // 这里需要自定义查询方法
      logPage = systemLogRepository.findAll(pageable);
    } else if (level != null) {
      logPage = systemLogRepository.findByLevelOrderByCreatedAtDesc(level, pageable);
    } else if (module != null) {
      logPage = systemLogRepository.findByModuleOrderByCreatedAtDesc(module, pageable);
    } else {
      logPage = systemLogRepository.findAll(pageable);
    }

    List<SystemLogDto> logs =
        logPage.getContent().stream().map(this::convertToDto).collect(Collectors.toList());

    return PageResult.<SystemLogDto>builder()
        .content(logs)
        .totalElements(logPage.getTotalElements())
        .totalPages(logPage.getTotalPages())
        .size(logPage.getSize())
        .number(logPage.getNumber())
        .build();
  }

  @Override
  public Map<String, Object> getSystemStats() {
    Map<String, Object> stats = new HashMap<>();

    // 基本统计
    stats.put("totalUsers", 0); // 需要从用户服务获取
    stats.put("totalAdmins", adminUserRepository.count());
    stats.put("activeAdmins", adminUserRepository.countActiveAdmins());
    stats.put("totalLogs", systemLogRepository.count());

    // 日志级别统计
    List<Object[]> levelStats = systemLogRepository.countByLevel();
    Map<String, Long> levelCounts = new HashMap<>();
    for (Object[] stat : levelStats) {
      levelCounts.put((String) stat[0], (Long) stat[1]);
    }
    stats.put("logLevels", levelCounts);

    return stats;
  }

  @Override
  public Map<String, Object> getUserStats() {
    Map<String, Object> stats = new HashMap<>();
    // TODO: 实现用户统计
    return stats;
  }

  @Override
  public Map<String, Object> getLearningStats() {
    Map<String, Object> stats = new HashMap<>();
    // TODO: 实现学习统计
    return stats;
  }

  @Override
  public Map<String, Object> getTestStats() {
    Map<String, Object> stats = new HashMap<>();
    // TODO: 实现测试统计
    return stats;
  }

  @Override
  public Map<String, Object> getChallengeStats() {
    Map<String, Object> stats = new HashMap<>();
    // TODO: 实现挑战统计
    return stats;
  }

  @Override
  @Transactional
  public void cleanExpiredLogs(int days) {
    LocalDateTime before = LocalDateTime.now().minusDays(days);
    systemLogRepository.deleteLogsBefore(before);
    log.info("清理{}天前的过期日志", days);
  }

  private AdminUserDto convertToDto(AdminUser adminUser) {
    return new AdminUserDto()
        .setId(adminUser.getId())
        .setUsername(adminUser.getUsername())
        .setEmail(adminUser.getEmail())
        .setFullName(adminUser.getFullName())
        .setRole(adminUser.getRole())
        .setIsActive(adminUser.getIsActive())
        .setLastLoginAt(adminUser.getLastLoginAt())
        .setCreatedBy(adminUser.getCreatedBy())
        .setCreatedAt(adminUser.getCreatedAt())
        .setUpdatedAt(adminUser.getUpdatedAt());
  }

  private SystemLogDto convertToDto(SystemLog systemLog) {
    return new SystemLogDto()
        .setId(systemLog.getId())
        .setUserId(systemLog.getUserId())
        .setUsername(systemLog.getUsername())
        .setAction(systemLog.getAction())
        .setDescription(systemLog.getDescription())
        .setIpAddress(systemLog.getIpAddress())
        .setUserAgent(systemLog.getUserAgent())
        .setLevel(systemLog.getLevel())
        .setModule(systemLog.getModule())
        .setCreatedAt(systemLog.getCreatedAt());
  }
}
