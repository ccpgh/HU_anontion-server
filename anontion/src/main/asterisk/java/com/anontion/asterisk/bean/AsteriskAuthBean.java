package com.anontion.asterisk.bean;

import org.springframework.stereotype.Component;

import com.anontion.asterisk.model.AsteriskAuth;

@Component
public class AsteriskAuthBean {

  AsteriskAuthBean() {
    
  }
  
  public AsteriskAuth createAsteriskAuth(String foreignKey1, String foreignKey2, String md5Password, String realm, String authType) {
      
    return new AsteriskAuth(foreignKey1, 
        foreignKey2, 
        md5Password, 
        realm,
        authType);
  }
}

