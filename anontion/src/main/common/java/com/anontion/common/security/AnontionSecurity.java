package com.anontion.common.security;
 
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.util.DigestUtils;

import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;

import java.nio.ByteBuffer;

public class AnontionSecurity {

  private static final char[] _BASE93_CHARS = new char[93];

  private static final char[] _BASE64_CHARS = new char[64];

  private static final String _BASE64_EXCLUDE_CHARS = ":@/;?=&,\"<>#%[]'`_{}.^|!*()$\\"; // TODO missing hyphen 

  static {
  
    for (int i = 33; i < 126; i++) {
    
      _BASE93_CHARS[i - 33] = (char) i;
    }
    
    int offset = 0;
    
    for (int i = 33; i < 126; i++) {
    
      if ((i - (33 + offset)) > 64) {
      
        throw new RuntimeException("BASE64_CHARS would be too big");
      }
      
      if (_BASE64_EXCLUDE_CHARS.indexOf((char) i) != -1) {
        
        offset++;
        
      } else {
        
        _BASE64_CHARS[i - (33 + offset)] = (char) i;
      }
    }
  }
  
  private static final SecureRandom _random = new SecureRandom();

  public static String generateForeignKey() {
    
    byte[] bytes1 = ByteBuffer.allocate(Long.BYTES).putLong(
        System.currentTimeMillis()).array();
    byte[] bytes2 = ByteBuffer.allocate(Long.BYTES).putLong(
        Thread.currentThread().threadId()).array();
    byte[] bytes3 = generateRandomSequenceString(12, 13).getBytes();
    
    byte[] combined = 
        new byte[bytes1.length + bytes2.length + bytes3.length];

    System.arraycopy(
        bytes1, 0, combined, 0, bytes1.length);
    System.arraycopy(
        bytes2, 0, combined, bytes1.length, bytes2.length);
    System.arraycopy(
        bytes3, 0, combined, bytes1.length + bytes2.length, bytes3.length);

    return Base64.getUrlEncoder().encodeToString(combined);
  }
  
  public static String generatePassword() {
    
    return generateRandomSequenceString(20, 40);
  }
  
  public static String generateMD5Password(String nonce, String password) {
    
    String authType = "md5";
    
    String md5Input = nonce + ":asterisk:" + password;
    
    String md5Cred = DigestUtils.md5DigestAsHex(md5Input.getBytes());
    
    return md5Cred;
  }

  private static String generateRandomSequenceString(int low, int high) {
    
    StringBuilder buffer = new StringBuilder();

    int length = _random.nextInt(low, high);
    
    while (length > 0) {

      char c = (char) _random.nextInt(33, 126);
      
      if (_BASE64_EXCLUDE_CHARS.indexOf((char) c) != -1) {

        continue;
      }
      
      buffer.append(c);

      length--;
    }
    
    return buffer.toString();
  }

  public static String tobase64FromBytes(byte[] bytes) {

    StringBuilder buffer = new StringBuilder();
    
    BigInteger number = new BigInteger(1, bytes);

    BigInteger base64 = BigInteger.valueOf(_BASE64_CHARS.length);

    if (number.equals(BigInteger.ZERO)) {

      return String.valueOf(_BASE64_CHARS[0]);
    }

    while (number.compareTo(BigInteger.ZERO) > 0) {

      BigInteger[] r = number.divideAndRemainder(base64);

      buffer.append(_BASE64_CHARS[r[1].intValue()]);

      number = r[0];
    }

    return buffer.reverse().toString();
  }
    
  public static boolean isBase64(String base64) { // TODO improve = padding only

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
  
  public static boolean isSafeBase64(String base64) { // TODO improve = padding only

    if (base64.isEmpty()) {
    
      return false;
    }
    
    for (Character c : base64.toCharArray()) {
      
      if (!((c >= 'A' && c <= 'Z') ||
          (c >= 'a' && c <= 'z') ||
          (c >= '0' && c <= '9') ||
          c == '-' || 
          c == '_')) {
            
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
  
  public static String encodeToSafeBase64(String base64Encoded) {
    
    if (isSafeBase64(base64Encoded)) {
    
      return base64Encoded;
    }
    
    if (!isBase64(base64Encoded)) {
      
      return "";
    }
    
    //return base64Encoded.replace('/', '-').replace('+', '_').replace("=", ""); // TODO copy to revert tests
    return base64Encoded.replace('/', '-').replace('+', '_').replace("=", "");
  }
  
  public static String decodeFromSafeBase64(String safePubEncoded) {

    String buffer = safePubEncoded.replace('-', '/').replace('_', '+');

    if (!isBase64(buffer)) {
      
      return "";
    }

    int requiredPadding = (4 - (buffer.length() % 4)) % 4;

    return buffer + "=".repeat(requiredPadding);
  }
  
  public static String makeSafeIfRequired(String id) {
    
    if (AnontionSecurity.isBase64(id) && !AnontionSecurity.isSafeBase64(id)) {
      
      return AnontionSecurity.encodeToSafeBase64(id);
    }
    
    return id;
  }  
    
  public static String fromSipAddressToSipname(String sipAddress) {
    
    String buffer = sipAddress.trim();
    
    buffer = removeOpenBracket(removeCloseBracket(buffer));
        
    if (isPrefixPjsip(buffer)) {

      buffer = removePrefixPjsip(buffer);
    
    } else if (isPrefixSips(buffer)) {

      buffer = removePrefixSips(buffer);
    
    } else if (isPrefixSip(buffer)) {

      buffer = removePrefixSip(buffer);
    
    } else {
    
      _logger.info("DEBUG converted sipAddress '" + sipAddress + "' to Username '' - nothing");

      return "";
    }

    buffer = extractToColon(extractToSemiColon(
        extractToAt(buffer)));

    _logger.info("DEBUG converted sipAddress '" + sipAddress + "' to Username '" + buffer + "'");
    
    return buffer;
  }
  
  private static String extractToColon(String buffer) {
    
    return extractToChar(buffer, ':');
  }

  private static String extractToSemiColon(String buffer) {
    
    return extractToChar(buffer, ';');
  }

  private static String extractToAt(String buffer) {

    return extractToChar(buffer, '@');
  }
  
  private static String extractToChar(String buffer, char c) {

    int index = buffer.indexOf(c);
    
    if (index == 0) {
      
      return "";
      
    } 
    
    if (index > 0) {
      
      return buffer.substring(0, index);
    }
    
    return buffer;
  }
  
  private static String removeOpenBracket(String buffer) {
    
    if (!buffer.isEmpty() &&
        buffer.charAt(0) == AnontionConfig._CONTACT_CONNECTION_PJSIP_OPENBRACKET) {
      
      return buffer.substring(1);
    }
    
    return buffer;
  }

  private static String removeCloseBracket(String buffer) {
  
    if (!buffer.isEmpty() &&
        buffer.charAt(buffer.length()-1) == AnontionConfig._CONTACT_CONNECTION_PJSIP_CLOSEBRACKET) {
      
      return buffer.substring(0, buffer.length());
    }
    
    return buffer;
  }

  private static boolean isPrefixPjsip(String buffer) {

    return buffer.startsWith(AnontionConfig._CONTACT_CONNECTION_PJSIP_NAME);
  }

  private static String removePrefixPjsip(String buffer) {

    if (buffer.length() < AnontionConfig._CONTACT_CONNECTION_PJSIP_NAME.length()) {
    
      return "";
    }
    
    return buffer.substring(AnontionConfig._CONTACT_CONNECTION_PJSIP_NAME.length());
  }

  private static boolean isPrefixSips(String buffer) {

    return buffer.startsWith(AnontionConfig._CONTACT_CONNECTION_SIPS_PREFIX);
  }

  private static String removePrefixSips(String buffer) {

    if (buffer.length() < AnontionConfig._CONTACT_CONNECTION_SIPS_NAME.length()) {
    
      return "";
    }
    
    return buffer.substring(AnontionConfig._CONTACT_CONNECTION_SIPS_NAME.length());
  }

  private static boolean isPrefixSip(String buffer) {

    return buffer.startsWith(AnontionConfig._CONTACT_CONNECTION_SIP_PREFIX);
  }

  private static String removePrefixSip(String buffer) {

    if (buffer.length() < AnontionConfig._CONTACT_CONNECTION_SIP_NAME.length()) {
    
      return "";
    }
    
    return buffer.substring(AnontionConfig._CONTACT_CONNECTION_SIP_NAME.length());
  }
  
  final private static AnontionLog _logger = new AnontionLog(AnontionSecurity.class.getName());
}


