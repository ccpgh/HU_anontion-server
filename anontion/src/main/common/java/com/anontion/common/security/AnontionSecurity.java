package com.anontion.common.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

import com.anontion.common.misc.AnontionLog;

public class AnontionSecurity {

  private static final char[] _BASE93_CHARS = new char[93];

  private static final char[] _BASE76_CHARS = new char[76];

  private static final String _BASE76_EXCLUDE_CHARS = ":@/;?=&,\"<>#%[]'`";

  static {
  
    for (int i = 33; i < 126; i++) {
    
      _BASE93_CHARS[i - 33] = (char) i;
    }
    
    int offset = 0;
    
    for (int i = 33; i < 126; i++) {
    
      if ((i - (33 + offset)) > 75) {
      
        throw new RuntimeException("BASE76_CHARS would be too big");
      }
      
      if (_BASE76_EXCLUDE_CHARS.indexOf((char) i) != -1) {
        
        offset++;
        
      } else {
        
        _BASE76_CHARS[i - (33 + offset)] = (char) i;
      }
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
    
    if (!isBase64(base64)) {

      _logger.severe("not base64 '" + base64 + "'");

      return "";
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
  
  public static String tobase76FromBase64(String base64) {
   
    if (!isBase64(base64)) {

      _logger.severe("not base64 '" + base64 + "'");

      return "";
    }
    
    StringBuilder buffer = new StringBuilder();
    
    byte[] bytes = Base64.getDecoder().decode(base64);

    BigInteger number = new BigInteger(1, bytes);

    BigInteger base76 = BigInteger.valueOf(_BASE76_CHARS.length);

    if (number.equals(BigInteger.ZERO)) {

      return String.valueOf(_BASE76_CHARS[0]);
    }

    while (number.compareTo(BigInteger.ZERO) > 0) {
      
      BigInteger[] r = number.divideAndRemainder(base76);
      
      buffer.append(_BASE76_CHARS[r[1].intValue()]);
      
      number = r[0];
    }

    return buffer.reverse().toString();
  }
  
  public static boolean isBase64(String base64) {

    if (base64.isEmpty()) {
    
      return false;
    }
    
    for (Character c : base64.toCharArray()) {
      
      if (!((c >= 'A' && c <= 'Z') ||
          (c >= 'a' && c <= 'z') ||
          (c >= '0' && c <= '9') ||
          c == '+' || 
          c == '/' || 
          c == '=')) {
            
        return false;
      }
    }
    
    return true;
  }
  
  public static byte[] generateSalt(int length) throws Exception {
   
    byte[] salt = new byte[length];
    
    _random.nextBytes(salt);
    
    return salt;
  }
  
  final private static AnontionLog _logger = new AnontionLog(AnontionSecurity.class.getName());
}

