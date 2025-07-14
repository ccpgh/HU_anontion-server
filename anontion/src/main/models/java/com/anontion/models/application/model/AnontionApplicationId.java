package com.anontion.models.application.model;

import java.io.Serializable;

import java.util.UUID;

public class AnontionApplicationId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String clientName;

  private Long clientTs;
  
  private UUID clientId;
  
  public AnontionApplicationId() {}

  public AnontionApplicationId(String clientName, Long clientTs, UUID clientId) {

    this.clientName = clientName;

    this.clientTs = clientTs;
    
    this.clientId = clientId;
  }

  public String getClientName() {

    return clientName;
  }

  public void setClientName(String clientName) {

    this.clientName = clientName;
  }

  public Long getClientTs() {

    return clientTs;
  }

  public void setClientTs(Long clientTs) {

    this.clientTs = clientTs;
  }

  public UUID getClientId() {

    return clientId;
  }

  public void setClientId(UUID clientId) {

    this.clientId = clientId;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {

      return true;
    }

    if (o == null || getClass() != o.getClass()) {

      return false;
    }

    AnontionApplicationId that = (AnontionApplicationId) o;

    return 
        clientName.equals(that.clientName) && 
        clientTs.equals(that.clientTs) && 
        clientId.equals(that.clientId);
  }

  @Override
  public int hashCode() {

    return 31 * 
        (clientName.hashCode() + 
        clientTs.hashCode() + 
        clientId.hashCode());
  }
  
  public String description() {
    
    return String.format("Name '%s' ts '%d' Client '%s'", 
        clientName, 
        clientTs, 
        clientId);
  }
}