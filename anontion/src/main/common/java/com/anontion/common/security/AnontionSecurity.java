package com.anontion.common.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.security.SecureRandom;

abstract public class AnontionSecurity {

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

      if (key instanceof ECPrivateKey) {

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

  public static String encodeKey(ECPublicKey key) {

    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  public static ECPrivateKey decodeKey(String s) {

    byte[] decodedKey = Base64.getDecoder().decode(s);

    try {

      KeyFactory keyFactory = KeyFactory.getInstance("EC");

      return (ECPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

      e.printStackTrace();
    }

    return null;
  }

  public static ECPublicKey decodePub(String s) {

    byte[] decodedKey = Base64.getDecoder().decode(s);

    try {

      KeyFactory keyFactory = KeyFactory.getInstance("EC");

      return (ECPublicKey) keyFactory.generatePublic(new PKCS8EncodedKeySpec(decodedKey));

    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

      e.printStackTrace();
    }

    return null;
  }

  public static boolean isValidPub(String s) {

    ECPublicKey key = decodePub(s);

    return key != null && isValidPubCurve(key) && isValidPubLength(key);
  }

  public static boolean isValidKey(String s) {

    ECPrivateKey key = decodeKey(s);

    return key != null && isValidKeyCurve(key) && isValidKeyLength(key);
  }

  public static boolean isValidKeyCurve(Key key) {

    return (key instanceof ECPrivateKey) && "secp256r1".equals(((ECPrivateKey) key).getParams().getCurve().toString());
  }

  public static boolean isValidPubCurve(Key key) {

    return (key instanceof ECPublicKey) && "secp256r1".equals(((ECPublicKey) key).getParams().getCurve().toString());
  }

  public static boolean isValidKeyLength(Key key) {

    return (key instanceof ECPrivateKey) && ((ECPrivateKey) key).getS().bitLength() == 256;
  }

  public static boolean isValidPubLength(Key key) {

    return (key instanceof ECPublicKey) && ((ECPublicKey) key).getEncoded().length == 65;
  }
}


