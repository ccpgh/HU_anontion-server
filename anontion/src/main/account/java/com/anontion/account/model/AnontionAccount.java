package com.anontion.account.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.UUID;

import com.anontion.common.security.AnontionSecurity;

@Entity
public class AnontionAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false)
  private Integer ts;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private UUID appplication;

  @Column(nullable = false)
  private String key;

  public AnontionAccount() {
    
  }

  public AnontionAccount(Integer ts, String name, UUID application) {

    this.id = UUID.randomUUID();
    this.ts = ts;
    this.name = name;
    this.appplication = application;
    this.key = AnontionSecurity.encodeKey(AnontionSecurity.getPrivateECDSAKey());
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

  public UUID getAppplication() {
    
    return appplication;
  }

  public void setAppplication(UUID appplication) {
    
    this.appplication = appplication;
  }

  public String getKey() {
    
    return key;
  }

  public void setKey(String key) {
   
    this.key = key;
  }

}