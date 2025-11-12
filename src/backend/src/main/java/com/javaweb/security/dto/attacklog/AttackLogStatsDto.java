package com.javaweb.security.dto.attacklog;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttackLogStatsDto {
  long totalCount;
  long successCount;
  long failureCount;
  List<ModuleStat> modules;

  @Value
  @Builder
  public static class ModuleStat {
    String module;
    long totalCount;
    long successCount;
    long failureCount;
  }
}
