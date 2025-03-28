package com.anontion.account.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.anontion.account.service.AccountBean;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.anontion.common, com.anontion.application, com.anontion.account, com.anontion.system")
@PropertySource("classpath:application.properties")
@Import(ValidationConfig.class) 
public class AccountConfig {

  @Bean
  public AccountBean accountBean() {
    
    return new AccountBean();
  }
}

