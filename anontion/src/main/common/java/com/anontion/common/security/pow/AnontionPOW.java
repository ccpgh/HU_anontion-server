package com.anontion.common.security.pow;

import java.security.SecureRandom;
import java.util.Base64;

public class AnontionPOW {

  final private String text;

  final private Long target;

  static private Long CONST_TARGET = 1000000000000L;

  static private Integer CONST_TEXT_LENGTH = 1024;

  static private final String BASE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_!$&*+";

  static private final Integer BASE_ALPHABET_LENGTH = BASE_ALPHABET.length();
  
  public AnontionPOW() {

    text = generateText();

    target = CONST_TARGET;
  }

  private static String generateText() {

    SecureRandom secureRandom = new SecureRandom();

    byte[] randomBytes = new byte[ CONST_TEXT_LENGTH ];

    secureRandom.nextBytes(randomBytes);

    return Base64.getEncoder().encodeToString(randomBytes);
  }

  static {

    if (AnontionPOW.CONST_TEXT_LENGTH % 8 != 0) {

      throw new IllegalArgumentException("CONST_TEXT_LENGTH not divisible by 8");
    }
  }

  public static String encode(byte[] bytes) {

    StringBuilder buffer = new StringBuilder();
    
    for (byte b : bytes) {
    
      buffer.append(BASE_ALPHABET.charAt((b & 0xFF) % BASE_ALPHABET_LENGTH));
    }

    return buffer.toString();
  }
  
  public String getText() {
    
    return text;
  }
  
  public Long getTarget() {
    
    return target;
  }
}











