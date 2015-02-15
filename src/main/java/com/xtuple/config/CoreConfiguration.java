package com.xtuple.config;


import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
@ComponentScan(basePackages = "com.xtuple")
@PropertySources({
  @PropertySource("classpath:xtuple.properties"),
  @PropertySource(value = "classpath:override.properties", ignoreResourceNotFound = true)})
//@EnableCaching(proxyTargetClass = true)
public class CoreConfiguration {

  @Resource
  private Environment environment;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
