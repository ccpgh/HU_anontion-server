package com.anontion.common.dto.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestAccountBodyDTO {
  
  @NotNull(message = "Id cannot be null.")
  private UUID id;

  @NotNull(message = "Timestamp cannot be null.")
  private Long ts;
  
  @NotBlank(message = "Name cannot be blank.")
  private String name;

  @NotBlank(message = "Low cannot be blank.")
  private String low;

  @NotBlank(message = "High cannot be blank.")
  private String high;

  @NotBlank(message = "Target cannot be blank.")
  private String text;

  @Min(0)
  private Long target;

  public RequestAccountBodyDTO() {
    
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
  
  public String getLow() {
    
    return low;
  }

  public void setLow(String low) {
 
    this.low = low;
  }

  public String getHigh() {
    
    return high;
  }

  public void setHigh(String high) {
 
    this.high = high;
  }

  public String getText() {
    
    return text;
  }

  public void setText(String text) {
 
    this.text = text;
  }

  public Long getTarget() {
    
    return target;
  }

  public void setTarget(Long target) {
 
    this.target = target;
  }
}
