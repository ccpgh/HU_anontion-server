package com.anontion.models.account.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

import com.anontion.common.misc.AnontionJson;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(
  name = "anontion_account",
  uniqueConstraints = @UniqueConstraint(columnNames = {"client_ts", "client_name", "client_id"})
)
public class AnontionAccount {

  public static final Float CONSTANT_COMPLETED_POW_PERCENTAGE = 100.0f;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "account_id")
  private UUID accountId;

  @Column(name = "client_ts", nullable = false)
  private Long clientTs;

  @Column(name = "client_name", nullable = false, length = 255)
  private String clientName;

  @Column(name = "client_id", nullable = false)
  private UUID clientId;

  @Column(name = "server_signature", nullable = false, length = 255)
  private String serverSignature;

  @Column(name = "client_pub", columnDefinition = "VARCHAR(4096)", nullable = false)
  private String clientPub;

  @Column(name = "client_uid", nullable = false, length = 255)
  private String clientUID;

  @Column(name = "default_expiration", nullable = false)
  private Integer defaultExpiration;

  @Column(name = "minimum_expiration", nullable = false)
  private Integer minimumExpiration;

  @Column(name = "maximum_expiration", nullable = false)
  private Integer maximumExpiration;

  @Column(name = "is_disabled", nullable = false)
  private boolean isDisabled;

  //
  
  public final static Integer DEFAULT_defaultExpiration = 300;

  public final static Integer DEFAULT_minimumExpiration = 60;

  public final static Integer DEFAULT_maximumExpiration = 3600;
      
  public AnontionAccount() {
    
  }

  public AnontionAccount(Long clientTs, String clientName, UUID clientId, String serverSignature, 
      String clientPub, String clientUID, Integer defaultExpiration, Integer minimumExpiration, 
      Integer maximumExpiration, boolean isDisabled) {

    this.clientTs = clientTs;

    this.clientName = clientName;
    
    this.clientId = clientId;
        
    this.serverSignature = serverSignature;
    
    this.clientPub = clientPub;
    
    this.clientUID = clientUID;
    
    this.defaultExpiration = defaultExpiration;
    
    this.minimumExpiration = minimumExpiration;
    
    this.maximumExpiration = maximumExpiration;

    this.isDisabled = isDisabled;
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

  public String getClientPub() {
    
    return clientPub;
  }

  public void setClientPub(String clientPub) {
   
    this.clientPub = clientPub;
  }

  public String getServerSignature() {
    
    return serverSignature;
  }

  public void setServerSignature(String serverSignature) {
   
    this.serverSignature = serverSignature;
  }
  
  public String getClientUid() {
    
    return clientUID;
  }

  public void setClientUid(String clientUID) {
   
    this.clientUID = clientUID;
  }
  
  public String toString() {
    
    return AnontionJson.o2Json(this);
  }
  
  @JsonProperty("isADisabled")
  public boolean isDisabled() {

    return isDisabled;
  }

  @JsonProperty("isADisabled")
  public void setDisabled(boolean isDisabled) {

    this.isDisabled = isDisabled;
  }
  
  public Integer getDefaultExpiration() {

    return defaultExpiration;
  }

  public void setDefaultExpiration(Integer defaultExpiration) {

    this.defaultExpiration = defaultExpiration;
  }
  
  public Integer getMinimumExpiration() {

    return minimumExpiration;
  }

  public void setMinimumExpiration(Integer minimumExpiration) {

    this.minimumExpiration = minimumExpiration;
  }
  
  public Integer getMaximumExpiration() {

    return maximumExpiration;
  }

  public void setMaximumExpiration(Integer maximumExpiration) {

    this.maximumExpiration = maximumExpiration;
  }
  
  public Integer getSipExpiration() {
    
    return Math.max(getDefaultExpiration()/3, getMinimumExpiration());
  }
}

