package com.anontion.asterisk.bean;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.anontion.asterisk.model.AsteriskAuth;

@Component
public class AsteriskAuthBean {

  AsteriskAuthBean() {
    
  }
  
  public AsteriskAuth createAsteriskAuth(String foreignKey1, String foreignKey2, String password) {
    
    String authType = "md5";
    
    String md5Input = foreignKey2 + ":asterisk:" + password;
    
    String md5Cred = DigestUtils.md5DigestAsHex(md5Input.getBytes());

    return new AsteriskAuth(foreignKey1, authType, foreignKey2, password, md5Cred, "asterisk");
  }
}
