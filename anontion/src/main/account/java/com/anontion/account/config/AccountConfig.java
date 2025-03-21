package com.anontion.account.config;

import org.springframework.context.annotation.Bean;

//import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableLoadTimeWeaving;
//import org.springframework.context.annotation.LoadTimeWeavingConfigurer;
//import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
//import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.anontion.account.service.AccountBean;

//@ PropertySource("")

@Configuration
@EnableWebMvc
public class AccountConfig {

  @Bean
  public AccountBean accountBean() {
    
    return new AccountBean();
  }
}
