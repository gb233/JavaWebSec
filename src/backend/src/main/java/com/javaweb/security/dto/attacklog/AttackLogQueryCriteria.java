package com.javaweb.security.dto.attacklog;

import com.javaweb.security.entity.AttackLog.RiskLevel;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class AttackLogQueryCriteria {
  private Long userId;
  private Long vulnerabilityId;
  private String attackType;
  private String module;
  private Boolean successful;
  private RiskLevel riskLevel;
  private String sourceIp;
  private String sessionId;
  private String traceId;
  private String keyword;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime startTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endTime;
}
