package com.anontion.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;

public class ResponseBodyErrorDTO extends ResponseBodyDTO {

  //@JsonInclude(JsonInclude.Include.NON_NULL) TODO
  @NotNull(message = "Body detail cannot be null.")
  private String detail;
  
  public ResponseBodyErrorDTO() {

  } 

  public ResponseBodyErrorDTO(String detail) {
    
    this.detail = detail;
  }

  public String getDetail() {
    
    return detail;
  }

  public void setDetail(String detail) {
  
    this.detail = detail;
  } 
}
