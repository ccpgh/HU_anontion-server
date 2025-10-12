package com.anontion.common.dto.response;

public class ResponseGetConnectionBodyDTO extends ResponseBodyDTO {

  private String endpointA;
  
  private String endpointB;

  private String rollEndpoint;

  public ResponseGetConnectionBodyDTO() {

  }

  public ResponseGetConnectionBodyDTO(String endpointA,
      String endpointB,
      String rollEndpoint) {
    
    this.endpointA = endpointA;
    this.endpointB = endpointB;
    this.rollEndpoint = rollEndpoint;
  }

  public String getEndpointA() {
  
    return endpointA;
  }

  public void setEndpointA(String endpointA) {
  
    this.endpointA = endpointA;
  }

  public String getEndpointB() {
    
    return endpointB;
  }

  public void setEndpointB(String endpointB) {
  
    this.endpointB = endpointB;
  }

  public String getRollEndpoint() {
    
    return rollEndpoint;
  }

  public void setRollEndpoint(String rollEndpoint) {
  
    this.rollEndpoint = rollEndpoint;
  }

}

