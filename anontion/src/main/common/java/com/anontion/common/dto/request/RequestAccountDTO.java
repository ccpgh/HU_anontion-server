package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

import com.anontion.common.misc.AnontionJson;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Component
public class RequestAccountDTO {
  
  @Valid
  @NotNull(message = "Json header cannot be null.")
  private RequestAccountHeaderDTO header;
  
  @Valid
  @NotNull(message = "Json body cannot be null.")
  private RequestAccountBodyDTO body;

  public RequestAccountDTO() {
  }

  public RequestAccountHeaderDTO getHeader() {
    
    return header;
  }

  public void setHeader(RequestAccountHeaderDTO header) {
    
    this.header = header;
  }

  public RequestAccountBodyDTO getBody() {
    
    return body;
  }

  public void setBody(RequestAccountBodyDTO body) {
   
    this.body = body;
  }
  
  public String toString() {
        
    return AnontionJson.o2Json(this);
  }
}

