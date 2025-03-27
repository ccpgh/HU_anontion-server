package com.anontion.account.model;

import java.util.UUID;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;

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
  private Integer ts;

  @Id
  @Column(nullable = false)
  private UUID client;

  @Column(nullable = false) 
  private Integer now;

  public AnontionApplication() {
    
  }

  public AnontionApplication(String name, Integer ts, UUID client) {

    this.id = UUID.randomUUID(); //TODO check unique
    this.name = name;
    this.ts = ts;
    this.client = client;
    this.now = (int) (System.currentTimeMillis() / 1000); 
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

  public Integer getTs() {
    
    return ts;
  }

  public void setTs(Integer ts) {
    
    this.ts = ts;
  }

  public UUID getClient() {
    
    return client;
  }

  public void setClient(UUID client) {
    
    this.client = client;
  }

  public Integer getNow() {
    
    return now;
  }

  public void setNow(Integer now) {
    
    this.now = now;
  }
}