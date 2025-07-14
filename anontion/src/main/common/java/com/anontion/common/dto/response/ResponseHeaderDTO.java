package com.anontion.common.dto.response;
  
public class ResponseHeaderDTO {

  private String originator;
  
  private Boolean success;
  
  private Integer code;
  
  private String message;
  
  public ResponseHeaderDTO(Boolean success, Integer code, String message) {

    this.originator = "server";
  
    this.success = success;
    
    this.code   = code;
    
    this.message = message;
  }

  public String getOriginator() {
    
    return originator;
  }

  public void setOriginator(String originator) {
    
    this.originator = originator;
  }

  public Boolean isSuccess() {
    
    return success;
  }

  public void setSuccess(Boolean success) {
    
    this.success = success;
  }

  public Integer getCode() {
    
    return code;
  }

  public void setCode(Integer code) {
    
    this.code = code;
  }

  public String getMessage() {
    
    return message;
  }

  public void setMessage(String message) {
    
    this.message = message;
  }
}

