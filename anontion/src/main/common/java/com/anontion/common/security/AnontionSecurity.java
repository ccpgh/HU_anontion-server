package com.anontion.common.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.security.SecureRandom;

public class AnontionSecurity {

  private static SecureRandom _secureRandom = new SecureRandom();

  public static String generateRandomString() {

    byte[] randomBytes = new byte[32];

    _secureRandom.nextBytes(randomBytes);

    return Base64.getEncoder().encodeToString(randomBytes);
  }

  public static ECPrivateKey getPrivateECDSAKey() {

    try {

      ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
      
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");

      keyPairGenerator.initialize(ecSpec, _secureRandom);

      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      PrivateKey key = keyPair.getPrivate();
      
      if (key instanceof ECPrivateKey ) {
        
        return (ECPrivateKey) key;
      }

      return null;

    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
    
      e.printStackTrace();        
    }
    
    return null;
  }
  
  public static String encodeKey(ECPrivateKey key) {
    
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }
  
  public static ECPrivateKey decodeKey(String key) {

    byte[] decodedKey = Base64.getDecoder().decode(key);

    KeyFactory keyFactory;
    
    try {
    
      keyFactory = KeyFactory.getInstance("EC");

      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
      
      ECPrivateKey privateKey = (ECPrivateKey) keyFactory.generatePrivate(keySpec);
      
      return privateKey;

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

      e.printStackTrace();
    }
    
    return null;    
  }

}

