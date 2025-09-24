package com.anontion.common.misc;

import java.util.ArrayList;

import org.springframework.util.DigestUtils;

public class AnontionStrings {

  private static String VALID_CHARACTERS_IN_NAME = "^[A-Za-z0-9- ]+$"; 
  
  private static Integer PASSWORD_MINCHARS = 8; // NYI agree with client
  private static Integer PASSWORD_MAXCHARS = 20; // NYI agree with client

  private static Integer USERNAME_MINCHARS = 2; // NYI agree with client
  private static Integer USERNAME_MAXCHARS = 20;  // NYI agree with client
      
  public static String concatLowercase(String[] tokens, String delimiter) {

    return concat(tokens, delimiter).toString().toLowerCase();
  }
  
  public static String concat(String[] tokens, String delimiter) {

    StringBuffer buffer = new StringBuffer();
    
    boolean first = true;
    
    for (String token: tokens) {
      
      if (!first) {
        
        buffer.append(delimiter);
      
      } else {
      
        first = false;
      }
      
      buffer.append(token);      
    } 
    
    return buffer.toString();
  }
  
  public static String[] split(String s, String delimiter) {

    ArrayList<String> buffer = new ArrayList<String>();
    
    for (String token : s.split(delimiter)) {

      buffer.add(token.trim());
    }
   
    return buffer.toArray(new String[0]);
  }
    
  public static boolean isValidName(String name) {

    if (name == null) {
      return false;
    }
        
    if (!name.matches(AnontionStrings.VALID_CHARACTERS_IN_NAME)) {
      return false;
    }
    
    if (name != name.trim()) {
      return false;
    }

    if (name.contains("  ")) {
      return false;
    }

    return name.length() >= USERNAME_MINCHARS && 
        name.length() <= USERNAME_MAXCHARS;
  }
  
  public static boolean isValidPassword(String password) {

    if (password == null) {
      return false;
    }
        
    if (!password.matches(AnontionStrings.VALID_CHARACTERS_IN_NAME)) {
      return false;
    }
    
    if (password != password.trim()) {
      return false;
    }

    if (password.contains("  ")) {
      return false;
    }

    return password.length() >= PASSWORD_MINCHARS && 
        password.length() <= PASSWORD_MAXCHARS;
  }
  
  public static String generateAsteriskMD5(String nonce, String realm, String password) {
    
    String md5Input = nonce + ":" + realm + ":" + password;
    
    return DigestUtils.md5DigestAsHex(md5Input.getBytes());
  }
  
  public static boolean isHex(String s) {
    
    if (s == null || s.isEmpty()) {
    
      return false;
    }

    for (char c : s.toCharArray()) {
      
      if (!((c >= '0' && c <= '9') ||
          (c >= 'a' && c <= 'f') ||
          (c >= 'A' && c <= 'F'))) {
      
        return false;
      }
    }

    return true;
  }
  
  public static String toHexString(byte[] bytes) {

    if (bytes == null || bytes.length == 0) {
      
      return "";
    }
    
    StringBuilder buffer = new StringBuilder();
    
    for (byte b : bytes) {
    
      buffer.append(String.format("%02x", b));
    }
    
    return buffer.toString();
  }

  public static String lpad(String s, int length) {

    return lpad(s, length, "0");
  }
  
  public static String lpad(String s, int length, String pad) {
    
    if (s.length() >= length) {
      
      return s;
    }

    String buffer = pad.repeat(length - s.length());
    
    return buffer + s;
  }
}
