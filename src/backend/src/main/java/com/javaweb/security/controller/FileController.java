package com.javaweb.security.controller;

import com.javaweb.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件访问控制器 提供安全的文件访问功能
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "文件访问", description = "文件访问相关API")
public class FileController {

  private final AuthenticationService authenticationService;

  @Value("${app.file.avatar.path:uploads/avatars}")
  private String avatarPath;

  /**
   * 获取用户头像
   *
   * @param userId 用户ID
   * @param filename 文件名
   * @return 头像文件
   */
  @GetMapping("/avatars/{userId}/{filename}")
  @Operation(summary = "获取用户头像", description = "根据用户ID和文件名获取头像图片")
  public ResponseEntity<byte[]> getAvatar(
      @PathVariable Long userId, @PathVariable String filename) {
    try {
      // 1. 验证文件名安全性（防止路径遍历攻击）
      if (filename == null
          || filename.contains("..")
          || filename.contains("/")
          || filename.contains("\\")) {
        log.warn("非法的文件名：filename={}", filename);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
      }

      // 2. 构建文件路径
      Path filePath = Paths.get(avatarPath, String.valueOf(userId), filename);

      // 3. 验证文件是否存在
      if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
        log.debug("头像文件不存在：userId={}, filename={}", userId, filename);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      // 4. 读取文件内容
      byte[] fileContent = Files.readAllBytes(filePath);

      // 5. 根据文件扩展名设置Content-Type
      String contentType = getContentType(filename);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType(contentType));
      headers.setContentLength(fileContent.length);
      // 设置缓存头，提高性能
      headers.setCacheControl("public, max-age=31536000"); // 缓存1年

      return ResponseEntity.ok().headers(headers).body(fileContent);

    } catch (IOException e) {
      log.error("读取头像文件失败：userId={}, filename={}, error={}", userId, filename, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** 根据文件名获取Content-Type */
  private String getContentType(String filename) {
    String lowerFilename = filename.toLowerCase();
    if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
      return "image/jpeg";
    } else if (lowerFilename.endsWith(".png")) {
      return "image/png";
    } else if (lowerFilename.endsWith(".gif")) {
      return "image/gif";
    }
    return "application/octet-stream";
  }
}
