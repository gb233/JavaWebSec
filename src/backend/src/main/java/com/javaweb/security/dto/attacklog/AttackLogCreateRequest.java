package com.javaweb.security.dto.attacklog;

import com.javaweb.security.entity.AttackLog.RiskLevel;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttackLogCreateRequest {

  @NotNull(message = "用户ID不能为空") private Long userId;

  @NotNull(message = "漏洞ID不能为空") private Long vulnerabilityId;

  @NotBlank(message = "攻击类型不能为空")
  private String attackType;

  @NotBlank(message = "模块不能为空")
  private String module;

  @NotBlank(message = "攻击载荷不能为空")
  private String attackPayload;

  @NotBlank(message = "请求方法不能为空")
  private String requestMethod;

  @NotBlank(message = "请求URL不能为空")
  private String requestUrl;

  @NotBlank(message = "来源IP不能为空")
  private String sourceIp;

  private String requestHeaders;
  private String requestBody;
  private Integer responseStatus;
  private String responseHeaders;
  private String responseBody;
  private Boolean successful;
  private RiskLevel riskLevel;
  private String userAgent;
  private Integer executionTime;
  private String errorMessage;
  private String sessionId;
  private String traceId;
}
