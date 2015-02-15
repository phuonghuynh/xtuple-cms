package com.xtuple.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by phuonghqh on 2/15/15.
 */
@Configuration
public class DbConfiguration {

  @Value("${db.host}")
  private String dbHost;

  @Value("${db.username}")
  private String dbUsername;

  @Value("${db.password}")
  private String dbPassword;

  @Bean
  public Flyway flyway() {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dbHost, dbUsername, dbPassword);
    flyway.migrate();
    return flyway;
  }

//  @Bean
//  @DependsOn("flyway")
//  public DataSource dataSource() {
//    DriverManagerDataSource dataSource = new DriverManagerDataSource();
//    dataSource.setDriverClassName("org.postgresql.Driver");
//    dataSource.setUrl(dbHost);
//    dataSource.setUsername(dbUsername);
//    dataSource.setPassword(dbPassword);
//    return dataSource;
//  }
//
//  @Bean
//  public FactoryBean<JdbcTemplate> jdbcTemplate() {
//    FactoryBean<JdbcTemplate> factory = new FactoryBean<JdbcTemplate>() {
//      public JdbcTemplate getObject() throws Exception {
//        return new JdbcTemplate(dataSource());
//      }
//
//      public Class<?> getObjectType() {
//        return JdbcTemplate.class;
//      }
//
//      public boolean isSingleton() {
//        return false;
//      }
//    };
//    return factory;
//  }
}
