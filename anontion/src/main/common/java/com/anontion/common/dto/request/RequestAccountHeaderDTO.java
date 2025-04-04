package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

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
