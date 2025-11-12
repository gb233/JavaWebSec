package com.javaweb.security.dto.admin;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统日志DTO
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class SystemLogDto {

  private Long id;
  private Long userId;
  private String username;
  private String action;
  private String description;
  private String ipAddress;
  private String userAgent;
  private String level;
  private String module;
  private LocalDateTime createdAt;
}
