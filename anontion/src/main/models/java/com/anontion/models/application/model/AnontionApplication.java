package com.anontion.models.application.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "anontion_application")
@IdClass(AnontionApplicationId.class)
public class AnontionApplication {

  @Column(name = "application_id", nullable = false, unique = true)
  private UUID applicationId;

  @Id
  @Column(name = "client_name", nullable = false, length = 255)
  private String clientName;

  @Id
  @Column(name = "client_ts", nullable = false)
  private Long clientTs;

  @Id
  @Column(name = "client_id", nullable = false)
  private UUID clientId;

  @Column(name = "client_pub", nullable = false, length = 255) 
  private String clientPub;

  @Column(name = "client_uid", nullable = false, length = 255) 
  private String clientUID;

  @Column(name = "server_signature", nullable = false, length = 255) 
  private String serverSignature;

  @Column(name = "pow_text", nullable = false, columnDefinition = "TEXT")
  private String powText;

  @Column(name = "pos_target", nullable = false) 
  private Long powTarget;

  @Column(name = "is_disabled", nullable = false) 
  private boolean isDisabled;

  public AnontionApplication() {
    
  }

  public AnontionApplication(String clientName, Long clientTs, UUID clientId, String clientPub, String clientUID, String serverSignature, String powText, Long powTarget, boolean isDisabled) {

    this.applicationId = UUID.randomUUID();

    this.clientName = clientName;
    
    this.clientTs = clientTs;
    
    this.clientId = clientId;
    
    this.clientPub = clientPub;
    
    this.clientUID = clientUID;
    
    this.serverSignature = serverSignature;
    
    this.powText = powText;
    
    this.powTarget = powTarget;
    
    this.isDisabled = isDisabled;
  }

  public UUID getApplicationId() {
    
    return applicationId;
  }

  public void setApplicationId(UUID applicationId) {

    this.applicationId = applicationId;
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

  public String getClientPub() {

    return clientPub;
  }

  public void setClientPub(String clientPub) {

    this.clientPub = clientPub;
  }
  
  public String getClientUID() {

    return clientUID;
  }

  public void setClientUid(String clientUID) {

    this.clientUID = clientUID;
  }
  
  public String getServerSignature() {

    return serverSignature;
  }

  public void setServerSignature(String serverSignature) {

    this.serverSignature = serverSignature;
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

  @JsonProperty("isADisabled")
  public boolean isDisabled() {

    return isDisabled;
  }

  @JsonProperty("isADisabled")
  public void setDisabled(boolean isDisabled) {

    this.isDisabled = isDisabled;
  }
}
