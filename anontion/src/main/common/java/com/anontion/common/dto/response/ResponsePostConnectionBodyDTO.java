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
  
  private String returnSipUserId;
  
  private String returnSipLabel;
  
  private String returnPhoto;
  
  private String serverMessage;

  private Long timeoutTs;

  public ResponsePostConnectionBodyDTO(String localSipAddress, String remoteSipAddress, 
      Long nowTs, String serverSignature, boolean isRejected, boolean isConnected, boolean isRetry,
      String returnPassword, String returnSipUserId, String returnSipLabel, String returnPhoto, String serverMessage) {
    
    this (localSipAddress, remoteSipAddress, nowTs, serverSignature, isRejected, isConnected, isRetry, 
        returnPassword, returnSipUserId, returnSipLabel, returnPhoto, serverMessage, 0L);
  }
  
  public ResponsePostConnectionBodyDTO(String localSipAddress, String remoteSipAddress, 
      Long nowTs, String serverSignature, boolean isRejected, boolean isConnected, boolean isRetry,
      String returnPassword, String returnSipUserId, String returnSipLabel, String returnPhoto, String serverMessage, Long timeoutTs) {
    
    this.localSipAddress = localSipAddress;
    
    this.remoteSipAddress = remoteSipAddress;
    
    this.nowTs = nowTs;
    
    this.serverSignature = serverSignature;
    
    this.isRejected = isRejected;
    
    this.isConnected = isConnected;
    
    this.isRetry = isRetry;
    
    this.returnPassword = returnPassword;

    this.returnSipUserId = returnSipUserId;
    
    this.returnSipLabel = returnSipLabel;
    
    this.returnPhoto = returnPhoto;

    this.serverMessage = serverMessage;
    
    this.timeoutTs = timeoutTs;
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

  public String getReturnSipUserId() {
    
    return returnSipUserId;
  }

  public void setReturnSipUserId(String returnSipUserId) {
 
    this.returnSipUserId = returnSipUserId;
  }

  public String getReturnSipLabel() {
    
    return returnSipLabel;
  }

  public void setReturnSipLabel(String returnSipLabel) {
 
    this.returnSipLabel = returnSipLabel;
  }
  
  public String getReturnPhoto() {
    
    return returnPhoto;
  }

  public void setReturnPhoto(String returnPhoto) {
 
    this.returnPhoto = returnPhoto;
  }

  public Long getTimeoutTs() {
    
    return timeoutTs;
  }

  public void setTimeoutTs(Long timeoutTs) {
 
    this.timeoutTs = timeoutTs;
  }
}

