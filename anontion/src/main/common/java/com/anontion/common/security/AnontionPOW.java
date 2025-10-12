package com.anontion.common.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;

public class AnontionPOW {

  final private String _text;

  final private String _target;

  static private String CONST_MAX = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".toLowerCase();

  static private BigInteger CONST_BIGINT_MAX = new BigInteger(CONST_MAX, 16);

  static public String CONST_TARGET = "000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF".toLowerCase();

  static private Integer CONST_TEXT_INPUTLENGTH = 32;
  
  static final public double _ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT = 24 * 60 * 60; // 1 day
  
  public AnontionPOW(String target) {

    _text = generateText();

    if (!target.isBlank()) {
      
      _target = target;
      
    } else {
    
      _target = CONST_TARGET;
    }
  }

  private static String generateText() {

    SecureRandom secureRandom = new SecureRandom();

    byte[] randomBytes = new byte[ CONST_TEXT_INPUTLENGTH ];

    secureRandom.nextBytes(randomBytes);
    
    String base64 = Base64.getEncoder().encodeToString(randomBytes);

    return base64.substring(0, Math.min(CONST_TEXT_INPUTLENGTH, base64.length()));
  }
  
  public String getText() {
    
    return _text;
  }
  
  public String getTarget() {
    
    return _target;
  }
  
  public static boolean isApprovedTime(Long ts, Long now, Double timeout) {
    
    return ((now - ts) > timeout);
  }

  public static boolean isApprovedPow(String powLow, String powHigh, String powText, String powTarget, String powNonce) {

    try {
      
      BigInteger target = new BigInteger(powTarget, 16);
      
      BigInteger nonce = new BigInteger(powNonce, 16);

      String paddedNonce = AnontionStrings.lpad(nonce.toString(16), 64);

      byte[] data = (powText + paddedNonce).getBytes("UTF-8");

      byte[] hash1 = AnontionSecuritySHA.hashFromBytesToBytes(data);
      
      byte[] hash2 = AnontionSecuritySHA.hashFromBytesToBytes(hash1);

      BigInteger hexValue = new BigInteger(AnontionStrings.toHexString(hash2), 16);
      
      boolean isSolved = hexValue.compareTo(target) < 0;
      
      _logger.info("POW calc '" + AnontionStrings.lpad(hexValue.toString(16), 64) +
          "' target '" + powTarget + "' is solved '" + isSolved + "' @ nonce '" + nonce.toString(16) + "'");
      
      return isSolved;

    } catch (Exception e) {
      
      _logger.exception(e);
    }
    
    return false;
  }

  public static double calculateProgress(String powNonce) {
    
    try {

      BigInteger nonce = new BigInteger(powNonce, 16);
      
      BigInteger progress = nonce.divide(CONST_BIGINT_MAX);
      
      if (progress == BigInteger.ZERO) {
        
        return 1.0;
      }

      if (progress.intValue() > 100) {
        
        return 99;
      }
      
      return progress.intValue();
      
    } catch (Exception e) {
      
      _logger.exception(e);
    }
    
    return 0.0;
  }
  
  final private static AnontionLog _logger = new AnontionLog(AnontionPOW.class.getName());
}




