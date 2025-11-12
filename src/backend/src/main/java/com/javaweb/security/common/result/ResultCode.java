package com.javaweb.security.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * <p>定义系统中所有可能的响应状态码和对应的消息
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Getter
public enum ResultCode {

  // ==================== 成功状态 ====================
  SUCCESS(200, "操作成功"),
  CREATED(201, "创建成功"),
  ACCEPTED(202, "请求已接受"),
  NO_CONTENT(204, "无内容"),

  // ==================== 客户端错误 ====================
  BAD_REQUEST(400, "请求参数错误"),
  UNAUTHORIZED(401, "未认证"),
  FORBIDDEN(403, "权限不足"),
  NOT_FOUND(404, "资源不存在"),
  METHOD_NOT_ALLOWED(405, "请求方法不允许"),
  NOT_ACCEPTABLE(406, "不可接受的请求"),
  REQUEST_TIMEOUT(408, "请求超时"),
  CONFLICT(409, "资源冲突"),
  GONE(410, "资源已删除"),
  UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
  UNPROCESSABLE_ENTITY(422, "请求参数验证失败"),
  TOO_MANY_REQUESTS(429, "请求过于频繁"),

  // ==================== 服务器错误 ====================
  INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
  NOT_IMPLEMENTED(501, "功能未实现"),
  BAD_GATEWAY(502, "网关错误"),
  SERVICE_UNAVAILABLE(503, "服务暂不可用"),
  GATEWAY_TIMEOUT(504, "网关超时"),

  // ==================== 业务错误 (6000-6999) ====================

  // 用户相关错误 (6000-6099)
  USER_NOT_FOUND(6000, "用户不存在"),
  USER_ALREADY_EXISTS(6001, "用户已存在"),
  USER_DISABLED(6002, "用户已被禁用"),
  USER_LOCKED(6003, "用户账户已锁定"),
  USERNAME_OR_PASSWORD_ERROR(6004, "用户名或密码错误"),
  PASSWORD_TOO_WEAK(6005, "密码强度不足"),
  EMAIL_ALREADY_EXISTS(6006, "邮箱已被使用"),
  EMAIL_NOT_VERIFIED(6007, "邮箱未验证"),

  // 认证相关错误 (6100-6199)
  TOKEN_EXPIRED(6100, "令牌已过期"),
  TOKEN_INVALID(6101, "令牌无效"),
  TOKEN_MISSING(6102, "缺少令牌"),
  REFRESH_TOKEN_EXPIRED(6103, "刷新令牌已过期"),
  REFRESH_TOKEN_INVALID(6104, "刷新令牌无效"),
  LOGIN_FAILED(6105, "登录失败"),
  LOGOUT_FAILED(6106, "退出登录失败"),
  SESSION_EXPIRED(6107, "会话已过期"),

  // 权限相关错误 (6200-6299)
  PERMISSION_DENIED(6200, "权限不足"),
  ROLE_NOT_FOUND(6201, "角色不存在"),
  ROLE_ALREADY_EXISTS(6202, "角色已存在"),
  INSUFFICIENT_PRIVILEGES(6203, "权限不足"),

  // 漏洞相关错误 (6300-6399)
  VULNERABILITY_NOT_FOUND(6300, "漏洞内容不存在"),
  VULNERABILITY_CATEGORY_NOT_FOUND(6301, "漏洞分类不存在"),
  VULNERABILITY_ALREADY_EXISTS(6302, "漏洞内容已存在"),
  VULNERABILITY_ACCESS_DENIED(6303, "无权访问该漏洞内容"),

  // 学习相关错误 (6400-6499)
  LEARNING_PROGRESS_NOT_FOUND(6400, "学习进度不存在"),
  LEARNING_CONTENT_LOCKED(6401, "学习内容已锁定"),
  PREREQUISITE_NOT_COMPLETED(6402, "前置条件未完成"),
  LEARNING_TIME_LIMIT_EXCEEDED(6403, "学习时间已超限"),

  // 测试相关错误 (6500-6599)
  TEST_NOT_FOUND(6500, "测试不存在"),
  TEST_ALREADY_COMPLETED(6501, "测试已完成"),
  TEST_TIME_LIMIT_EXCEEDED(6502, "测试时间已超限"),
  TEST_ATTEMPT_LIMIT_EXCEEDED(6503, "测试尝试次数已达上限"),
  QUESTION_NOT_FOUND(6504, "题目不存在"),
  INVALID_ANSWER_FORMAT(6505, "答案格式错误"),

  // 挑战相关错误 (6600-6699)
  CHALLENGE_NOT_FOUND(6600, "挑战不存在"),
  CHALLENGE_NOT_AVAILABLE(6601, "挑战暂未开放"),
  CHALLENGE_ALREADY_COMPLETED(6602, "挑战已完成"),
  CHALLENGE_TIME_LIMIT_EXCEEDED(6603, "挑战时间已超限"),
  CHALLENGE_ATTEMPT_LIMIT_EXCEEDED(6604, "挑战尝试次数已达上限"),
  INVALID_FLAG_FORMAT(6605, "Flag格式错误"),
  FLAG_SUBMISSION_FAILED(6606, "Flag提交失败"),

  // 文件相关错误 (6700-6799)
  FILE_NOT_FOUND(6700, "文件不存在"),
  FILE_TOO_LARGE(6701, "文件大小超出限制"),
  FILE_TYPE_NOT_SUPPORTED(6702, "不支持的文件类型"),
  FILE_UPLOAD_FAILED(6703, "文件上传失败"),
  FILE_DOWNLOAD_FAILED(6704, "文件下载失败"),
  FILE_SCAN_FAILED(6705, "文件安全扫描失败"),
  FILE_VIRUS_DETECTED(6706, "检测到恶意文件"),

  // 系统配置错误 (6800-6899)
  CONFIG_NOT_FOUND(6800, "配置项不存在"),
  CONFIG_VALUE_INVALID(6801, "配置值无效"),
  SYSTEM_MAINTENANCE(6802, "系统正在维护"),
  FEATURE_DISABLED(6803, "功能已禁用"),

  // 数据验证错误 (6900-6999)
  VALIDATION_FAILED(6900, "数据验证失败"),
  REQUIRED_FIELD_MISSING(6901, "必填字段缺失"),
  FIELD_VALUE_INVALID(6902, "字段值无效"),
  FIELD_LENGTH_EXCEEDED(6903, "字段长度超出限制"),
  DUPLICATE_ENTRY(6904, "数据重复"),
  FOREIGN_KEY_CONSTRAINT(6905, "外键约束错误"),
  DATA_INTEGRITY_VIOLATION(6906, "数据完整性违反");

  private final Integer code;
  private final String message;

  ResultCode(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * 根据状态码获取枚举实例
   *
   * @param code 状态码
   * @return 对应的枚举实例，如果不存在则返回null
   */
  public static ResultCode getByCode(Integer code) {
    for (ResultCode resultCode : values()) {
      if (resultCode.code.equals(code)) {
        return resultCode;
      }
    }
    return null;
  }

  /**
   * 判断是否为成功状态码
   *
   * @param code 状态码
   * @return 是否为成功状态码
   */
  public static boolean isSuccess(Integer code) {
    return code != null && code >= 200 && code < 300;
  }

  /**
   * 判断是否为客户端错误状态码
   *
   * @param code 状态码
   * @return 是否为客户端错误状态码
   */
  public static boolean isClientError(Integer code) {
    return code != null && code >= 400 && code < 500;
  }

  /**
   * 判断是否为服务器错误状态码
   *
   * @param code 状态码
   * @return 是否为服务器错误状态码
   */
  public static boolean isServerError(Integer code) {
    return code != null && code >= 500 && code < 600;
  }

  /**
   * 判断是否为业务错误状态码
   *
   * @param code 状态码
   * @return 是否为业务错误状态码
   */
  public static boolean isBusinessError(Integer code) {
    return code != null && code >= 6000 && code < 7000;
  }
}
