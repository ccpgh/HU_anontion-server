package com.anontion.account.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

//import java.util.UUID;

@Entity
public class AnontionAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String username;

  public Long getId() {
    
    return id;
  }

  public void setId(Long id) {

    this.id = id;
  }

  public String getUsername() {
    
    return username;
  }

  public void setUsername(String username) {
    
    this.username = username;
  }

}