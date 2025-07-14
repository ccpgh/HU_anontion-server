package com.anontion.common.dto.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestPostAccountBodyDTO {

  @NotNull(message = "Client Id cannot be null.")
  private UUID clientId;

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

  @Min(0)
  private Long powTarget;

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

  public Long getPowTarget() {
    
    return powTarget;
  }

  public void setPowTarget(Long powTarget) {
 
    this.powTarget = powTarget;
  }
  
  public String getClientSignature() {
    
    return clientSignature;
  }

  public void setClientSignature(String clientSignature) {
 
    this.clientSignature = clientSignature;
  }
}

