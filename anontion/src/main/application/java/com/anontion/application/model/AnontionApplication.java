package com.anontion.application.model;

import java.util.UUID;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(AnontionApplicationId.class)
public class AnontionApplication {

  @Column(nullable = false, unique = true)
  private UUID id;

  @Id
  @Column(nullable = false)
  private String name;

  @Id
  @Column(nullable = false)
  private Integer cts;

  @Id
  @Column(nullable = false)
  private UUID client;

  @Column(nullable = false) 
  private Integer ts;

  public AnontionApplication() {
    
  }

  public AnontionApplication(String name, Integer cts, UUID client) {

    this.id = UUID.randomUUID();
    this.name = name;
    this.cts = cts;
    this.client = client;
    this.ts = (int) (System.currentTimeMillis() / 1000); 
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

  public Integer getCts() {
    
    return cts;
  }

  public void setCts(Integer cts) {
    
    this.cts = cts;
  }

  public UUID getClient() {
    
    return client;
  }

  public void setClient(UUID client) {
    
    this.client = client;
  }

  public Integer getTs() {
    
    return ts;
  }

  public void setTs(Integer ts) {
    
    this.ts = ts;
  }
}