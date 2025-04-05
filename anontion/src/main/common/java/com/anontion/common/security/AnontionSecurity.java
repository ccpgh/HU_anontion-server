package com.anontion.common.security;

import java.math.BigInteger;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;

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

    ECKeyPairGenerator generator = new ECKeyPairGenerator();//("ECDSA");
    
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
  
  public static String encodePubK1(ECPublicKeyParameters pub) {
   
    return pub.getQ().toString();
  }
  
  public static PublicKey decodePublicKeyFromBase64XY(String base64Key) {

    byte[] decodedKey = Base64.getDecoder().decode(base64Key);

    if (!(decodedKey.length == 65 && decodedKey[0] == 0x04) && !(decodedKey.length == 33 && (decodedKey[0] == 0x02 || decodedKey[0] == 0x03))) {
      
      return null;
    }
    
    byte[] xBytes = new byte[32];

    byte[] yBytes = new byte[32];
    
    System.arraycopy(decodedKey, 1, xBytes, 0, 32);
    
    System.arraycopy(decodedKey, 33, yBytes, 0, 32);

    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
    
    ECPoint ecPoint = new ECPoint(new BigInteger(1, xBytes), new BigInteger(1, yBytes));

    ECCurve curve = params.getCurve();

    java.security.spec.ECFieldFp field = new java.security.spec.ECFieldFp(curve.getField().getCharacteristic());

    java.math.BigInteger a = curve.getA().toBigInteger();

    java.math.BigInteger b = curve.getB().toBigInteger();

    java.security.spec.EllipticCurve ellipticCurve = new java.security.spec.EllipticCurve(field, a, b);

    org.bouncycastle.math.ec.ECPoint g = params.getG();

    java.math.BigInteger gx = g.getXCoord().toBigInteger();

    java.math.BigInteger gy = g.getYCoord().toBigInteger();

    java.security.spec.ECPoint gPoint = new ECPoint(gx, gy);

    java.math.BigInteger n = params.getN();

    java.math.BigInteger h = params.getH();
        
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
  
//  private static ECPrivateKey getPrivateECDSAKey() {
//    
//    //ECNamedCurveParameterSpec curve = ECNamedCurveTable.getParameterSpec("secp256k1");
//
//    X9ECParameters curve = ECNamedCurveTable.getByName("secp256k1");
//
//    ECDomainParameters domainParams = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH(), curve.getSeed());
//
//    ECKeyGenerationParameters keyParams = new ECKeyGenerationParameters(domainParams, _secureRandom);
//
//    ECKeyPairGenerator generator = new ECKeyPairGenerator();//("ECDSA");
//    
//    generator.init(keyParams);
//    
//    AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();
//
//    ECPrivateKeyParameters privkey = (ECPrivateKeyParameters) keyPair.getPrivate();
//    
//    String privateKey = privkey.getD().toString();
//    
//    ECPublicKeyParameters pubkey = (ECPublicKeyParameters) keyPair.getPublic();
//    
//    String publicKey = pubkey.getQ().toString();
//    
//    return null;
//  }
//  
//  private static ECPublicKey getPublicECDSAKey_(ECPrivateKey key) {
//
////    ECParameterSpec ecSpec = key.getParams();
////
////    KeyFactory keyFactory;
////
////    try {
////    
////      keyFactory = KeyFactory.getInstance("EC");
////
////      return (ECPublicKey) keyFactory.generatePublic(new ECPublicKeySpec(ecSpec.getGenerator(), ecSpec));
////      
////    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
////
////      e.printStackTrace();
////    }
//    
//    return null;
//  }
  
//  public static ECPrivateKey generateSecp256k1PrivateKey_() {
//    
////    try {
////
////      X9ECParameters curveParams = ECNamedCurveTable.getByName("secp256k1");
////
////      SecureRandom random = new SecureRandom();
////
////      BigInteger privateKeyValue = new BigInteger(256, random).mod(curveParams.getN());
////
////      //public ECParameterSpec(EllipticCurve curve, ECPoint g,
////      //                       BigInteger n, int h) {
////      //  
////      //}
////      
////      EllipticCurve one = (EllipticCurve) curveParams.getCurve();
////      ECPoint two = curveParams.getG();
////          
////      ECParameterSpec ecSpec = new ECParameterSpec(
////        one, //,
////        two, //,
////        curveParams.getN(),
////        curveParams.getH().intValue()
////      );
////      
////      ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(privateKeyValue, ecSpec);
////
////      KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
////      
////      return (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);
////
////    } catch (Exception e) {
////    
////      e.printStackTrace();
////    }
//    
//    return null;
//  }

//  private static ECPrivateKey getPrivateECDSAKey_() {
//
////    try {
////
////      ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
////
////      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
////
////      keyPairGenerator.initialize(ecSpec, _secureRandom);
////
////      KeyPair keyPair = keyPairGenerator.generateKeyPair();
////
////      PrivateKey key = keyPair.getPrivate();
////
////      if (key instanceof ECPrivateKey) {
////
////        return (ECPrivateKey) key;
////      }
////
////      return null;
////
////    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
////
////      e.printStackTrace();
////    }
//
//    return null;
//  }


//  public static String encodeKeyK1_(ECPrivateKey key) {
//    
////    X9ECParameters curveParams = ECNamedCurveTable.getByName("secp256k1");
////
////    ECDomainParameters domainParameters = new ECDomainParameters(
////        curveParams.getCurve(), 
////        curveParams.getG(), 
////        curveParams.getN(), 
////        curveParams.getH()
////    );
////
////    BigInteger privateKey = key.getS();
////
////    ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKey, domainParameters);
////
////    return Base64.getEncoder().encodeToString(privateKeyParameters.getD().toByteArray());
//    
//    return null;
//  }

//  public static String encodeKeyR1_(ECPublicKey key) {
//
//    return Base64.getEncoder().encodeToString(key.getEncoded());
//  }
//  
//  public static ECPrivateKey decodeKey_(String s) {
//
////    byte[] decodedKey = Base64.getDecoder().decode(s);
////
////    try {
////
////      KeyFactory keyFactory = KeyFactory.getInstance("EC");
////
////      return (ECPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
////
////    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
////
////      e.printStackTrace();
////    }
//
//    return null;
//  }

//  public static ECPublicKey decodePub_(String s) {
//
////    byte[] decodedKey = Base64.getDecoder().decode(s);
////
////    try {
////
////      KeyFactory keyFactory = KeyFactory.getInstance("EC");
////
////      return (ECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
////
////    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
////
////      e.printStackTrace();
////    }
//
//    return null;
//  }
//
//  public static boolean isValidPub_(String s) {
//
//    //return decodePub(s) != null;
//
//    return false;
//  }
//
//  public static boolean isValidKey_(String s) {
//
//    //return decodeKey(s) != null;
//    
//    return false;
//  }
}


