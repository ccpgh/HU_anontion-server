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

import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionTime;

@Entity
@Table(
  name = "anontion_connection",
  uniqueConstraints = { @UniqueConstraint(columnNames = {"connection_id"}), @UniqueConstraint(columnNames = {"roll_sip_endpoint"}) }
)
@IdClass(AnontionConnectionId.class)
public class AnontionConnection {

  //

  @Column(name = "connection_id")
  private UUID connectionId;

  @Column(name = "connection_ts", nullable = false)
  private Long connectionTs;

  //

  @Column(name = "sip_ts_A", nullable = true)
  private Long sipTsA;
  
  @Id
  @Column(name = "sip_endpoint_A", nullable = false, length = 255)
  private String sipEndpointA;

  @Column(name = "sip_signature_A", nullable = true, length = 255)
  private String sipSignatureA;

  @Column(name = "sip_password_A", nullable = true, length = 255)
  private String sipPasswordA;

  @Column(name = "sip_label_A", nullable = true, length = 255)
  private String sipLabelA;

  //
  
  @Column(name = "sip_ts_B", nullable = true)
  private Long sipTsB;

  @Id
  @Column(name = "sip_endpoint_B", nullable = false, length = 255)
  private String sipEndpointB;

  @Column(name = "sip_signature_B", nullable = true, length = 255)
  private String sipSignatureB;

  @Column(name = "sip_password_B", nullable = true, length = 255)
  private String sipPasswordB;

  @Column(name = "sip_label_B", nullable = true, length = 255)
  private String sipLabelB;

  //
  
  @Column(name = "timeout_ts", nullable = false)
  private Long timeoutTs;

  //
  
  @Pattern(regexp = "direct|indirect|multiple|roll|broadcast", message = "connectionType must be 'direct', 'indirect', 'roll', 'broadcast' or 'multiple'")
  @Column(name = "connection_type", nullable = true, length = 255)
  private String connectionType;

  @Column(name = "roll_sip_endpoint", nullable = true, length = 255)
  private String rollSipEndpoint;

  //

  @Column(name = "latitude", nullable = false)
  private Double latitude;

  @Column(name = "longitude", nullable = false)
  private Double longitude;
    
  //
  
  @PrePersist
  @PreUpdate
  private void validateSipOrder() {
      if (sipEndpointA.compareTo(sipEndpointB) > 0) {
          throw new IllegalStateException("sipEndpointA must be less than sipEndpointB");
      }
  }

  public AnontionConnection(Long sipTsA, String sipEndpointA, String sipSignatureA, String sipLabelA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, String sipLabelB, String connectionType, String rollSipEndpoint) {
    
    this(sipTsA, sipEndpointA, sipSignatureA, sipLabelA, sipTsB, sipEndpointB, sipSignatureB, sipLabelB, 
        connectionType, rollSipEndpoint, AnontionConfig._CONTACT_GPS_NULL_LATITUDE, 
        AnontionConfig._CONTACT_GPS_NULL_LONGITUDE, AnontionConfig._CONTACT_CONNECTION_BROADCAST_TIMEOUT_NOTIMEOUT);
  }
  
  public AnontionConnection(Long sipTsA, String sipEndpointA, String sipSignatureA, String sipLabelA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, String sipLabelB, String connectionType, 
      String rollSipEndpoint, Double latitude, Double longitude, Long timeoutTs) {
    
    this.connectionId = UUID.randomUUID();

    this.connectionTs = AnontionTime.tsN();
    
    //
    
    this.sipTsA = sipTsA;

    this.sipEndpointA = sipEndpointA;
    
    this.sipSignatureA = sipSignatureA;
    
    this.sipPasswordA = "";
    
    this.sipLabelA = sipLabelA;
    
    //
    
    this.sipTsB = sipTsB;
    
    this.sipEndpointB = sipEndpointB;
    
    this.sipSignatureB = sipSignatureB;
    
    this.sipPasswordB = "";

    this.sipLabelB = sipLabelB;

    //
    
    this.connectionType = connectionType;
    
    this.rollSipEndpoint = rollSipEndpoint;
    
    //
    
    this.latitude = latitude;
    
    this.longitude = longitude;
    
    this.timeoutTs = timeoutTs;
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

  //
  
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

  public String getSipPasswordA() {
    
    return sipPasswordA;
  }

  public void setSipPasswordA(String sipPasswordA) {
    
    this.sipPasswordA = sipPasswordA;
  }

  public String getSipLabelA() {
    
    return sipLabelA;
  }

  public void setSipLabelA(String sipLabelA) {
    
    this.sipLabelA = sipLabelA;
  }

  //

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
  
  
  public String getSipPasswordB() {
    
    return sipPasswordB;
  }

  public void setSipPasswordB(String sipPasswordB) {
    
    this.sipPasswordB = sipPasswordB;
  }

  public String getSipLabelB() {
    
    return sipLabelB;
  }

  public void setSipLabelB(String sipLabelB) {
    
    this.sipLabelB = sipLabelB;
  }

  //
  
  public String getConnectionType() {
    
    return connectionType;
  }

  public void setConnectionType(String connectionType) {
    
    this.connectionType = connectionType;
  }
  
  public String getRollSipEndpoint() {
    
    return rollSipEndpoint;
  }

  public void setRollSipEndpoint(String rollSipEndpoint) {
    
    this.rollSipEndpoint = rollSipEndpoint;
  }
  
  //

  public Double getLatitude() {
    
    return latitude;
  }

  public void setLatitude(Double latitude) {
    
    this.latitude = latitude;
  }
  
  public Double getLongitude() {
    
    return longitude;
  }

  public void setLongitude(Double longitude) {
    
    this.longitude = longitude;
  }

  public Long getTimeoutTs() {
    
    return timeoutTs;
  }

  public void setTimeoutTs(Long timeoutTs) {
    
    this.timeoutTs = timeoutTs;
  }

}



