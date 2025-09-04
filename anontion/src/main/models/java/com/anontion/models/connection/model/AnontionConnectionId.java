package com.anontion.models.connection.model;

import java.io.Serializable;

public class AnontionConnectionId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String sipEndpointA;

  private String sipEndpointB;
  
  public AnontionConnectionId() {}

  public AnontionConnectionId(String sipEndpointA, String sipEndpointB) {

    this.sipEndpointA = sipEndpointA;

    this.sipEndpointB = sipEndpointB;
  }

  public String getSipEndpointA() {

    return sipEndpointA;
  }

  public void setSipEndpointA(String sipEndpointA) {

    this.sipEndpointA = sipEndpointA;
  }

  public String getSipEndpoinB() {

    return sipEndpointB;
  }

  public void setSipEndpointB(String sipEndpointB) {

    this.sipEndpointB = sipEndpointB;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {

      return true;
    }

    if (o == null || getClass() != o.getClass()) {

      return false;
    }

    AnontionConnectionId that = (AnontionConnectionId) o;

    return 
        sipEndpointA.equals(that.sipEndpointA) && 
        sipEndpointB.equals(that.sipEndpointB);
  }

  @Override
  public int hashCode() {

    return 31 * 
        (sipEndpointA.hashCode() + 
            sipEndpointB.hashCode());
  }
  
  public String description() {
    
    return String.format("sipEndpointA '%s' sipEndpointB '%s'", 
        sipEndpointA, 
        sipEndpointB);
  }
}