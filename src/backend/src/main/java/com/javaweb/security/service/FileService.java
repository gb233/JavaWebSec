package com.javaweb.security.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
public interface FileService {

  /**
   * 上传用户头像
   *
   * @param file 头像文件
   * @param userId 用户ID
   * @return 头像访问URL
   * @throws IllegalArgumentException 如果文件验证失败
   */
  String uploadAvatar(MultipartFile file, Long userId);

  /**
   * 删除用户头像
   *
   * @param userId 用户ID
   */
  void deleteAvatar(Long userId);

  /**
   * 获取头像访问URL
   *
   * @param userId 用户ID
   * @return 头像访问URL，如果不存在返回null
   */
  String getAvatarUrl(Long userId);
}
