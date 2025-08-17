package com.anontion.common.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponsePostAccountBodyDTO extends ResponseBodyDTO {

  private UUID accountId;

  private Long clientTs;

  private String clientName;

  private UUID clientId;
  
  private String sipUsername;

  private String sipUserId;

  private String sipPassword;

  private Integer sipRegisterExpiry;

  private Float progressPercentage;

  private boolean isApproved;

  public ResponsePostAccountBodyDTO() {

  }

  public ResponsePostAccountBodyDTO(UUID accountId, Long clientTs, String clientName, 
      UUID clientId, String sipUsername, String sipUserId, String sipPassword, Float progressPercentage, 
      Integer sipRegisterExpiry, boolean isApproved) {
    
    this.accountId = accountId;

    this.clientTs = clientTs;
    
    this.clientName = clientName;
    
    this.clientId = clientId;
    
    this.sipUsername = sipUsername;

    this.sipUserId = sipUserId;

    this.sipPassword = sipPassword;

    this.progressPercentage = progressPercentage;
    
    this.sipRegisterExpiry = sipRegisterExpiry;

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
  
  @JsonProperty("isApproved")
  public boolean isApproved() {
    
    return isApproved;
  }

  @JsonProperty("isApproved")
  public void setApproved(boolean isApproved) {
    
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

  public String getSipUserId() {
    
    return sipUserId;
  }

  public void setSipUserId(String sipUserId) {
    
    this.sipUserId = sipUserId;
  }

  public String getSipPassword() {
    
    return sipPassword;
  }

  public void setSipPassword(String sipPassword) {
    
    this.sipPassword = sipPassword;
  }
  
  public Integer getSipRegisterExpiry() {
    
    return sipRegisterExpiry;
  }

  public void setSipRegisterExpiry(Integer sipRegisterExpiry) {
    
    this.sipRegisterExpiry = sipRegisterExpiry;
  }
  
}

