package com.anontion.common.dto.response;

import java.time.Instant;

public class ResponseBodyPOWDTO extends ResponseBodyDTO {

  private String data;

  private int target;

  private Instant ts;

  private String signature; // TODO

  public ResponseBodyPOWDTO() {

  }

  public ResponseBodyPOWDTO(String data, int target, Instant ts) {

    this.data = data;
    this.target = target;
    this.ts = ts;
    this.signature = data; // TODO fix - use signature
  }

  public String getData() {
    
    return data;
  }

  public void setData(String data) {
    
    this.data = data;
  }

  public int getTarget() {
    
    return target;
  }

  public void setTarget(int target) {
    
    this.target = target;
  }

  public Instant getTs() {
    
    return ts;
  }

  public void setTs(Instant ts) {

    this.ts = ts;
  }
}
