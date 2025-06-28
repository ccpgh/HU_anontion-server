package com.anontion.common.misc;

import java.util.ArrayList;

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
  
  public static String[] split(String s, String delimiter) {

    ArrayList<String> buffer = new ArrayList<String>();
    
    for (String token : s.split(delimiter)) {

      buffer.add(token.trim());
    }
   
    return buffer.toArray(new String[0]);
  }
}
