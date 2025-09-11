package com.anontion.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponsePostConnectionBodyDTO extends ResponseBodyDTO {

  private String localSipAddress;

  private String remoteSipAddress;

  private Long nowTs;

  private String serverSignature;
  
  private boolean isRejected;

  private boolean isConnected;

  private boolean isRetry;
  
  private String returnPassword;
  
  private String serverMessage;

  public ResponsePostConnectionBodyDTO(String localSipAddress, String remoteSipAddress, 
      Long nowTs, String serverSignature, boolean isRejected, boolean isConnected, boolean isRetry,
      String returnPassword, String serverMessage) {
    
    this.localSipAddress = localSipAddress;
    
    this.remoteSipAddress = remoteSipAddress;
    
    this.nowTs = nowTs;
    
    this.serverSignature = serverSignature;
    
    this.isRejected = isRejected;
    
    this.isConnected = isConnected;
    
    this.isRetry = isRetry;
    
    this.returnPassword = returnPassword;
    
    this.serverMessage = serverMessage;
  }
  
  public String getLocalSipAddress() {

    return localSipAddress;
  }

  public void setLocalSipAddress(String localSipAddress) {

    this.localSipAddress = localSipAddress;
  }

  public String getRemoteSipAddress() {

    return remoteSipAddress;
  }

  public void setRemoteSipAddress(String remoteSipAddress) {

    this.remoteSipAddress = remoteSipAddress;
  }

  public Long getNowTs() {
    
    return nowTs;
  }

  public void setNowTs(Long nowTs) {
    
    this.nowTs = nowTs;
  } 
  
  public String getServerSignature() {
    
    return serverSignature;
  }

  public void setServerSignature(String serverSignature) {
 
    this.serverSignature = serverSignature;
  }
      
  public String getServerMessage() {
    
    return serverMessage;
  }

  public void setServerMessage(String serverMessage) {
 
    this.serverMessage = serverMessage;
  }
  
  @JsonProperty("isRejected")
  public boolean isRejected() {
    
    return isRejected;
  }

  @JsonProperty("isRejected")
  public void setRejected(boolean isRejected) {
    
    this.isRejected = isRejected;
  }
  
  @JsonProperty("isConnected")
  public boolean isConnected() {
    
    return isConnected;
  }

  @JsonProperty("isConnected")
  public void setConnected(boolean isConnected) {
    
    this.isConnected = isConnected;
  }
  
  @JsonProperty("isRetry")
  public boolean isRetry() {
    
    return isRetry;
  }

  @JsonProperty("isRetry")
  public void setRetry(boolean isRetry) {
    
    this.isRetry = isRetry;
  }
  
  public String getReturnPassword() {
    
    return returnPassword;
  }

  public void setReturnPassword(String returnPassword) {
 
    this.returnPassword = returnPassword;
  }

}

