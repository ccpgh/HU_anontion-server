package com.anontion.models.application.model;

import java.util.UUID;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "anontion_application")
@IdClass(AnontionApplicationId.class)
public class AnontionApplication {

  @Column(nullable = false, unique = true)
  private UUID id;

  @Id
  @Column(nullable = false, length = 255)
  private String name;

  @Id
  @Column(nullable = false)
  private Long ts;

  @Id
  @Column(nullable = false)
  private UUID client;

  @Column(nullable = false, length = 255) 
  private String pub;

  @Column(nullable = false, length = 255) 
  private String plaintext;

  @Column(nullable = false, length = 255) 
  private String sign;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String encrypt;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String text;

  @Column(nullable = false) 
  private Long target;

  public AnontionApplication() {
    
  }

  public AnontionApplication(String name, Long ts, UUID client, String pub, String plaintext, String sign, String text, Long target, String encrypt) {

    this.id = UUID.randomUUID();

    this.name = name;
    
    this.ts = ts;
    
    this.client = client;
    
    this.pub = pub;
    
    this.plaintext = plaintext;
    
    this.sign = sign;
    
    this.text = text;
    
    this.target = target;
    
    this.encrypt = encrypt;
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

  public Long getTs() {
    
    return ts;
  }

  public void setTs(Long ts) {
    
    this.ts = ts;
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
  
  
  public String getPlaintext() {

    return plaintext;
  }

  public void setHash(String plaintext) {

    this.plaintext = plaintext;
  }
  
  
  public String getSign() {

    return sign;
  }

  public void setSign(String sign) {

    this.sign = sign;
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

  public String getEncrypt() {

    return encrypt;
  }

  public void setEncrypt(String encrypt) {

    this.encrypt = encrypt;
  }

}
