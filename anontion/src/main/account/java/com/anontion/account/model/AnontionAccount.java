package com.anontion.account.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.UUID;

import com.anontion.common.misc.AnontionJson;
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
  private UUID application;

  @Column(name = "privatekey", columnDefinition = "VARCHAR(4096)", nullable = false)
  private String key;

  @Column(name = "publickey", columnDefinition = "VARCHAR(4096)", nullable = false)
  private String pub;

  public AnontionAccount() {
    
  }

  public AnontionAccount(Integer ts, String name, UUID application, String pub) {

    this.ts = ts;
    this.name = name;
    this.application = application;
    this.key = AnontionSecurity.encodeKey(AnontionSecurity.getPrivateECDSAKey());
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

  public String getKey() {
    
    return key;
  }

  public void setKey(String key) {
   
    this.key = key;
  }

  public String getPub() {
    
    return pub;
  }

  public void setPub(String pub) {
   
    this.pub = pub;
  }

  public String toString() {
    
    return AnontionJson.o2Json(this);
  }
}