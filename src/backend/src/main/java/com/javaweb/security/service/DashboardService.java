package com.javaweb.security.service;

import com.javaweb.security.dto.dashboard.DashboardOverviewDto;

public interface DashboardService {

  /**
   * 获取当前用户仪表盘概览数据
   *
   * @param userId 当前用户ID
   * @return 仪表盘概览
   */
  DashboardOverviewDto getOverview(Long userId);
}
