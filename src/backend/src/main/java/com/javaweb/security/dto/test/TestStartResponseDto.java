package com.javaweb.security.dto.test;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 开始测试响应 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestStartResponseDto {

  private TestRecordDto record;
  private List<TestQuestionDto> questions;
}
