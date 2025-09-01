package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;

@Component
public class RequestPostConnectionHeaderDTO {
  
  @NotNull(message = "Originator cannot be null.")
  private String originator;

  public RequestPostConnectionHeaderDTO() {
  }

  public RequestPostConnectionHeaderDTO(String originator) {

    this.originator = originator;
  }

  public String getOriginator() {

    return originator;
  }

  public void setOriginator(String originator) {
    
    this.originator = originator;
  }
}
