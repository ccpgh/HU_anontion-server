package com.anontion.account.controller;

import org.springframework.stereotype.Component;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Component
public class PersonForm {

  public PersonForm() {
    
  }
  
  @NotBlank
  private String name;

  @NotBlank
  public String getName() {
    
    return name;
  }

  @NotBlank
  public void setName(String name) {
  
    this.name = name;
  }
}

