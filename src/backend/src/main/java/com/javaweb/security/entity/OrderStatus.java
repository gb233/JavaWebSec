package com.javaweb.security.entity;

/**
 * 订单状态枚举
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-30
 */
public enum OrderStatus {
  PENDING("待处理"),
  PROCESSING("处理中"),
  COMPLETED("已完成"),
  CANCELLED("已取消"),
  REFUNDED("已退款");

  private final String description;

  OrderStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
