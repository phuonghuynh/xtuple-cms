package com.xtuple.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.Resource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.xtuple.controller"})
public class WebConfiguration extends WebMvcConfigurerAdapter {

  @Resource
  private Environment environment;

  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("index.html");
  }

  @Bean
  public ViewResolver viewResolver() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setSuffix("");
    return viewResolver;
  }

  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**").addResourceLocations(environment.getProperty("webapp.resource.location"));
  }
}
