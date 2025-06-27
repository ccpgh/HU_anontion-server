package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Component
public class RequestPostApplicationDTO {
  
  @Valid
  @NotNull(message = "Json header cannot be null.")
  private RequestPostApplicationHeaderDTO header;
  
  @Valid
  @NotNull(message = "Json body cannot be null.")
  private RequestPostApplicationBodyDTO body;

  public RequestPostApplicationDTO() {
  }

  public RequestPostApplicationHeaderDTO getHeader() {
    
    return header;
  }

  public void setHeader(RequestPostApplicationHeaderDTO header) {
    
    this.header = header;
  }

  public RequestPostApplicationBodyDTO getBody() {
    
    return body;
  }

  public void setBody(RequestPostApplicationBodyDTO body) {
   
    this.body = body;
  }
}

