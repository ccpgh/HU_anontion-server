package com.anontion.common.dto.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestApplicationBodyDTO {
  
  @NotNull(message = "Id cannot be null.")
  private UUID id;

  @NotNull(message = "Timestamp cannot be null.")
  private Integer ts;
  
  @NotBlank(message = "Name cannot be blank.")
  private String name;

  public RequestApplicationBodyDTO() {
    
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
}
