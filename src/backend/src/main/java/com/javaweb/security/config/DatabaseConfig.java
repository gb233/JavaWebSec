package com.javaweb.security.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库配置类
 *
 * <p>配置数据源、JPA、MyBatis Plus等数据库相关组件
 *
 * <ul>
 *   <li>HikariCP连接池配置
 *   <li>JPA实体管理器配置
 *   <li>MyBatis Plus插件配置
 *   <li>事务管理器配置
 * </ul>
 *
 * @author Java Web Security Team
 * @version 1.0.0
 * @since 2024-09-24
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.javaweb.security.repository",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")
public class DatabaseConfig {

  @Value("${spring.datasource.url}")
  private String jdbcUrl;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  @Value("${spring.jpa.hibernate.ddl-auto:validate}")
  private String ddlAuto;

  @Value("${spring.jpa.show-sql:false}")
  private boolean showSql;

  @Value("${spring.datasource.hikari.auto-commit:false}")
  private boolean hikariAutoCommit;

  /**
   * HikariCP数据源配置
   *
   * <p>使用HikariCP作为连接池，提供高性能的数据库连接管理
   *
   * @return 配置好的HikariDataSource
   */
  @Bean
  @Primary
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();

    // 基本连接信息
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setDriverClassName(driverClassName);

    // 连接池配置
    config.setMaximumPoolSize(20); // 最大连接数
    config.setMinimumIdle(5); // 最小空闲连接数
    config.setConnectionTimeout(30000); // 连接超时时间(30秒)
    config.setIdleTimeout(600000); // 空闲超时时间(10分钟)
    config.setMaxLifetime(1800000); // 连接最大生存时间(30分钟)
    config.setLeakDetectionThreshold(60000); // 连接泄漏检测阈值(60秒)

    // 事务配置遵循spring.datasource.hikari.auto-commit
    config.setAutoCommit(hikariAutoCommit);

    // 连接池名称
    config.setPoolName("SecurityTeachingSystemPool");

    // 连接测试查询
    config.setConnectionTestQuery("SELECT 1");

    // 性能优化配置
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    config.addDataSourceProperty("useLocalSessionState", "true");
    config.addDataSourceProperty("rewriteBatchedStatements", "true");
    config.addDataSourceProperty("cacheResultSetMetadata", "true");
    config.addDataSourceProperty("cacheServerConfiguration", "true");
    config.addDataSourceProperty("maintainTimeStats", "false");

    return new HikariDataSource(config);
  }

  /**
   * JPA实体管理器工厂配置
   *
   * @param dataSource 数据源
   * @return LocalContainerEntityManagerFactoryBean
   */
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

    factory.setDataSource(dataSource);
    factory.setPackagesToScan("com.javaweb.security.entity");

    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setGenerateDdl(false);
    vendorAdapter.setShowSql(showSql);
    factory.setJpaVendorAdapter(vendorAdapter);

    Properties jpaProperties = new Properties();
    // 修复：恢复MySQL数据库方言配置
    jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
    jpaProperties.put("hibernate.hbm2ddl.auto", ddlAuto);
    jpaProperties.put("hibernate.show_sql", showSql);
    jpaProperties.put("hibernate.format_sql", true);
    jpaProperties.put("hibernate.use_sql_comments", true);
    jpaProperties.put("hibernate.jdbc.batch_size", 20);
    jpaProperties.put("hibernate.order_inserts", true);
    jpaProperties.put("hibernate.order_updates", true);
    jpaProperties.put("hibernate.jdbc.batch_versioned_data", true);
    // 让连接提供者负责关闭自动提交，避免Hibernate重新开启
    jpaProperties.put("hibernate.connection.provider_disables_autocommit", true);
    jpaProperties.put("hibernate.query.conventional_java_constants", true);

    factory.setJpaProperties(jpaProperties);

    return factory;
  }

  /**
   * JPA事务管理器配置
   *
   * @param entityManagerFactory 实体管理器工厂
   * @return JPA事务管理器
   */
  @Bean
  public PlatformTransactionManager transactionManager(
      LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
    return transactionManager;
  }

  /**
   * MyBatis Plus拦截器配置
   *
   * <p>配置分页、乐观锁、防全表更新删除等插件
   *
   * @return MyBatis Plus拦截器
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

    // 分页插件
    PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
    paginationInterceptor.setMaxLimit(1000L); // 最大分页限制
    paginationInterceptor.setOverflow(false); // 溢出总页数后是否进行处理
    interceptor.addInnerInterceptor(paginationInterceptor);

    // 乐观锁插件
    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

    // 防全表更新与删除插件
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

    return interceptor;
  }

  /**
   * MyBatis Plus全局配置
   *
   * @return GlobalConfig
   */
  @Bean
  public GlobalConfig globalConfig() {
    GlobalConfig globalConfig = new GlobalConfig();

    // 数据库配置
    GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
    dbConfig.setIdType(com.baomidou.mybatisplus.annotation.IdType.AUTO);
    dbConfig.setTableUnderline(true);
    dbConfig.setCapitalMode(false);
    // Note: setKeyGenerator() method has been removed in newer versions of MyBatis-Plus
    dbConfig.setLogicDeleteField("deleted");
    dbConfig.setLogicDeleteValue("1");
    dbConfig.setLogicNotDeleteValue("0");

    globalConfig.setDbConfig(dbConfig);

    return globalConfig;
  }
}
