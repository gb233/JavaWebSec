package com.javaweb.security.config;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * MyBatis配置类
 *
 * <p>用于安全教学系统的混合ORM架构，提供MyBatis支持 主要用于学习进度模块，演示安全和不安全的SQL实现对比
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-25
 */
@Slf4j
@Configuration
@MapperScan(
    basePackages = "com.javaweb.security.mapper.mybatis",
    sqlSessionFactoryRef = "myBatisSqlSessionFactory")
public class MyBatisConfig {

  /**
   * MyBatis SqlSessionFactory配置
   *
   * @param dataSource 数据源
   * @return SqlSessionFactory
   * @throws Exception 配置异常
   */
  @Bean("myBatisSqlSessionFactory")
  public SqlSessionFactory myBatisSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
      throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource);

    // 设置MyBatis配置文件路径
    sessionFactory.setConfigLocation(
        new PathMatchingResourcePatternResolver()
            .getResource("classpath:mybatis/mybatis-config.xml"));

    // 设置Mapper XML文件路径（如果存在）
    try {
      var resources =
          new PathMatchingResourcePatternResolver().getResources("classpath:mapper/mybatis/*.xml");
      if (resources.length > 0) {
        sessionFactory.setMapperLocations(resources);
        log.info("找到 {} 个MyBatis mapper文件", resources.length);
      } else {
        log.info("未找到MyBatis mapper文件，跳过设置");
      }
    } catch (Exception e) {
      // 如果mapper目录不存在，跳过设置
      log.warn("MyBatis mapper目录不存在，跳过设置: {}", e.getMessage());
    }

    return sessionFactory.getObject();
  }
}
