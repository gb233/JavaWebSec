package com.javaweb.security.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** 攻击日志实体，记录漏洞演示与挑战过程中的请求详情。 */
@Getter
@Setter
@ToString
@Entity
@Table(
    name = "attack_logs",
    indexes = {
      @Index(name = "idx_attack_logs_user_vuln", columnList = "user_id,vulnerability_id"),
      @Index(name = "idx_attack_logs_attack_type", columnList = "attack_type"),
      @Index(name = "idx_attack_logs_module", columnList = "module"),
      @Index(name = "idx_attack_logs_successful", columnList = "is_successful"),
      @Index(name = "idx_attack_logs_risk_level", columnList = "risk_level"),
      @Index(name = "idx_attack_logs_created_at", columnList = "created_at"),
      @Index(name = "idx_attack_logs_ip_time", columnList = "source_ip,created_at"),
      @Index(name = "idx_attack_logs_session", columnList = "session_id")
    })
@EntityListeners(AuditingEntityListener.class)
public class AttackLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  @ToString.Exclude
  private User user;

  @Column(name = "vulnerability_id")
  private Long vulnerabilityId;

  @Column(name = "module", length = 100, nullable = false)
  private String module = "demo:unknown";

  @Column(name = "attack_type", nullable = false, length = 50)
  private String attackType;

  @Lob
  @Column(name = "attack_payload", nullable = false)
  private String attackPayload;

  @Column(name = "request_method", nullable = false, length = 10)
  private String requestMethod;

  @Column(name = "request_url", nullable = false, length = 500)
  private String requestUrl;

  @Lob
  @Column(name = "request_headers")
  private String requestHeaders;

  @Lob
  @Column(name = "request_body")
  private String requestBody;

  @Column(name = "response_status")
  private Integer responseStatus;

  @Lob
  @Column(name = "response_headers")
  private String responseHeaders;

  @Lob
  @Column(name = "response_body")
  private String responseBody;

  @Column(name = "is_successful", nullable = false)
  private Boolean successful = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "risk_level", nullable = false, length = 10)
  private RiskLevel riskLevel = RiskLevel.MEDIUM;

  @Column(name = "source_ip", nullable = false, length = 45)
  private String sourceIp;

  @Lob
  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "execution_time")
  private Integer executionTime;

  @Lob
  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "session_id", length = 100)
  private String sessionId;

  @Column(name = "trace_id", length = 100)
  private String traceId;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
  }
}
