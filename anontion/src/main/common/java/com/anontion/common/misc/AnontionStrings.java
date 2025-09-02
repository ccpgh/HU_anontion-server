package com.anontion.common.misc;

import java.util.ArrayList;

public class AnontionStrings {

  private static String VALID_CHARACTERS_IN_NAME = "^[A-Za-z0-9- ]+$"; 
  
  private static Integer PASSWORD_MINCHARS = 8; // NYI agree with client
  private static Integer PASSWORD_MAXCHARS = 20; // NYI agree with client

  private static Integer USERNAME_MINCHARS = 2; // NYI agree with client
  private static Integer USERNAME_MAXCHARS = 20;  // NYI agree with client
      
  public static String concat(String[] tokens, String delimiter) {

    StringBuffer buffer = new StringBuffer();
    
    boolean first = true;
    
    for (String token: tokens) {
      
      if (!first) {
        
        buffer.append(delimiter);
      
      } else {
      
        first = false;
      }
      
      buffer.append(token.toLowerCase());      
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
}
