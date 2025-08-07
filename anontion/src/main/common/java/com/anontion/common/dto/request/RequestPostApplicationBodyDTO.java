package com.anontion.common.dto.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestPostApplicationBodyDTO {
  
  @NotNull(message = "Client Id cannot be null.")
  private UUID clientId;

  @NotBlank(message = "Client Pub cannot be blank.")
  private String clientPub;

  public RequestPostApplicationBodyDTO() {
    
  }
  
  public UUID getClientId() {
    
    return clientId;
  }

  public void setClientId(UUID clientId) {
    
    this.clientId = clientId;
  }
  
  public String getClientPub() {
    
    return clientPub;
  }

  public void setClientPub(String clientPub) {
 
    this.clientPub = clientPub;
  }
}
