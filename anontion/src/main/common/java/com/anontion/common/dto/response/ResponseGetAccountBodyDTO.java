package com.anontion.common.dto.response;

public class ResponseGetAccountBodyDTO extends ResponseBodyDTO {

  private String sipUsername;

  public ResponseGetAccountBodyDTO() {

  }

  public ResponseGetAccountBodyDTO(String sipUsername) {
    
    this.sipUsername = sipUsername;
  }

  public String getSipUsername() {

    return sipUsername;
  }

  public void setSipUsername(String sipUsername) {
   
    this.sipUsername = sipUsername;
  }
  
}

