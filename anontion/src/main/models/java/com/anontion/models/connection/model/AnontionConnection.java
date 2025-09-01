package com.anontion.models.connection.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(
  name = "anontion_connection"
)
public class AnontionConnection {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "connection_id")
  private UUID connectionId;

//  public AnontionConnection() {
//    
//  }

  public AnontionConnection() {

  }

  public UUID getAccountId() {
    
    return connectionId;
  }

  public void setAccountId(UUID connectionId) {
    
    this.connectionId = connectionId;
  }
}

