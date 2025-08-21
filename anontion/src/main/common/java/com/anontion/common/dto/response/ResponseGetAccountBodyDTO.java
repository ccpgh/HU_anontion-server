package com.anontion.common.dto.response;

public class ResponseGetAccountBodyDTO extends ResponseBodyDTO {

  private String serverPub;
  
  private String sipUsername;

  public ResponseGetAccountBodyDTO() {

  }

  public ResponseGetAccountBodyDTO(String serverPub,
      String sipUsername) {
    
    this.serverPub = serverPub;
    this.sipUsername = sipUsername;
  }

  public String getServerPub() {
  
    return serverPub;
  }

  public void setServerPub(String serverPub) {
  
    this.serverPub = serverPub;
  }
  
  public String getSipUsername() {

    return sipUsername;
  }

  public void setSipUsername(String sipUsername) {
   
    this.sipUsername = sipUsername;
  }
  
}

