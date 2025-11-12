package com.javaweb.security.dto.admin;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 管理员用户DTO
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class AdminUserDto {

  private Long id;
  private String username;
  private String password;
  private String email;
  private String fullName;
  private String role;
  private Boolean isActive;
  private LocalDateTime lastLoginAt;
  private String createdBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
