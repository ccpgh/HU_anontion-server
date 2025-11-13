package com.anontion.common.dto.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestPostAccountBodyDTO {

  @NotNull(message = "Client Id cannot be null.")
  private UUID clientId;

  @NotNull(message = "Now Timestamp cannot be null.")
  private Long nowTs;

  @NotNull(message = "Client Timestamp cannot be null.")
  private Long clientTs;
  
  @NotBlank(message = "Client Name cannot be blank.")
  private String clientName;

  @NotBlank(message = "Pow Low cannot be blank.")
  private String powLow;

  @NotBlank(message = "Pow High cannot be blank.")
  private String powHigh;

  @NotBlank(message = "Pow Text cannot be blank.")
  private String powText;

  @NotBlank(message = "Pow Target cannot be blank.")
  private String powTarget;

  @NotBlank(message = "Pow Nonce cannot be blank.")
  private String powNonce;

  @NotBlank(message = "Nonce cannot be blank.")
  private String nonce;

  @NotBlank(message = "Client Signature cannot be blank.")
  private String clientSignature;

  public RequestPostAccountBodyDTO() {
    
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
  
  public String getPowLow() {
    
    return powLow;
  }

  public void setPowLow(String powLow) {
 
    this.powLow = powLow;
  }

  public String getPowHigh() {
    
    return powHigh;
  }

  public void setPowHigh(String powHigh) {
 
    this.powHigh = powHigh;
  }

  public String getPowText() {
    
    return powText;
  }

  public void setPowText(String powText) {
 
    this.powText = powText;
  }

  public String getPowTarget() {
    
    return powTarget;
  }

  public void setPowTarget(String powTarget) {
 
    this.powTarget = powTarget;
  }
  
  public String getClientSignature() {
    
    return clientSignature;
  }

  public void setClientSignature(String clientSignature) {
 
    this.clientSignature = clientSignature;
  }

  public String getPowNonce() {
    
    return powNonce;
  }

  public void setPownonce(String powNonce) {
 
    this.powNonce = powNonce;
  }
  
  public Long getNowTs() {
    
    return nowTs;
  }

  public void setNowTs(Long nowTs) {
    
    this.nowTs = nowTs;
  }

  public String getNonce() {
    
    return nonce;
  }

  public void setNonce(String nonce) {
 
    this.nonce = nonce;
  }
}

