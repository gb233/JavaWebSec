package com.javaweb.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务配置类
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Configuration
public class TransactionConfig {

  /** 配置事务模板，禁用自动提交 */
  @Bean
  public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
    TransactionTemplate template = new TransactionTemplate(transactionManager);
    template.setReadOnly(true);
    return template;
  }
}
