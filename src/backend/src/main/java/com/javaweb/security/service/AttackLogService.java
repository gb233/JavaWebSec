package com.javaweb.security.service;

import com.javaweb.security.dto.attacklog.AttackLogCreateRequest;
import com.javaweb.security.dto.attacklog.AttackLogQueryCriteria;
import com.javaweb.security.dto.attacklog.AttackLogResponseDto;
import com.javaweb.security.dto.attacklog.AttackLogStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttackLogService {

  AttackLogResponseDto recordAttack(AttackLogCreateRequest request);

  Page<AttackLogResponseDto> queryAttackLogs(AttackLogQueryCriteria criteria, Pageable pageable);

  AttackLogResponseDto getAttackLog(Long id);

  AttackLogStatsDto getAttackLogStats();
}
