package com.anontion.application.model;

import java.io.Serializable;

import java.util.UUID;

public class AnontionApplicationId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;

  private Long ts;
  
  private UUID client;
  
  public AnontionApplicationId() {}

  public AnontionApplicationId(String name, Long ts, UUID client) {

    this.name = name;

    this.ts = ts;
    
    this.client = client;
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

  @Override
  public boolean equals(Object o) {

    if (this == o) {

      return true;
    }

    if (o == null || getClass() != o.getClass()) {

      return false;
    }

    AnontionApplicationId that = (AnontionApplicationId) o;

    return name.equals(that.name) && ts.equals(that.ts) && client.equals(that.client);
  }

  @Override
  public int hashCode() {

    return 31 * (name.hashCode() + ts.hashCode() + client.hashCode());
  }
}