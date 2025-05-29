package com.anontion.application.model;

import java.util.UUID;

import com.anontion.common.misc.AnontionTime;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;

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
  private Long cts;

  @Id
  @Column(nullable = false)
  private UUID client;

  @Column(nullable = false) 
  private String pub;

  @Column(nullable = false) 
  private String hash;

  @Column(nullable = false) 
  private String sign;

  @Column(nullable = false) 
  private Long ts;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String text;

  @Column(nullable = false) 
  private Long target;

  public AnontionApplication() {
    
  }

  public AnontionApplication(String name, Long cts, UUID client, String pub, String hash, String sign, String text, Long target) {

    this.id = UUID.randomUUID();
    this.name = name;
    this.cts = cts;
    this.client = client;
    this.pub = pub;
    this.hash = hash;
    this.sign = sign;
    this.text = text;
    this.target = target;
    this.ts = AnontionTime.ts(); 
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

  public Long getCts() {
    
    return cts;
  }

  public void setCts(Long cts) {
    
    this.cts = cts;
  }

  public UUID getClient() {
    
    return client;
  }

  public void setClient(UUID client) {
    
    this.client = client;
  }

  public String getPub() {

    return pub;
  }

  public void setPub(String pub) {

    this.pub = pub;
  }
  
  
  public String getHash() {

    return hash;
  }

  public void setHash(String hash) {

    this.hash = hash;
  }
  
  
  public String getSign() {

    return sign;
  }

  public void setSign(String sign) {

    this.sign = sign;
  }
  
  
  public Long getTs() {
    
    return ts;
  }

  public void setTs(Long ts) {
    
    this.ts = ts;
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