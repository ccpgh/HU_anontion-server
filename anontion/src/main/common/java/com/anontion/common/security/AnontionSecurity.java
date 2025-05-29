package com.anontion.common.security;

import java.math.BigInteger;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;

abstract public class AnontionSecurity {

  private static SecureRandom _secureRandom = new SecureRandom();

  private static ECKeyPairGenerator _generator = getGenerator();
  
  private static AsymmetricCipherKeyPair _pair = getPair();
  
  private static ECPrivateKeyParameters _root = getRoot();

  private static ECPublicKeyParameters _pub = getPub();

  public static ECPrivateKeyParameters root() {
    
    return _root;
  }

  public static ECPublicKeyParameters pub() {
    
    return _pub;
  }

  public static String generateRandomString() {

    byte[] randomBytes = new byte[32];

    _secureRandom.nextBytes(randomBytes);

    return Base64.getEncoder().encodeToString(randomBytes);
  }

  private static ECKeyPairGenerator getGenerator() {
    
    ECNamedCurveParameterSpec curve = ECNamedCurveTable.getParameterSpec("secp256k1");

    ECDomainParameters domainParams = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH(), curve.getSeed());

    ECKeyGenerationParameters keyParams = new ECKeyGenerationParameters(domainParams, _secureRandom);

    ECKeyPairGenerator generator = new ECKeyPairGenerator();
    
    generator.init(keyParams);
    
    return generator;
  }
  
  private static AsymmetricCipherKeyPair getPair() {
    
    return _generator.generateKeyPair();
  }

  private static ECPrivateKeyParameters getRoot() {
    
    return (ECPrivateKeyParameters) _pair.getPrivate();
  }

  private static ECPublicKeyParameters getPub() {
    
    return (ECPublicKeyParameters)  _pair.getPublic();
  }
  
  public static String encodeKeyK1(ECPrivateKeyParameters key) {
    
    return key.getD().toString();
  }
  
  public static String encodePubK1Q(ECPublicKeyParameters pub) {
   
    return pub.getQ().toString();
  }

  public static String encodePubK1XY(PublicKey pub) {

    ECPublicKey ecPublicKey = (ECPublicKey) pub;
    
    java.security.spec.ECPoint publicKeyPoint = ecPublicKey.getW();

    BigInteger x = publicKeyPoint.getAffineX();

    BigInteger y = publicKeyPoint.getAffineY();
    
    byte[] xBytes = x.toByteArray();

    byte[] yBytes = y.toByteArray();
    
    byte[] x32Bytes = new byte[32];

    byte[] y32Bytes = new byte[32];
   
    int xOffset = 0;
    
    int yOffset = 0;

    if (xBytes.length == 33 && xBytes[0] == 0x00) {
      
      xOffset = 1;
    }
    
    if (yBytes.length == 33 && yBytes[0] == 0x00) {
      
      yOffset = 1;
    }

    System.arraycopy(xBytes, xOffset, x32Bytes, 32 - (xBytes.length - xOffset) , (xBytes.length - xOffset));
    
    System.arraycopy(yBytes, yOffset, y32Bytes, 32 - (yBytes.length - yOffset) , (yBytes.length - yOffset));

    byte[] publicKeyBytes = new byte[65];
    
    publicKeyBytes[0] = 0x04;
    
    System.arraycopy(x32Bytes, 0, publicKeyBytes, 1, 32);
    
    System.arraycopy(y32Bytes, 0, publicKeyBytes, 33, 32);

    return Base64.getEncoder().encodeToString(publicKeyBytes);
  }

  public static String encodePubK1XY(ECPublicKeyParameters pub) {
    
    org.bouncycastle.math.ec.ECPoint publicKeyPoint = pub.getQ();
        
    BigInteger x = publicKeyPoint.getAffineXCoord().toBigInteger();

    BigInteger y = publicKeyPoint.getAffineYCoord().toBigInteger();
    
    byte[] xBytes = x.toByteArray();

    byte[] yBytes = y.toByteArray();
    
    byte[] x32Bytes = new byte[32];

    byte[] y32Bytes = new byte[32];
   
    int xOffset = 0;
    
    int yOffset = 0;

    if (xBytes.length == 33 && xBytes[0] == 0x00) {
      
      xOffset = 1;
    }
    
    if (yBytes.length == 33 && yBytes[0] == 0x00) {
      
      yOffset = 1;
    }

    System.arraycopy(xBytes, xOffset, x32Bytes, 32 - (xBytes.length - xOffset) , (xBytes.length - xOffset));
    
    System.arraycopy(yBytes, yOffset, y32Bytes, 32 - (yBytes.length - yOffset) , (yBytes.length - yOffset));

    byte[] publicKeyBytes = new byte[65];
    
    publicKeyBytes[0] = 0x04;
    
    System.arraycopy(x32Bytes, 0, publicKeyBytes, 1, 32);
    
    System.arraycopy(y32Bytes, 0, publicKeyBytes, 33, 32);

    return Base64.getEncoder().encodeToString(publicKeyBytes);
  }

  public static ECPublicKeyParameters decodeECPublicKeyParametersFromBase64XY(String base64Key) {
    
    byte[] decodedKey = Base64.getDecoder().decode(base64Key);

    if (decodedKey.length != 65 || decodedKey[0] != 0x04) {
      
      return null;
    }
    
    byte[] xBytes = new byte[32];

    byte[] yBytes = new byte[32];
    
    System.arraycopy(decodedKey, 1, xBytes, 0, 32);
    
    System.arraycopy(decodedKey, 33, yBytes, 0, 32);

    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
    
    ECCurve curve = params.getCurve();

    org.bouncycastle.math.ec.ECPoint g = params.getG();

    BigInteger gx = g.getXCoord().toBigInteger();

    BigInteger gy = g.getYCoord().toBigInteger();
        
    try {

      org.bouncycastle.math.ec.ECPoint point = curve.createPoint(gx, gy);;

      ECDomainParameters domainParams = new ECDomainParameters(curve, params.getG(), params.getN(), params.getH());

      ECPublicKeyParameters key = new ECPublicKeyParameters(point, domainParams);
      
      System.out.println("DEBUG key = " + key);
      
      return key;
    
    } catch (Exception e) {
    
      e.printStackTrace();
    }


    return null;
  }
  
  public static PublicKey decodePublicKeyFromBase64XY(String base64Key) {

    byte[] decodedKey = Base64.getDecoder().decode(base64Key);

    if (decodedKey.length != 65 || decodedKey[0] != 0x04) {
      
      return null;
    }
    
    byte[] xBytes = new byte[32];

    byte[] yBytes = new byte[32];
    
    System.arraycopy(decodedKey, 1, xBytes, 0, 32);
    
    System.arraycopy(decodedKey, 33, yBytes, 0, 32);

    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
    
    java.security.spec.ECPoint ecPoint = new java.security.spec.ECPoint(new BigInteger(1, xBytes), new BigInteger(1, yBytes));

    ECCurve curve = params.getCurve();

    java.security.spec.ECFieldFp field = new java.security.spec.ECFieldFp(curve.getField().getCharacteristic());

    BigInteger a = curve.getA().toBigInteger();

    BigInteger b = curve.getB().toBigInteger();

    java.security.spec.EllipticCurve ellipticCurve = new java.security.spec.EllipticCurve(field, a, b);

    org.bouncycastle.math.ec.ECPoint g = params.getG();

    BigInteger gx = g.getXCoord().toBigInteger();

    BigInteger gy = g.getYCoord().toBigInteger();

    java.security.spec.ECPoint gPoint = new java.security.spec.ECPoint(gx, gy);

    BigInteger n = params.getN();

    BigInteger h = params.getH();
        
    ECParameterSpec ecSpec = new ECParameterSpec(ellipticCurve, gPoint, n, h.intValue());
    
    ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(ecPoint, ecSpec);
    
    try {

      KeyFactory keyFactory = KeyFactory.getInstance("EC");
    
      PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
      
      return publicKey;
    
    } catch (Exception e) {
    
      e.printStackTrace();
    }
    
    return null;
  }
  
  public static String hash(String name, Long cts, UUID client, String pub) {

    ECPrivateKeyParameters root = AnontionSecurity.root(); // TODO
    
    return "";
  }
  
  public static String sign(String hash) {

    ECPrivateKeyParameters root = AnontionSecurity.root(); // TODO
    
    return "";
  }

}


