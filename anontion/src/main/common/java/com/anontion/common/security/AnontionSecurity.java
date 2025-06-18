package com.anontion.common.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

import com.anontion.common.misc.AnontionLog;

public class AnontionSecurity {

  private static final char[] _BASE93_CHARS = new char[93];

  static {
  
    for (int i = 33; i < 126; i++) {
    
      _BASE93_CHARS[i - 33] = (char) i;
    }
  }
  
  private static final SecureRandom _random = new SecureRandom();

  public static String generatePassword() {
    
    StringBuilder buffer = new StringBuilder();

    int length = _random.nextInt(8, 12);
    
    while (length > 0) {
      
      buffer.append((char) _random.nextInt(33, 126));

      length--;
    }
    
    return buffer.toString();
  }

  public static String tobase93FromBase64(String base64) {
    
    for (Character c : base64.toCharArray()) {
      
      if ((c >= 'A' && c <= 'Z') ||
          (c >= 'a' && c <= 'z') ||
          (c >= '0' && c <= '9') ||
          c == '+' || 
          c == '/' || 
          c == '=') {
            
        _logger.severe("Invaliid base64 '" + base64 + "'");
        
        return "";
      }
    }
    
    StringBuilder buffer = new StringBuilder();
    
    byte[] bytes = Base64.getDecoder().decode(base64);

    BigInteger number = new BigInteger(1, bytes);

    BigInteger base93 = BigInteger.valueOf(_BASE93_CHARS.length);

    if (number.equals(BigInteger.ZERO)) {

      return String.valueOf(_BASE93_CHARS[0]);
    }

    while (number.compareTo(BigInteger.ZERO) > 0) {
      
      BigInteger[] r = number.divideAndRemainder(base93);
      
      buffer.append(_BASE93_CHARS[r[1].intValue()]);
      
      number = r[0];
    }

    return buffer.reverse().toString();
  }

  @SuppressWarnings("unused")
  final private static AnontionLog _logger = new AnontionLog(AnontionSecurity.class.getName());
}

