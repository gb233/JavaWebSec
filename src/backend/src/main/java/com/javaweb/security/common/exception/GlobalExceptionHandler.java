package com.javaweb.security.common.exception;

import com.javaweb.security.common.result.ApiResult;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器
 *
 * <p>统一处理系统中的各种异常，提供友好的错误信息给前端
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  /** 处理业务参数错误 */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResult<Object>> handleIllegalArgumentException(
      IllegalArgumentException e) {
    log.warn("业务参数错误: {}", e.getMessage());
    return ResponseEntity.badRequest().body(ApiResult.failed(e.getMessage()));
  }

  /** 处理数据完整性违反异常 */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiResult<Object>> handleDataIntegrityViolation(
      DataIntegrityViolationException e) {
    log.error("数据完整性错误: {}", e.getMessage());
    String message = "数据验证失败，请检查输入信息";

    // 根据具体的约束违反类型提供更友好的错误信息
    String errorMessage = e.getMessage();
    if (errorMessage != null) {
      if (errorMessage.contains("Duplicate entry")) {
        message = "数据已存在，请检查输入信息";
      } else if (errorMessage.contains("foreign key constraint")) {
        message = "关联数据不存在，请检查输入信息";
      } else if (errorMessage.contains("cannot be null")) {
        message = "必填字段不能为空";
      }
    }

    return ResponseEntity.badRequest().body(ApiResult.failed(message));
  }

  /** 处理请求参数验证异常 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResult<Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    log.warn("请求参数验证失败: {}", e.getMessage());

    StringBuilder errorMessage = new StringBuilder("参数验证失败: ");
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      errorMessage.append(fieldError.getDefaultMessage()).append("; ");
    }

    return ResponseEntity.badRequest().body(ApiResult.failed(errorMessage.toString()));
  }

  /** 处理绑定异常 */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResult<Object>> handleBindException(BindException e) {
    log.warn("数据绑定失败: {}", e.getMessage());

    StringBuilder errorMessage = new StringBuilder("数据绑定失败: ");
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      errorMessage.append(fieldError.getDefaultMessage()).append("; ");
    }

    return ResponseEntity.badRequest().body(ApiResult.failed(errorMessage.toString()));
  }

  /** 处理约束违反异常 */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResult<Object>> handleConstraintViolationException(
      ConstraintViolationException e) {
    log.warn("约束违反: {}", e.getMessage());

    StringBuilder errorMessage = new StringBuilder("数据验证失败: ");
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    for (ConstraintViolation<?> violation : violations) {
      errorMessage.append(violation.getMessage()).append("; ");
    }

    return ResponseEntity.badRequest().body(ApiResult.failed(errorMessage.toString()));
  }

  /** 处理运行时异常 */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResult<Object>> handleRuntimeException(RuntimeException e) {
    log.error("运行时异常: {}", e.getMessage(), e);

    // 检查是否是业务异常（包含特定消息）
    String message = e.getMessage();
    if (message != null && (message.contains("注册失败") || message.contains("登录过程中发生错误"))) {
      // 提取原始错误信息
      String originalMessage = message;
      if (e.getCause() != null) {
        originalMessage = e.getCause().getMessage();
      }
      return ResponseEntity.internalServerError()
          .body(ApiResult.failed(originalMessage != null ? originalMessage : "操作失败，请稍后重试"));
    }

    return ResponseEntity.internalServerError().body(ApiResult.failed("系统内部错误，请稍后重试"));
  }

  /** 处理空指针异常 */
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ApiResult<Object>> handleNullPointerException(NullPointerException e) {
    log.error("空指针异常: {}", e.getMessage(), e);
    return ResponseEntity.internalServerError().body(ApiResult.failed("系统内部错误，请稍后重试"));
  }

  /** 处理通用异常 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResult<Object>> handleGenericException(Exception e) {
    log.error("系统异常: {}", e.getMessage(), e);
    return ResponseEntity.internalServerError().body(ApiResult.failed("系统异常，请稍后重试"));
  }
}
