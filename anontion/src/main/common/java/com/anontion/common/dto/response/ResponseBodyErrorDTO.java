package com.anontion.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseBodyErrorDTO extends ResponseBodyDTO {

  @JsonInclude(JsonInclude.Include.ALWAYS)
  private String detail;
  
  public ResponseBodyErrorDTO() {

  } 

  public ResponseBodyErrorDTO(String detail) {
    
    this.detail = detail;
  }

  public String getTodo() {
    
    return detail;
  }

  public void setTodo(String detail) {
  
    this.detail = detail;
  } 
}
