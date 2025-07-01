package com.anontion.common.security;
 
import java.math.BigInteger;
import java.security.SecureRandom;

public class AnontionSecurity {

  private static final char[] _BASE93_CHARS = new char[93];

  private static final char[] _BASE71_CHARS = new char[71];

  private static final String _BASE71_EXCLUDE_CHARS = ":@/;?=&,\"<>#%[]'`_{}.\\";

  static {
  
    for (int i = 33; i < 126; i++) {
    
      _BASE93_CHARS[i - 33] = (char) i;
    }
    
    int offset = 0;
    
    for (int i = 33; i < 126; i++) {
    
      if ((i - (33 + offset)) > 75) {
      
        throw new RuntimeException("BASE71_CHARS would be too big");
      }
      
      if (_BASE71_EXCLUDE_CHARS.indexOf((char) i) != -1) {
        
        offset++;
        
      } else {
        
        _BASE71_CHARS[i - (33 + offset)] = (char) i;
      }
    }
  }
  
  private static final SecureRandom _random = new SecureRandom();

  public static String generatePassword() {
    
    StringBuilder buffer = new StringBuilder();

    int length = _random.nextInt(8, 12);
    
    while (length > 0) {

      char c = (char) _random.nextInt(33, 126);
      
      if (_BASE71_EXCLUDE_CHARS.indexOf((char) c) != -1) {

        continue;
      }
      
      buffer.append(c);

      length--;
    }
    
    return buffer.toString();
  }
  
  public static String tobase71FromBytes(byte[] bytes) {

    StringBuilder buffer = new StringBuilder();
    
    BigInteger number = new BigInteger(1, bytes);

    BigInteger base71 = BigInteger.valueOf(_BASE71_CHARS.length);

    if (number.equals(BigInteger.ZERO)) {

      return String.valueOf(_BASE71_CHARS[0]);
    }

    while (number.compareTo(BigInteger.ZERO) > 0) {

      BigInteger[] r = number.divideAndRemainder(base71);

      buffer.append(_BASE71_CHARS[r[1].intValue()]);

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
}

