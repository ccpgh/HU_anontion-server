package com.anontion.common.dto.response;

import java.util.UUID;

public class ResponseBodyAccountDTO extends ResponseBodyDTO {

  private UUID id;

  private Integer ts;

  private String name;

  private UUID application;

  private String pub;

  public ResponseBodyAccountDTO() {

  }

  public ResponseBodyAccountDTO(UUID id, Integer ts, String name, UUID application, String pub) {

    this.id = id;
    this.ts = ts;
    this.name = name;
    this.application = application;
    this.pub = pub;
  }

  public UUID getId() {

    return id;
  }

  public void setId(UUID id) {
    
    this.id = id;
  }

  public Integer getTs() {
    
    return ts;
  }

  public void setTs(Integer ts) {
    
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

  public String getPub() {
    
    return pub;
  }

  public void setPub(String pub) {
    
    this.pub = pub;
  }
}
