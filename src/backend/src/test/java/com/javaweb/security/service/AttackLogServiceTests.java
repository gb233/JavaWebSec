package com.javaweb.security.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.javaweb.security.dto.attacklog.AttackLogCreateRequest;
import com.javaweb.security.dto.attacklog.AttackLogQueryCriteria;
import com.javaweb.security.dto.attacklog.AttackLogResponseDto;
import com.javaweb.security.dto.attacklog.AttackLogStatsDto;
import com.javaweb.security.entity.AttackLog.RiskLevel;
import com.javaweb.security.entity.User;
import com.javaweb.security.repository.AttackLogRepository;
import com.javaweb.security.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttackLogServiceTests {

  @Autowired private AttackLogService attackLogService;

  @Autowired private AttackLogRepository attackLogRepository;

  @Autowired private UserRepository userRepository;

  private Long userId;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setUsername("tester");
    user.setEmail("tester@example.com");
    user.setPasswordHash("password");
    userId = userRepository.save(user).getId();
  }

  @Test
  @DisplayName("记录攻击日志后能够查询到对应记录")
  void recordAttackLog() {
    AttackLogCreateRequest request = buildRequest("/api/v1/demo/sql-injection/vulnerable");

    AttackLogResponseDto responseDto = attackLogService.recordAttack(request);

    assertThat(responseDto.getId()).isNotNull();
    assertThat(responseDto.getUserId()).isEqualTo(userId);
    assertThat(responseDto.getModule()).isEqualTo("demo:sql-injection");
    assertThat(responseDto.getAttackType()).isEqualTo("VULNERABLE");
    assertThat(attackLogRepository.count()).isEqualTo(1);
  }

  @Test
  @DisplayName("按照条件筛选攻击日志")
  void queryAttackLogsByCriteria() {
    AttackLogCreateRequest vulnerable = buildRequest("/api/v1/demo/sql-injection/vulnerable");
    attackLogService.recordAttack(vulnerable);

    AttackLogCreateRequest secure = buildRequest("/api/v1/demo/sql-injection/secure");
    secure.setSuccessful(true);
    secure.setRiskLevel(RiskLevel.LOW);
    secure.setAttackType("SECURE");
    attackLogService.recordAttack(secure);

    AttackLogQueryCriteria criteria = new AttackLogQueryCriteria();
    criteria.setModule("demo:sql-injection");
    criteria.setSuccessful(Boolean.TRUE);

    Page<AttackLogResponseDto> page =
        attackLogService.queryAttackLogs(criteria, PageRequest.of(0, 10));

    List<AttackLogResponseDto> logs = page.getContent();
    assertThat(logs).hasSize(1);
    assertThat(logs.get(0).getRiskLevel()).isEqualTo(RiskLevel.LOW);

    AttackLogStatsDto stats = attackLogService.getAttackLogStats();
    assertThat(stats.getTotalCount()).isEqualTo(2);
    assertThat(stats.getSuccessCount()).isEqualTo(1);
    assertThat(stats.getModules()).isNotEmpty();
  }

  private AttackLogCreateRequest buildRequest(String url) {
    AttackLogCreateRequest request = new AttackLogCreateRequest();
    request.setUserId(userId);
    request.setVulnerabilityId(null);
    request.setAttackType("vulnerable");
    request.setModule("demo:sql-injection");
    request.setAttackPayload("payload");
    request.setRequestMethod("GET");
    request.setRequestUrl(url);
    request.setSourceIp("127.0.0.1");
    request.setRequestHeaders(null);
    request.setRequestBody("");
    request.setResponseStatus(200);
    request.setResponseHeaders(null);
    request.setResponseBody("result");
    request.setSuccessful(false);
    request.setRiskLevel(RiskLevel.HIGH);
    request.setUserAgent("JUnit");
    request.setExecutionTime(15);
    request.setSessionId("session-1");
    request.setTraceId("trace-1");
    return request;
  }
}
