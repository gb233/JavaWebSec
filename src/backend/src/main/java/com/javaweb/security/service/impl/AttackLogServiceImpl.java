package com.javaweb.security.service.impl;

import com.javaweb.security.dto.attacklog.AttackLogCreateRequest;
import com.javaweb.security.dto.attacklog.AttackLogQueryCriteria;
import com.javaweb.security.dto.attacklog.AttackLogResponseDto;
import com.javaweb.security.dto.attacklog.AttackLogStatsDto;
import com.javaweb.security.entity.AttackLog;
import com.javaweb.security.entity.AttackLog.RiskLevel;
import com.javaweb.security.repository.AttackLogRepository;
import com.javaweb.security.repository.AttackLogRepository.ModuleStatsProjection;
import com.javaweb.security.repository.UserRepository;
import com.javaweb.security.service.AttackLogService;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttackLogServiceImpl implements AttackLogService {

  private final AttackLogRepository attackLogRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public AttackLogResponseDto recordAttack(AttackLogCreateRequest request) {
    AttackLog attackLog = new AttackLog();
    attackLog.setUserId(request.getUserId());
    attackLog.setVulnerabilityId(request.getVulnerabilityId());
    attackLog.setAttackType(request.getAttackType().trim().toUpperCase());
    attackLog.setModule(
        StringUtils.hasText(request.getModule())
            ? request.getModule().trim().toLowerCase(Locale.ROOT)
            : "demo:unknown");
    attackLog.setAttackPayload(request.getAttackPayload());
    attackLog.setRequestMethod(request.getRequestMethod().trim().toUpperCase());
    attackLog.setRequestUrl(request.getRequestUrl().trim());
    attackLog.setRequestHeaders(request.getRequestHeaders());
    attackLog.setRequestBody(request.getRequestBody());
    attackLog.setResponseStatus(request.getResponseStatus());
    attackLog.setResponseHeaders(request.getResponseHeaders());
    attackLog.setResponseBody(request.getResponseBody());
    attackLog.setSuccessful(Optional.ofNullable(request.getSuccessful()).orElse(Boolean.FALSE));
    attackLog.setRiskLevel(Optional.ofNullable(request.getRiskLevel()).orElse(RiskLevel.MEDIUM));
    attackLog.setSourceIp(request.getSourceIp().trim());
    attackLog.setUserAgent(request.getUserAgent());
    attackLog.setExecutionTime(request.getExecutionTime());
    attackLog.setErrorMessage(request.getErrorMessage());
    attackLog.setSessionId(request.getSessionId() != null ? request.getSessionId().trim() : null);
    attackLog.setTraceId(request.getTraceId() != null ? request.getTraceId().trim() : null);

    AttackLog saved = attackLogRepository.save(attackLog);
    userRepository.findById(saved.getUserId()).ifPresent(saved::setUser);
    log.info(
        "记录攻击日志：userId={}, vulnerabilityId={}, attackType={}",
        saved.getUserId(),
        saved.getVulnerabilityId(),
        saved.getAttackType());
    return AttackLogResponseDto.fromEntity(saved);
  }

  @Override
  public Page<AttackLogResponseDto> queryAttackLogs(
      AttackLogQueryCriteria criteria, Pageable pageable) {
    Specification<AttackLog> specification = buildSpecification(criteria);
    return attackLogRepository
        .findAll(specification, pageable)
        .map(AttackLogResponseDto::fromEntity);
  }

  @Override
  public AttackLogResponseDto getAttackLog(Long id) {
    AttackLog log =
        attackLogRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("攻击日志不存在: " + id));
    return AttackLogResponseDto.fromEntity(log);
  }

  @Override
  public AttackLogStatsDto getAttackLogStats() {
    long total = attackLogRepository.count();
    long success =
        attackLogRepository.count((root, query, cb) -> cb.isTrue(root.get("successful")));
    long failure = Math.max(total - success, 0);

    java.util.List<ModuleStatsProjection> projections = attackLogRepository.findModuleStatistics();
    java.util.List<AttackLogStatsDto.ModuleStat> modules =
        projections.stream()
            .map(
                p ->
                    AttackLogStatsDto.ModuleStat.builder()
                        .module(p.getModule())
                        .totalCount(p.getTotalCount())
                        .successCount(p.getSuccessCount())
                        .failureCount(Math.max(p.getTotalCount() - p.getSuccessCount(), 0))
                        .build())
            .collect(Collectors.toList());

    return AttackLogStatsDto.builder()
        .totalCount(total)
        .successCount(success)
        .failureCount(failure)
        .modules(modules)
        .build();
  }

  private Specification<AttackLog> buildSpecification(AttackLogQueryCriteria criteria) {
    Specification<AttackLog> spec = Specification.where(null);

    if (criteria.getUserId() != null) {
      spec = spec.and((root, query, cb) -> cb.equal(root.get("userId"), criteria.getUserId()));
    }
    if (criteria.getVulnerabilityId() != null) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.equal(root.get("vulnerabilityId"), criteria.getVulnerabilityId()));
    }
    if (StringUtils.hasText(criteria.getModule())) {
      String module = criteria.getModule().trim().toLowerCase(Locale.ROOT);
      spec = spec.and((root, query, cb) -> cb.equal(root.get("module"), module));
    }
    if (StringUtils.hasText(criteria.getAttackType())) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.equal(root.get("attackType"), criteria.getAttackType().trim().toUpperCase()));
    }
    if (criteria.getSuccessful() != null) {
      spec =
          spec.and((root, query, cb) -> cb.equal(root.get("successful"), criteria.getSuccessful()));
    }
    if (criteria.getRiskLevel() != null) {
      spec =
          spec.and((root, query, cb) -> cb.equal(root.get("riskLevel"), criteria.getRiskLevel()));
    }
    if (StringUtils.hasText(criteria.getSourceIp())) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.like(root.get("sourceIp"), "%" + criteria.getSourceIp().trim() + "%"));
    }
    if (StringUtils.hasText(criteria.getSessionId())) {
      spec =
          spec.and(
              (root, query, cb) -> cb.equal(root.get("sessionId"), criteria.getSessionId().trim()));
    }
    if (StringUtils.hasText(criteria.getTraceId())) {
      spec =
          spec.and(
              (root, query, cb) -> cb.equal(root.get("traceId"), criteria.getTraceId().trim()));
    }
    if (criteria.getStartTime() != null) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getStartTime()));
    }
    if (criteria.getEndTime() != null) {
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getEndTime()));
    }
    if (StringUtils.hasText(criteria.getKeyword())) {
      String likePattern = "%" + criteria.getKeyword().trim() + "%";
      spec =
          spec.and(
              (root, query, cb) ->
                  cb.or(
                      cb.like(root.get("attackPayload"), likePattern),
                      cb.like(root.get("requestUrl"), likePattern),
                      cb.like(root.get("requestBody"), likePattern),
                      cb.like(root.get("responseBody"), likePattern),
                      cb.like(root.get("errorMessage"), likePattern)));
    }

    return spec;
  }
}
