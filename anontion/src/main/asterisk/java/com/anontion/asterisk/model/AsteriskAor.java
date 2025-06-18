package com.anontion.asterisk.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "ps_aors")
public class AsteriskAor {

  @Id
  @Column(nullable = false, unique = true, name = "id", length = 255)
  private String id;

  @Column(nullable = false, name = "max_contacts")
  private Integer maxContacts;

  @Column(nullable = false, name = "remove_existing", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String removeExisting;

  @Column(nullable = false, name = "qualify_frequency")
  private Integer qualifyFrequency;

  @Column(nullable = false, name = "support_path", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String supportPath;

  public AsteriskAor() {

  }

  public AsteriskAor(String id, Integer maxContacts, String removeExisting, Integer qualifyFrequency, String supportPath) {

    this.id = id;
    
    this.maxContacts = maxContacts;

    this.removeExisting = removeExisting;
    
    this.qualifyFrequency = qualifyFrequency;
    
    this.supportPath = supportPath;
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }

  public Integer getMaxContacts() {
    
    return maxContacts;
  }

  public void setMaxContacts(Integer maxContacts) {
    
    this.maxContacts = maxContacts;
  }

  public String getRemoveExisting() {
    
    return removeExisting;
  }

  public void setRemoveExisting(String removeExisting) {
    
    this.removeExisting = removeExisting;
  }

  public Integer getQualifyFrequency() {
    
    return qualifyFrequency;
  }

  public void setQualifyFrequency(Integer qualifyFrequency) {
    
    this.qualifyFrequency = qualifyFrequency;
  }

  public String getSupportPath() {
    
    return supportPath;
  }

  public void setSupportPath(String supportPath) {
    
    this.supportPath = supportPath;
  }
}
