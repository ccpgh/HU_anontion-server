package com.anontion.models.connection.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;


import com.anontion.common.misc.AnontionTime;

@Entity
@Table(
  name = "anontion_connection",
  uniqueConstraints = { @UniqueConstraint(columnNames = {"connection_id"}) }
)
@IdClass(AnontionConnectionId.class)
public class AnontionConnection {

  @Column(name = "connection_id")
  private UUID connectionId;

  @Column(name = "connection_ts", nullable = false)
  private Long connectionTs;

  @Column(name = "sip_ts_A", nullable = true)
  private Long sipTsA;
  
  @Id
  @Column(name = "sip_endpoint_A", nullable = false, length = 255)
  private String sipEndpointA;

  @Column(name = "sip_signature_A", nullable = true, length = 255)
  private String sipSignatureA;

  @Column(name = "sip_ts_B", nullable = true)
  private Long sipTsB;

  @Id
  @Column(name = "sip_endpoint_B", nullable = false, length = 255)
  private String sipEndpointB;

  @Column(name = "sip_signature_B", nullable = true, length = 255)
  private String sipSignatureB;

  @Pattern(regexp = "direct|indirect|multiple", message = "connectionType must be 'direct', 'indirect', or 'multiple'")
  @Column(name = "connection_type", nullable = true, length = 255)
  private String connectionType;
  
  @PrePersist
  @PreUpdate
  private void validateSipOrder() {
      if (sipEndpointA.compareTo(sipEndpointB) >= 0) {
          throw new IllegalStateException("sipEndpointA must be less than sipEndpointB");
      }
  }

  public AnontionConnection(Long sipTsA, String sipEndpointA, String sipSignatureA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, String connectionType) {
    
    this.connectionId = UUID.randomUUID();

    this.connectionTs = AnontionTime.tsN();
    
    this.sipTsA = sipTsA;

    this.sipEndpointA = sipEndpointA;
    
    this.sipSignatureA = sipSignatureA;
    
    this.sipTsB = sipTsB;
    
    this.sipEndpointB = sipEndpointB;
    
    this.sipSignatureB = sipSignatureB;
    
    this.connectionType = connectionType;
  }

  public AnontionConnection() {

  }

  public UUID getConnectionId() {
    
    return connectionId;
  }

  public void setConnectionId(UUID connectionId) {
    
    this.connectionId = connectionId;
  }

  public Long getConnectionTs() {
    
    return connectionTs;
  }

  public void setConnectionTs(Long connectionTs) {
    
    this.connectionTs = connectionTs;
  }

  public Long getSipTsA() {

    return sipTsA;
  }

  public void setSipTsA(Long sipTsA) {
    
    this.sipTsA = sipTsA;
  }

  public String getSipEndpointA() {
    
    return sipEndpointA;
  }

  public void setSipEndpointA(String sipEndpointA) {
    
    this.sipEndpointA = sipEndpointA;
  }

  public String getSipSignatureA() {
    
    return sipSignatureA;
  }

  public void setSipSignatureA(String sipSignatureA) {
    
    this.sipSignatureA = sipSignatureA;
  }

  public Long getSipTsB() {
    
    return sipTsB;
  }

  public void setSipTsB(Long sipTsB) {
    
    this.sipTsB = sipTsB;
  }

  public String getSipEndpointB() {
    
    return sipEndpointB;
  }

  public void setSipEndpointB(String sipEndpointB) {
    
    this.sipEndpointB = sipEndpointB;
  }

  public String getSipSignatureB() {
    
    return sipSignatureB;
  }

  public void setSipSignatureB(String sipSignatureB) {
    
    this.sipSignatureB = sipSignatureB;
  }
  
  public String getConnectionType() {
    
    return connectionType;
  }

  public void setConnectionType(String connectionType) {
    
    this.connectionType = connectionType;
  }
}



