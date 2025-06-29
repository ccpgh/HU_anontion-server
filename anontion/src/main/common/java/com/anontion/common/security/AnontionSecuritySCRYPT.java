package com.anontion.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.bouncycastle.crypto.generators.SCrypt;

import com.anontion.common.misc.AnontionLog;

abstract public class AnontionSecuritySCRYPT {

  public static String hash(String s) {

    return hash(s, null);
  }
  
  public static String hash(String s, byte[] salt) {
    
    byte[] sbytes = salt;

    if (sbytes == null) {
    
      try {
      
        sbytes = AnontionSecurity.generateSalt(32);
        
      } catch (Exception e) {
      
        _logger.exception(e);
        
        return "";  
      }
    }

    byte[] passwordBytes = s.getBytes(StandardCharsets.UTF_8);
    
    try {
    
      byte[] hash = SCrypt.generate(passwordBytes, sbytes, 16384, 8, 1, 31);

      return Base64.getEncoder().encodeToString(hash);

    } catch (Exception e) {

      _logger.exception(e);

      return "";
    }
  }

  final private static AnontionLog _logger = new AnontionLog(AnontionSecuritySCRYPT.class.getName());
}



