package com.anontion.account.controller;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestAccountBodyDTO {
  
  @NotNull(message = "Id cannot be null.")
  private UUID id;

  @NotNull(message = "Timestamp cannot be null.")
  private Integer ts;
  
  @NotBlank(message = "Name cannot be null.")
  private String name;

  public RequestAccountBodyDTO() {
    
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
