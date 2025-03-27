package com.anontion.common.dto.response;

  
public class ResponseHeaderDTO {

  private String originator;
  private boolean success;
  private int code;
  private String message;
  
  public ResponseHeaderDTO(boolean success, int code, String message) {

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

  public boolean isSuccess() {
    
    return success;
  }

  public void setSuccess(boolean success) {
    
    this.success = success;
  }

  public int getCode() {
    
    return code;
  }

  public void setCode(int code) {
    
    this.code = code;
  }

  public String getMessage() {
    
    return message;
  }

  public void setMessage(String message) {
    
    this.message = message;
  }
}

