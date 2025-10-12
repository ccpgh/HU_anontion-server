package com.anontion.common.dto.response;

import java.util.UUID;

public class ResponseGetConnectionBroadcastBodyDTO {

  private UUID id;
  
  private double latitude;
  
  private double longitude;
  
  private double distance;

  private String sipUsername;
  
  private Long timeoutTs;

  public ResponseGetConnectionBroadcastBodyDTO(UUID id, double latitude, double longitude, double distance, String sipUsername, Long timeoutTs) {
  
    this.id = id;
    
    this.latitude = latitude;
    
    this.longitude = longitude;
    
    this.distance = distance;
    
    this.sipUsername = sipUsername;
    
    this.timeoutTs = timeoutTs;
  }

  public UUID getId() {

    return id;
  }

  public void setId(UUID id) {
  
    this.id = id;
  }

  public double getLatitude() {

    return latitude;
  }

  public void setLatitude(double latitude) {
  
    this.latitude = latitude;
  }

  public double getLongitude() {
    
    return longitude;
  }

  public void setLongitude(double longitude) {
  
    this.longitude = longitude;
  }

  public double getDistance() {
    
    return distance;
  }

  public void setDistance(double distance) {
  
    this.distance = distance;
  }

  public String getSipUsername() {
    
    return sipUsername;
  }
  
  public void setSipUsername(String sipUsername) {
  
    this.sipUsername = sipUsername;
  }
  
  public Long getTimeoutTs() {
    
    return timeoutTs;
  }
  
  public void setTimeoutTs(Long timeoutTs) {
  
    this.timeoutTs = timeoutTs;
  }
}