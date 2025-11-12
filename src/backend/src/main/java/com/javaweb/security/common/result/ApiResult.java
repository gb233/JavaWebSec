package com.javaweb.security.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应结果包装类
 *
 * <p>用于统一API响应格式，包含状态码、消息、数据等信息
 *
 * @param <T> 数据类型
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一API响应结果")
public class ApiResult<T> {

  @Schema(description = "状态码", example = "200")
  private Integer code;

  @Schema(description = "响应消息", example = "success")
  private String message;

  @Schema(description = "响应数据")
  private T data;

  @Schema(description = "请求路径", example = "/api/v1/users")
  private String path;

  @Schema(description = "时间戳", example = "1695555555555")
  private Long timestamp;

  @Schema(description = "请求ID", example = "req-123456")
  private String requestId;

  // ==================== 静态工厂方法 ====================

  /**
   * 成功响应（无数据）
   *
   * @param <T> 数据类型
   * @return 成功响应
   */
  public static <T> ApiResult<T> success() {
    return ApiResult.<T>builder()
        .code(ResultCode.SUCCESS.getCode())
        .message(ResultCode.SUCCESS.getMessage())
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 成功响应（带消息）
   *
   * @param message 响应消息
   * @param <T> 数据类型
   * @return 成功响应
   */
  public static <T> ApiResult<T> success(String message) {
    return ApiResult.<T>builder()
        .code(ResultCode.SUCCESS.getCode())
        .message(message)
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 成功响应（带数据）
   *
   * @param data 响应数据
   * @param <T> 数据类型
   * @return 成功响应
   */
  public static <T> ApiResult<T> success(T data) {
    return ApiResult.<T>builder()
        .code(ResultCode.SUCCESS.getCode())
        .message(ResultCode.SUCCESS.getMessage())
        .data(data)
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 成功响应（带消息和数据）
   *
   * @param message 响应消息
   * @param data 响应数据
   * @param <T> 数据类型
   * @return 成功响应
   */
  public static <T> ApiResult<T> success(String message, T data) {
    return ApiResult.<T>builder()
        .code(ResultCode.SUCCESS.getCode())
        .message(message)
        .data(data)
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 失败响应（默认错误）
   *
   * @param <T> 数据类型
   * @return 失败响应
   */
  public static <T> ApiResult<T> error() {
    return ApiResult.<T>builder()
        .code(ResultCode.INTERNAL_SERVER_ERROR.getCode())
        .message(ResultCode.INTERNAL_SERVER_ERROR.getMessage())
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 失败响应（带消息）
   *
   * @param message 错误消息
   * @param <T> 数据类型
   * @return 失败响应
   */
  public static <T> ApiResult<T> error(String message) {
    return ApiResult.<T>builder()
        .code(ResultCode.INTERNAL_SERVER_ERROR.getCode())
        .message(message)
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 失败响应（带状态码和消息）
   *
   * @param code 状态码
   * @param message 错误消息
   * @param <T> 数据类型
   * @return 失败响应
   */
  public static <T> ApiResult<T> error(Integer code, String message) {
    return ApiResult.<T>builder()
        .code(code)
        .message(message)
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 失败响应（使用结果码枚举）
   *
   * @param resultCode 结果码枚举
   * @param <T> 数据类型
   * @return 失败响应
   */
  public static <T> ApiResult<T> error(ResultCode resultCode) {
    return ApiResult.<T>builder()
        .code(resultCode.getCode())
        .message(resultCode.getMessage())
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 失败响应（使用结果码枚举和自定义消息）
   *
   * @param resultCode 结果码枚举
   * @param message 自定义消息
   * @param <T> 数据类型
   * @return 失败响应
   */
  public static <T> ApiResult<T> error(ResultCode resultCode, String message) {
    return ApiResult.<T>builder()
        .code(resultCode.getCode())
        .message(message)
        .timestamp(System.currentTimeMillis())
        .build();
  }

  /**
   * 失败响应（别名方法，兼容现有代码）
   *
   * @param message 错误消息
   * @param <T> 数据类型
   * @return 失败响应
   */
  public static <T> ApiResult<T> failed(String message) {
    return error(message);
  }

  /**
   * 未授权响应
   *
   * @param message 错误消息
   * @param <T> 数据类型
   * @return 未授权响应
   */
  public static <T> ApiResult<T> unauthorized(String message) {
    return error(ResultCode.UNAUTHORIZED, message);
  }

  // ==================== 便捷判断方法 ====================

  /**
   * 判断是否成功
   *
   * @return 是否成功
   */
  public boolean isSuccess() {
    return ResultCode.SUCCESS.getCode().equals(this.code);
  }

  /**
   * 判断是否失败
   *
   * @return 是否失败
   */
  public boolean isError() {
    return !isSuccess();
  }

  // ==================== 链式调用方法 ====================

  /**
   * 设置请求路径（链式调用）
   *
   * @param path 请求路径
   * @return 当前对象
   */
  public ApiResult<T> path(String path) {
    this.path = path;
    return this;
  }

  /**
   * 设置请求ID（链式调用）
   *
   * @param requestId 请求ID
   * @return 当前对象
   */
  public ApiResult<T> requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }
}
