package com.anontion.common.dto.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestPostConnectionBodyDTO {

  @NotNull(message = "Client Id cannot be null.")
  private UUID clientId;

  @NotNull(message = "Client Timestamp cannot be null.")
  private Long clientTs;
  
  @NotBlank(message = "Client Name cannot be blank.")
  private String clientName;

  @NotBlank(message = "Local Sip Address cannot be blank.")
  private String localSipAddress;

  @NotBlank(message = "Remote Sip Address cannot be blank.")
  private String remoteSipAddress;

  @NotNull(message = "Now Timestamp cannot be null.")
  private Long nowTs;

  @NotBlank(message = "Client Signature1 cannot be blank.")
  private String clientSignature1;

  @NotBlank(message = "Client Signature2 cannot be blank.")
  private String clientSignature2;

  public RequestPostConnectionBodyDTO() {
    
  }
  
  public UUID getClientId() {
    
    return clientId;
  }

  public void setClientId(UUID clientId) {
    
    this.clientId = clientId;
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
  
  public String getClientSignature1() {
    
    return clientSignature1;
  }

  public void setClientSignature1(String clientSignature1) {
 
    this.clientSignature1 = clientSignature1;
  }

  public String getClientSignature2() {
    
    return clientSignature2;
  }

  public void setClientSignature2(String clientSignature2) {
 
    this.clientSignature2 = clientSignature2;
  }

}

