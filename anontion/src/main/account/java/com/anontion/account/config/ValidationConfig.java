package com.anontion.account.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {

  @Bean
  public static Validator validator() {

    LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
    
    factoryBean.setProviderClass(HibernateValidator.class);  
 
    return factoryBean;
  }
}