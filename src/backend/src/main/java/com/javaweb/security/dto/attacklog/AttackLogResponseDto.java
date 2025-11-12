package com.javaweb.security.dto.attacklog;

import com.javaweb.security.entity.AttackLog;
import com.javaweb.security.entity.AttackLog.RiskLevel;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttackLogResponseDto {
  Long id;
  Long userId;
  String username;
  Long vulnerabilityId;
  String attackType;
  String module;
  String attackPayload;
  String requestMethod;
  String requestUrl;
  String requestHeaders;
  String requestBody;
  Integer responseStatus;
  String responseHeaders;
  String responseBody;
  Boolean successful;
  RiskLevel riskLevel;
  String sourceIp;
  String userAgent;
  Integer executionTime;
  String errorMessage;
  String sessionId;
  String traceId;
  LocalDateTime createdAt;

  public static AttackLogResponseDto fromEntity(AttackLog log) {
    return AttackLogResponseDto.builder()
        .id(log.getId())
        .userId(log.getUserId())
        .username(log.getUser() != null ? log.getUser().getUsername() : null)
        .vulnerabilityId(log.getVulnerabilityId())
        .attackType(log.getAttackType())
        .module(log.getModule())
        .attackPayload(log.getAttackPayload())
        .requestMethod(log.getRequestMethod())
        .requestUrl(log.getRequestUrl())
        .requestHeaders(log.getRequestHeaders())
        .requestBody(log.getRequestBody())
        .responseStatus(log.getResponseStatus())
        .responseHeaders(log.getResponseHeaders())
        .responseBody(log.getResponseBody())
        .successful(log.getSuccessful())
        .riskLevel(log.getRiskLevel())
        .sourceIp(log.getSourceIp())
        .userAgent(log.getUserAgent())
        .executionTime(log.getExecutionTime())
        .errorMessage(log.getErrorMessage())
        .sessionId(log.getSessionId())
        .traceId(log.getTraceId())
        .createdAt(log.getCreatedAt())
        .build();
  }
}
