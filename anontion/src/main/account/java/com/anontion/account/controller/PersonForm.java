package com.anontion.account.controller;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Component
@Validated
public class PersonForm {

  public PersonForm() {
    
  }
  
  @NotNull
  //@Size(max=64)
  private String name;

  public String getName() {
    
    return name;
  }

  public void setName(String name) {
  
    this.name = name;
  }
  
  //@Min(0)
  //private int age;
}

