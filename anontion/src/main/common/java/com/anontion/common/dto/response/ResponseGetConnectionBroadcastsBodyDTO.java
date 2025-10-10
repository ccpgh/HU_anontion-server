package com.anontion.common.dto.response;

import java.util.List;

public class ResponseGetConnectionBroadcastsBodyDTO extends ResponseBodyDTO {

  private Double latitude;

  private Double longitude;
  
  private Double distance;
  
  private List<ResponseGetConnectionBroadcastBodyDTO> broadcasts;
  
  public ResponseGetConnectionBroadcastsBodyDTO() {

  }

  public ResponseGetConnectionBroadcastsBodyDTO(Double latitude,
      Double longitude,
      Double distance,
      List<ResponseGetConnectionBroadcastBodyDTO> broadcasts) {
    
    this.latitude = latitude;
    
    this.longitude = longitude;

    this.distance = distance;
    
    this.broadcasts = broadcasts;
  }

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

  public Double getDistance() {
    
    return distance;
  }

  public void setDistance(Double distance) {
  
    this.distance = distance;
  }

  public List<ResponseGetConnectionBroadcastBodyDTO> getBroadcasts() {
    
    return broadcasts;
  }

  public void setBroadcasts(List<ResponseGetConnectionBroadcastBodyDTO> broadcasts) {
  
    this.broadcasts = broadcasts;
  }
}
