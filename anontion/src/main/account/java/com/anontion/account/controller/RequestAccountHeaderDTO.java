package com.anontion.account.controller;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Component
public class RequestAccountHeaderDTO {
  
  @NotNull(message = "Originator cannot be null.")
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
