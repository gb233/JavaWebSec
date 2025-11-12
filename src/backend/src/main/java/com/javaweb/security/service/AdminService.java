package com.javaweb.security.service;

import com.javaweb.security.common.result.PageResult;
import com.javaweb.security.dto.admin.AdminUserDto;
import com.javaweb.security.dto.admin.SystemLogDto;
import java.util.Map;

/**
 * 管理后台服务接口
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
public interface AdminService {

  /** 获取管理员用户列表 */
  PageResult<AdminUserDto> getAdminUsers(String role, Boolean isActive, int page, int size);

  /** 创建管理员用户 */
  AdminUserDto createAdminUser(AdminUserDto adminUserDto);

  /** 更新管理员用户 */
  AdminUserDto updateAdminUser(Long id, AdminUserDto adminUserDto);

  /** 删除管理员用户 */
  void deleteAdminUser(Long id);

  /** 获取系统日志 */
  PageResult<SystemLogDto> getSystemLogs(
      String level, String module, Long userId, int page, int size);

  /** 获取系统统计 */
  Map<String, Object> getSystemStats();

  /** 获取用户统计 */
  Map<String, Object> getUserStats();

  /** 获取学习统计 */
  Map<String, Object> getLearningStats();

  /** 获取测试统计 */
  Map<String, Object> getTestStats();

  /** 获取挑战统计 */
  Map<String, Object> getChallengeStats();

  /** 清理过期日志 */
  void cleanExpiredLogs(int days);
}
