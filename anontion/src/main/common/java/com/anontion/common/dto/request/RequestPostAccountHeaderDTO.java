package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;

@Component
public class RequestPostAccountHeaderDTO {
  
  @NotNull(message = "Originator cannot be null.")
  private String originator;

  public RequestPostAccountHeaderDTO() {
  }

  public RequestPostAccountHeaderDTO(String originator) {

    this.originator = originator;
  }

  public String getOriginator() {

    return originator;
  }

  public void setOriginator(String originator) {
    
    this.originator = originator;
  }
}
