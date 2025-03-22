package com.anontion.common.security;

import java.security.SecureRandom;
import java.util.Base64;

public class AnontionSecurity {

  public static String generateRandomString() {
    
    SecureRandom secureRandom = new SecureRandom();

    byte[] randomBytes = new byte[32];
    
    secureRandom.nextBytes(randomBytes);

    return Base64.getEncoder().encodeToString(randomBytes);
  }
}











