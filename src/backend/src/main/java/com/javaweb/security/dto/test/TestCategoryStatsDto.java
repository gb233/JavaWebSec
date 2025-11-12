package com.javaweb.security.dto.test;

import lombok.Data;
import lombok.experimental.Accessors;

/** 知识测试分类统计 DTO */
@Data
@Accessors(chain = true)
public class TestCategoryStatsDto {

  private String categoryCode;
  private String categoryName;
  private long questionCount;
  private long easyCount;
  private long mediumCount;
  private long hardCount;
  private long otherCount;
  private long totalAttempts;
  private long passCount;
  private double averageScore;
  private double bestScore;
  private double passRate;
}
