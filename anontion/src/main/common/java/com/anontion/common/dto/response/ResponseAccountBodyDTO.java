package com.anontion.common.dto.response;

import java.util.UUID;

public class ResponseAccountBodyDTO extends ResponseBodyDTO {

  private UUID id;

  private Long ts;

  private String name;

  private UUID application;

  private boolean approved;

  public ResponseAccountBodyDTO() {

  }

  public ResponseAccountBodyDTO(UUID id, Long ts, String name, UUID application, boolean approved) {
    
    this.id = id;
    this.ts = ts;
    this.name = name;
    this.application = application;
    this.approved = approved;
  }

  public UUID getId() {

    return id;
  }

  public void setId(UUID id) {
    
    this.id = id;
  }

  public Long getTs() {
    
    return ts;
  }

  public void setTs(Long ts) {
    
    this.ts = ts;
  }

  public String getName() {
    
    return name;
  }

  public void setName(String name) {
    
    this.name = name;
  }

  public UUID getApplication() {
    
    return application;
  }

  public void setApplication(UUID application) {
    
    this.application = application;
  }
  
  public boolean getApproved() {
    
    return approved;
  }

  public void setApproved(boolean approved) {
    
    this.approved = approved;
  }
}

