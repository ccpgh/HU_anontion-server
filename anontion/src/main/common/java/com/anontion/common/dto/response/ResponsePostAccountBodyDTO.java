package com.anontion.common.dto.response;

import java.util.UUID;

public class ResponsePostAccountBodyDTO extends ResponseBodyDTO {

  private UUID id;

  private Long ts;

  private String name;

  private UUID application;
  
  private String username;

  private String password;

  private Float progress;

  private boolean approved;

  public ResponsePostAccountBodyDTO() {

  }

  public ResponsePostAccountBodyDTO(UUID id, Long ts, String name, UUID application, String username, String password, Float progress, 
      boolean approved) {
    
    this.id = id;

    this.ts = ts;
    
    this.name = name;
    
    this.application = application;
    
    this.username = username;

    this.password = password;

    this.progress = progress;
    
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
  
  public Float getProgress() {
    
    return progress;
  }

  public void setProgress(Float progress) {
    
    this.progress = progress;
  }
  

  public String getUsername() {
    
    return username;
  }

  public void setUsername(String username) {
    
    this.username = username;
  }


  public String getPassword() {
    
    return password;
  }

  public void setPassword(String password) {
    
    this.password = password;
  }
}

