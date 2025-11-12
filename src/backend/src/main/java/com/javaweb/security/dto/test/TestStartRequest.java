package com.javaweb.security.dto.test;

import lombok.Data;

/** 开始测试请求 */
@Data
public class TestStartRequest {
  private String testName;
  private String categoryCode;
  private Integer questionCount = 10;
}
