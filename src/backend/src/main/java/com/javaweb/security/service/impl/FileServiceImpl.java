package com.javaweb.security.service.impl;

import com.javaweb.security.service.FileService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务实现类 实现安全的文件上传功能，防止各种安全风险
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

  // 允许的图片MIME类型
  private static final List<String> ALLOWED_MIME_TYPES =
      Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif");

  // 允许的文件扩展名（小写）
  private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

  // 最大文件大小：2MB
  private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

  // 头像存储路径
  @Value("${app.file.avatar.path:uploads/avatars}")
  private String avatarPath;

  // 头像访问URL前缀
  @Value("${app.file.avatar.url-prefix:/api/v1/files/avatars}")
  private String avatarUrlPrefix;

  @Override
  public String uploadAvatar(MultipartFile file, Long userId) {
    log.info(
        "开始上传用户头像：userId={}, filename={}, size={}",
        userId,
        file.getOriginalFilename(),
        file.getSize());

    // 1. 基础验证
    validateFile(file);

    // 2. 验证文件内容（防止恶意文件伪装成图片）
    validateImageContent(file);

    // 3. 生成安全的文件名（使用UUID防止文件名冲突和路径遍历攻击）
    String safeFileName = generateSafeFileName(file.getOriginalFilename());

    // 4. 创建用户专属目录
    Path userAvatarDir = createUserAvatarDirectory(userId);

    // 5. 保存文件（重新读取文件内容，因为验证时已经读取过）
    Path targetPath = userAvatarDir.resolve(safeFileName);
    try {
      // 由于验证时已经读取了输入流，这里需要重新获取
      Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
      log.info("头像上传成功：userId={}, filePath={}", userId, targetPath);
    } catch (IOException e) {
      log.error("头像保存失败：userId={}, error={}", userId, e.getMessage(), e);
      throw new RuntimeException("头像保存失败：" + e.getMessage());
    }

    // 6. 返回访问URL
    return avatarUrlPrefix + "/" + userId + "/" + safeFileName;
  }

  @Override
  public void deleteAvatar(Long userId) {
    Path userAvatarDir = Paths.get(avatarPath, String.valueOf(userId));
    try {
      if (Files.exists(userAvatarDir)) {
        Files.walk(userAvatarDir)
            .sorted((a, b) -> -a.compareTo(b)) // 先删除文件，再删除目录
            .forEach(
                path -> {
                  try {
                    Files.delete(path);
                  } catch (IOException e) {
                    log.warn("删除头像文件失败：path={}, error={}", path, e.getMessage());
                  }
                });
        log.info("删除用户头像目录成功：userId={}", userId);
      }
    } catch (IOException e) {
      log.error("删除用户头像目录失败：userId={}, error={}", userId, e.getMessage(), e);
    }
  }

  @Override
  public String getAvatarUrl(Long userId) {
    Path userAvatarDir = Paths.get(avatarPath, String.valueOf(userId));
    if (!Files.exists(userAvatarDir)) {
      return null;
    }

    try {
      return Files.list(userAvatarDir)
          .filter(Files::isRegularFile)
          .filter(
              path -> {
                String fileName = path.getFileName().toString().toLowerCase();
                return ALLOWED_EXTENSIONS.stream().anyMatch(ext -> fileName.endsWith("." + ext));
              })
          .findFirst()
          .map(
              path -> {
                String fileName = path.getFileName().toString();
                return avatarUrlPrefix + "/" + userId + "/" + fileName;
              })
          .orElse(null);
    } catch (IOException e) {
      log.error("获取头像URL失败：userId={}, error={}", userId, e.getMessage(), e);
      return null;
    }
  }

  /** 验证文件基础信息 */
  private void validateFile(MultipartFile file) {
    // 1. 检查文件是否为空
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("文件不能为空");
    }

    // 2. 检查文件大小
    if (file.getSize() > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("文件大小不能超过2MB");
    }

    // 3. 检查文件名
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || originalFilename.trim().isEmpty()) {
      throw new IllegalArgumentException("文件名不能为空");
    }

    // 4. 检查文件扩展名（防止路径遍历攻击）
    String extension = getFileExtension(originalFilename);
    if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
      throw new IllegalArgumentException("不支持的文件类型，仅支持JPG、PNG、GIF格式");
    }

    // 5. 检查MIME类型（防止伪造Content-Type）
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
      throw new IllegalArgumentException("不支持的文件类型，仅支持JPG、PNG、GIF格式");
    }

    // 6. 检查文件名中是否包含路径遍历字符（防止路径遍历攻击）
    if (originalFilename.contains("..")
        || originalFilename.contains("/")
        || originalFilename.contains("\\")) {
      throw new IllegalArgumentException("文件名包含非法字符");
    }
  }

  /** 验证图片内容（防止恶意文件伪装成图片） */
  private void validateImageContent(MultipartFile file) {
    try {
      // 使用Java ImageIO验证图片内容
      // 注意：MultipartFile的InputStream可能不支持reset，所以我们需要先读取内容
      BufferedImage image = ImageIO.read(file.getInputStream());
      if (image == null) {
        throw new IllegalArgumentException("文件不是有效的图片格式");
      }

      // 验证图片尺寸（防止超大图片导致内存溢出）
      int width = image.getWidth();
      int height = image.getHeight();
      if (width > 5000 || height > 5000) {
        throw new IllegalArgumentException("图片尺寸过大，最大支持5000x5000像素");
      }

      // 验证图片尺寸不能为0
      if (width <= 0 || height <= 0) {
        throw new IllegalArgumentException("图片尺寸无效");
      }
    } catch (IOException e) {
      log.error("图片内容验证失败：error={}", e.getMessage(), e);
      throw new IllegalArgumentException("图片内容验证失败：" + e.getMessage());
    }
  }

  /** 生成安全的文件名（使用UUID + 原始扩展名） */
  private String generateSafeFileName(String originalFilename) {
    String extension = getFileExtension(originalFilename);
    String uuid = UUID.randomUUID().toString().replace("-", "");
    return uuid + "." + extension.toLowerCase();
  }

  /** 获取文件扩展名 */
  private String getFileExtension(String filename) {
    if (filename == null || filename.isEmpty()) {
      return null;
    }
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
      return null;
    }
    return filename.substring(lastDotIndex + 1);
  }

  /** 创建用户头像目录 */
  private Path createUserAvatarDirectory(Long userId) {
    Path userDir = Paths.get(avatarPath, String.valueOf(userId));
    try {
      Files.createDirectories(userDir);
      // 设置目录权限（如果支持）
      if (Files.exists(userDir)) {
        // 在Linux/Unix系统上设置权限为755
        try {
          userDir.toFile().setReadable(true, false);
          userDir.toFile().setWritable(true, true);
          userDir.toFile().setExecutable(true, false);
        } catch (Exception e) {
          log.debug("设置目录权限失败（可能不支持）：{}", e.getMessage());
        }
      }
      return userDir;
    } catch (IOException e) {
      log.error("创建用户头像目录失败：userId={}, error={}", userId, e.getMessage(), e);
      throw new RuntimeException("创建用户头像目录失败：" + e.getMessage());
    }
  }
}
