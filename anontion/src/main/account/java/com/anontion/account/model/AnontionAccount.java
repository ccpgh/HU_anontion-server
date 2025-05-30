package com.anontion.account.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

import com.anontion.common.misc.AnontionJson;
import com.anontion.common.security.AnontionSecurity;

@Entity
@Table(
  name = "AnontionAccount",
  uniqueConstraints = @UniqueConstraint(columnNames = {"ts", "name", "application"})
)
public class AnontionAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false)
  private Long ts;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private UUID application;

  @Column(name = "privatekey", columnDefinition = "VARCHAR(4096)", nullable = false)
  private String key;

  @Column(name = "publickey", columnDefinition = "VARCHAR(4096)", nullable = false)
  private String pub;

  @Column(nullable = false)
  private String hash;

  @Column(nullable = false)
  private String counter;
  
  public AnontionAccount() {
    
  }

  public AnontionAccount(Long ts, String name, UUID application, String pub, String hash, String counter) {

    this.ts = ts;
    this.name = name;
    this.application = application;
    this.key = AnontionSecurity.encodeKeyK1(AnontionSecurity.root());
    this.pub = pub;
    this.hash = hash;
    this.counter = counter;
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

  public String getCounter() {
    
    return counter;
  }

  public void setCounter(String counter) {
   
    this.counter = counter;
  }
  
  public String getHash() {
    
    return hash;
  }

  public void setHash(String hash) {
   
    this.hash = hash;
  }

  public String toString() {
    
    return AnontionJson.o2Json(this);
  }
}