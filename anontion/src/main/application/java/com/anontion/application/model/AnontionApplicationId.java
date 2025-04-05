package com.anontion.application.model;

import java.io.Serializable;

import java.util.UUID;

public class AnontionApplicationId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private Integer cts;
  private UUID client;

  public AnontionApplicationId() {}

  public AnontionApplicationId(String name, Integer cts, UUID client) {

    this.name = name;
    this.cts = cts;
    this.client = client;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public Integer getCts() {

    return cts;
  }

  public void setCts(Integer cts) {

    this.cts = cts;
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

    return name.equals(that.name) && cts.equals(that.cts) && client.equals(that.client);
  }

  @Override
  public int hashCode() {

    return 31 * (name.hashCode() + cts.hashCode() + client.hashCode());
  }
}