package com.anontion.common.dto.request;

import org.springframework.stereotype.Component;

import com.anontion.common.misc.AnontionJson;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Component
public class RequestPostConnectionDTO {
  
  @Valid
  @NotNull(message = "Json header cannot be null.")
  private RequestPostConnectionHeaderDTO header;
  
  @Valid
  @NotNull(message = "Json body cannot be null.")
  private RequestPostConnectionBodyDTO body;

  public RequestPostConnectionDTO() {
  }

  public RequestPostConnectionHeaderDTO getHeader() {
    
    return header;
  }

  public void setHeader(RequestPostConnectionHeaderDTO header) {
    
    this.header = header;
  }

  public RequestPostConnectionBodyDTO getBody() {
    
    return body;
  }

  public void setBody(RequestPostConnectionBodyDTO body) {
   
    this.body = body;
  }
  
  public String toString() {
        
    return AnontionJson.o2Json(this);
  }
}

