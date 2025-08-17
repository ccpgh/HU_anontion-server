package com.anontion.asterisk.bean;

import org.springframework.stereotype.Component;

import com.anontion.asterisk.model.AsteriskAuth;

@Component
public class AsteriskAuthBean {

  AsteriskAuthBean() {
    
  }
  
  public AsteriskAuth createAsteriskAuth(String foreignKey, String username, String password) {
    
    String authType = "userpass";
            
    return new AsteriskAuth(foreignKey, authType, username, password);
  }
}
