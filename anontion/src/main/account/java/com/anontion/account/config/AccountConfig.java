package com.anontion.account.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.anontion.account.service.AccountBean;

@Configuration
@EnableWebMvc
public class AccountConfig {

  @Bean
  public AccountBean accountBean() {

    return new AccountBean();
  }

  // data source, security, etc
}
