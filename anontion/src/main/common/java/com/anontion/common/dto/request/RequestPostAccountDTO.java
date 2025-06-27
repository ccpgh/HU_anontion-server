package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

import com.anontion.common.misc.AnontionJson;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Component
public class RequestPostAccountDTO {
  
  @Valid
  @NotNull(message = "Json header cannot be null.")
  private RequestPostAccountHeaderDTO header;
  
  @Valid
  @NotNull(message = "Json body cannot be null.")
  private RequestPostAccountBodyDTO body;

  public RequestPostAccountDTO() {
  }

  public RequestPostAccountHeaderDTO getHeader() {
    
    return header;
  }

  public void setHeader(RequestPostAccountHeaderDTO header) {
    
    this.header = header;
  }

  public RequestPostAccountBodyDTO getBody() {
    
    return body;
  }

  public void setBody(RequestPostAccountBodyDTO body) {
   
    this.body = body;
  }
  
  public String toString() {
        
    return AnontionJson.o2Json(this);
  }
}

