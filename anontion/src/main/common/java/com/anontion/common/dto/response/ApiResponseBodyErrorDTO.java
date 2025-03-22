package com.anontion.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiResponseBodyErrorDTO extends ApiResponseBodyDTO {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String todo;
  
  public ApiResponseBodyErrorDTO() {

  } 

  public ApiResponseBodyErrorDTO(String todo) {
    
    this.todo = todo;
  }

  public String getTodo() {
    
    return todo;
  }

  public void setTodo(String todo) {
  
    this.todo = todo;
  } 
}
