package com.anontion.asterisk.bean;

import org.springframework.stereotype.Component;

import com.anontion.asterisk.model.AsteriskAor;

@Component
public class AsteriskAorBean {

  AsteriskAorBean() {
    
  }
  
  public AsteriskAor createAsteriskAor(String id) {
    
    Integer maxContacts = 1;
    
    String removeExisting = "yes";
    
    Integer qualifyFrequency = 30;
    
    String supportPath = "yes";
    
    return new AsteriskAor(id, maxContacts , removeExisting, qualifyFrequency, supportPath);
  }  
}
