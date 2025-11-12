package com.javaweb.security.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页返回结果
 *
 * <p>用于封装分页查询返回的列表数据以及分页元信息，避免前端绑定 {@code Page} 序列化格式。
 *
 * @param <T> 列表元素类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "分页结果")
public class PageResult<T> {

  @Builder.Default
  @Schema(description = "当前页数据列表")
  private List<T> content = Collections.emptyList();

  @Schema(description = "总记录数", example = "120")
  private long totalElements;

  @Schema(description = "总页数", example = "12")
  private int totalPages;

  @Schema(description = "每页大小", example = "10")
  private int size;

  @Schema(description = "当前页号(从0开始)", example = "0")
  private int number;

  @Schema(description = "是否是第一页", example = "true")
  private Boolean first;

  @Schema(description = "是否是最后一页", example = "false")
  private Boolean last;
}
