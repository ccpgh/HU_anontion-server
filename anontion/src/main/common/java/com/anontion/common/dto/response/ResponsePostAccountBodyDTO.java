package com.anontion.common.dto.response;

import java.util.UUID;

public class ResponsePostAccountBodyDTO extends ResponseBodyDTO {

  private UUID accountId;

  private Long clientTs;

  private String clientName;

  private UUID clientId;
  
  private String sipUsername;

  private String sipPassword;

  private Float progressPercentage;

  private boolean isApproved;

  public ResponsePostAccountBodyDTO() {

  }

  public ResponsePostAccountBodyDTO(UUID accountId, Long clientTs, String clientName, 
      UUID clientId, String sipUsername, String sipPassword, Float progressPercentage, boolean isApproved) {
    
    this.accountId = accountId;

    this.clientTs = clientTs;
    
    this.clientName = clientName;
    
    this.clientId = clientId;
    
    this.sipUsername = sipUsername;

    this.sipPassword = sipPassword;

    this.progressPercentage = progressPercentage;
    
    this.isApproved = isApproved;
  }

  public UUID getAccountId() {

    return accountId;
  }

  public void setAccountId(UUID accountId) {
    
    this.accountId = accountId;
  }

  public Long getClientTs() {
    
    return clientTs;
  }

  public void setClientTs(Long clientTs) {
    
    this.clientTs = clientTs;
  }

  public String getClientName() {
    
    return clientName;
  }

  public void setClientName(String clientName) {
    
    this.clientName = clientName;
  }

  public UUID getClientId() {
    
    return clientId;
  }

  public void setClientId(UUID clientId) {
    
    this.clientId = clientId;
  }
  
  public boolean getIsApproved() {
    
    return isApproved;
  }

  public void setIsApproved(boolean isApproved) {
    
    this.isApproved = isApproved;
  }
  
  public Float getProgressPercentage() {
    
    return progressPercentage;
  }

  public void setProgressPercentage(Float progressPercentage) {
    
    this.progressPercentage = progressPercentage;
  }
  

  public String getSipUsername() {
    
    return sipUsername;
  }

  public void setSipUsername(String sipUsername) {
    
    this.sipUsername = sipUsername;
  }


  public String getSipPassword() {
    
    return sipPassword;
  }

  public void setSipPassword(String sipPassword) {
    
    this.sipPassword = sipPassword;
  }
}

