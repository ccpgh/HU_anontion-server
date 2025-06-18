package com.anontion.asterisk.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "ps_auths")
public class AsteriskAuth {

  @Id
  @Column(nullable = false, unique = true, name = "id", length = 255)
  private String id;

  @Column(nullable = false, name = "auth_type", columnDefinition = "enum('md5','userpass','google_oauth')")
  @Pattern(regexp = "^md5|userpass|google_oauth$")
  private String authType;

  @Column(nullable = false, name = "username", length = 40)
  private String username;

  @Column(nullable = false, name = "password", length = 80)
  private String password;

  public AsteriskAuth() {

  }

  public AsteriskAuth(String id, String authType, String username, String password) {

    this.id = id;

    this.authType = authType;
    
    this.username = username;
    
    this.password = password;
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }

  public String getAuthType() {
    
    return authType;
  }

  public void setAuthType(String authType) {
    
    this.authType = authType;
  }

  public String getUsername() {
    
    return username;
  }

  public void setUsername(String username) {
    
    this.username = username;
  }

  public String getPassword() {
    
    return password;
  }

  public void setPassword(String password) {
    
    this.password = password;
  }
}
