package com.anontion.account.controller;

import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestAccountHeaderDTO {
  
  @NotBlank(message = "originator cannot be null")
  private String originator;

  public RequestAccountHeaderDTO() {
  }

  public RequestAccountHeaderDTO(String originator) {

    this.originator = originator;
  }

  public String getOriginator() {

    return originator;
  }

  public void setOriginator(String originator) {
    
    this.originator = originator;
  }
}
