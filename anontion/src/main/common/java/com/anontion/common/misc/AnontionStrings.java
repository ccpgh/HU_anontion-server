package com.anontion.common.misc;

public class AnontionStrings {

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
}
