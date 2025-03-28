package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Component
public class RequestApplicationDTO {
  
  @Valid
  @NotNull(message = "Json header cannot be null.")
  private RequestApplicationHeaderDTO header;
  
  @Valid
  @NotNull(message = "Json body cannot be null.")
  private RequestApplicationBodyDTO body;

  public RequestApplicationDTO() {
  }

  public RequestApplicationHeaderDTO getHeader() {
    
    return header;
  }

  public void setHeader(RequestApplicationHeaderDTO header) {
    
    this.header = header;
  }

  public RequestApplicationBodyDTO getBody() {
    
    return body;
  }

  public void setBody(RequestApplicationBodyDTO body) {
   
    this.body = body;
  }
}

