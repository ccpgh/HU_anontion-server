package com.anontion.models.account.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

import com.anontion.common.misc.AnontionJson;
import com.anontion.common.security.AnontionSecurityECDSA;

@Entity
@Table(
  name = "anontion_account",
  uniqueConstraints = @UniqueConstraint(columnNames = {"ts", "name", "application"})
)
public class AnontionAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false)
  private Long ts;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false)
  private UUID application;

  @Column(name = "ekey", columnDefinition = "VARCHAR(4096)", nullable = false)
  private String key;

  @Column(nullable = false, length = 255)
  private String countersign;

  @Column(name = "epub", columnDefinition = "VARCHAR(4096)", nullable = false)
  private String pub;

  @Column(nullable = false, length = 255)
  private String uid;

  @Column(nullable = false)
  private boolean disabled;

  public AnontionAccount() {
    
  }

  public AnontionAccount(Long ts, String name, UUID application, String countersign, String pub, String uid, boolean disabled) {

    this.ts = ts;

    this.name = name;
    
    this.application = application;
    
    this.key = AnontionSecurityECDSA.encodeKeyK1(AnontionSecurityECDSA.root());
    
    this.countersign = countersign;
    
    this.pub = pub;
    
    this.uid = uid;
    
    this.disabled = disabled;
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

  public String getCountersign() {
    
    return countersign;
  }

  public void setCountersign(String countersign) {
   
    this.countersign = countersign;
  }
  
  public String getUid() {
    
    return uid;
  }

  public void setUid(String uid) {
   
    this.uid = uid;
  }
  
  public String toString() {
    
    return AnontionJson.o2Json(this);
  }
  
  public boolean getDisabled() {

    return disabled;
  }

  public void setDisabled(boolean disabled) {

    this.disabled = disabled;
  }
}
