package com.anontion.common.dto.request;

import java.util.UUID;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Component
public class RequestPostApplicationBodyDTO {
  
  @NotNull(message = "Id cannot be null.")
  private UUID id;

  @NotBlank(message = "Name cannot be blank.")
  private String name;

  @NotBlank(message = "Pub cannot be blank.")
  private String pub;

  @NotBlank(message = "Encrypt cannot be blank.")
  private String encrypt;

  public RequestPostApplicationBodyDTO() {
    
  }
  
  public UUID getId() {
    
    return id;
  }

  public void setId(UUID id) {
    
    this.id = id;
  }

  public String getName() {
    
    return name;
  }

  public void setName(String name) {
 
    this.name = name;
  }
  
  public String getPub() {
    
    return pub;
  }

  public void setPub(String pub) {
 
    this.pub = pub;
  }
  
  public String getEncrypt() {
    
    return encrypt;
  }

  public void setEncrypt(String encrypt) {
 
    this.encrypt = encrypt;
  }

}
