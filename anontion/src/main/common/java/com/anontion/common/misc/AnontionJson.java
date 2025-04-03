package com.anontion.common.misc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

abstract public class AnontionJson {

  public static String o2Json(Object o) {
    
    ObjectMapper objectMapper = new ObjectMapper();

    String requestJson = null;

    try {
      
      requestJson = objectMapper.writeValueAsString(o);
    
    } catch (JsonProcessingException e) {

      e.printStackTrace();
    } 
    
    return requestJson;
  }

}
