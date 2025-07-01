package com.anontion.common.security;

import java.nio.charset.StandardCharsets;

import org.bouncycastle.crypto.generators.SCrypt;

import com.anontion.common.misc.AnontionLog;

abstract public class AnontionSecuritySCRYPT {

  public static String hashBase64(String s) {

    byte[] data = hash(s, null);
    
    if (data == null) {
      
      return "";
    }
    
    return AnontionSecurity.tobase64FromBytes(data);
  }
  
  private static byte[] hash(String s, byte[] salt) {
    
    byte[] sbytes = salt;

    if (sbytes == null) {
    
      try {
      
        sbytes = AnontionSecurity.generateSalt(32);
        
      } catch (Exception e) {
      
        _logger.exception(e);
        
        return null;  
      }
    }

    byte[] passwordBytes = s.getBytes(StandardCharsets.UTF_8);
    
    try {
    
      return SCrypt.generate(passwordBytes, sbytes, 16384, 8, 1, 30); // TODO 30?

    } catch (Exception e) {

      _logger.exception(e);

      return null;
    }
  }

  final private static AnontionLog _logger = new AnontionLog(AnontionSecuritySCRYPT.class.getName());
}



