package com.anontion.common.dto.response;

import jakarta.validation.constraints.NotNull;

public class ResponseBodyOkDTO extends ResponseBodyDTO {

  //@JsonInclude(JsonInclude.Include.NON_NULL) TODO
  @NotNull(message = "Body detail cannot be null.")
  private String detail;
  
  public ResponseBodyOkDTO() {

  } 

  public ResponseBodyOkDTO(String detail) {
    
    this.detail = detail;
  }

  public String getDetail() {
    
    return detail;
  }

  public void setDetail(String detail) {
  
    this.detail = detail;
  } 
}


