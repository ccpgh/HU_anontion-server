package com.anontion.common.security;

import java.security.SecureRandom;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

import com.anontion.common.misc.AnontionLog;

public class AnontionSecurityTests {

  private static final String _CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  private static final SecureRandom _random = new SecureRandom();

  public static boolean run() {
    
    return AnontionSecurityTests.TEST_convertionKeyToStringToKeyToStringCompare() &&
        AnontionSecurityTests.TEST_conversionStringToKeyToStringCompare() &&
        AnontionSecurityTests.TEST_localSignAndCheck() &&
        AnontionSecurityTests.TEST_encryptDecrypt();
  }
  
  public static boolean TEST_convertionKeyToStringToKeyToStringCompare() {

    ECPublicKeyParameters k1 = AnontionSecurityECDSA.pub();

    
    String s1 = AnontionSecurityECDSA.encodePubK1XY(k1);
    
    ECPublicKeyParameters k2 = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(s1);
    
    String s2 = AnontionSecurityECDSA.encodePubK1XY(k2);

    if (s1.compareTo(s2) == 0) {

      _logger.info("TEST convertionKeyToStringToKeyToStringCompare OK");

      return true;
    }

    _logger.severe("TEST convertionKeyToStringToKeyToStringCompare FAILED");

    {

      ECPoint q = k1.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();
      
      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      _logger.info("TEST k1 Public Key Parameters:");

      _logger.info("TEST X: " + x.toString(16));
      
      _logger.info("TEST Y: " + y.toString(16));
      
      _logger.info("TEST Curve: " + k1.getParameters().getCurve());
      
      _logger.info("TEST G (generator): " + k1.getParameters().getG());
      
      _logger.info("TEST N (order): " + k1.getParameters().getN().toString(16));
      
      _logger.info("TEST H (cofactor): " + k1.getParameters().getH());
    }
    {
      ECPoint q = k2.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();

      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      _logger.info("TEST k2 Public Key Parameters:");
      
      _logger.info("TEST X: " + x.toString(16));
      
      _logger.info("TEST Y: " + y.toString(16));
      
      _logger.info("TEST Curve: " + k2.getParameters().getCurve());
      
      _logger.info("TEST G (generator): " + k2.getParameters().getG());
      
      _logger.info("TEST N (order): " + k2.getParameters().getN().toString(16));
      
      _logger.info("TEST H (cofactor): " + k2.getParameters().getH());
    }
    
    return false;
  }

  public static boolean TEST_conversionStringToKeyToStringCompare() {
    
    String xyEncodedUncompressedRawBase64String = "BAD/yB0qBYQNJGLQ0VSMdhFlNXD1Qil+RVVwd1EUP/n5V+mClQSDl65Qtchr6QSY3450GU4GN94Xic7PUdi4/Uk=";
    
    ECPublicKeyParameters k1 = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(xyEncodedUncompressedRawBase64String);
    
    String s1 = AnontionSecurityECDSA.encodePubK1XY(k1);
    
    ECPublicKeyParameters k2 = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(s1);
    
    String s2 = AnontionSecurityECDSA.encodePubK1XY(k2);

    if (s1.compareTo(s2) == 0) {

      _logger.info("TEST conversionStringToKeyToStringCompare OK");

      return true;
    }
    
    _logger.severe("TEST conversionStringToKeyToStringCompare FAILED");

    {
      ECPoint q = k1.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();
      
      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      _logger.info("TEST k1 Public Key Parameters:");

      _logger.info("TEST X: " + x.toString(16));
    
      _logger.info("TEST Y: " + y.toString(16));
    
      _logger.info("TEST Curve: " + k1.getParameters().getCurve());
    
      _logger.info("TEST G (generator): " + k1.getParameters().getG());
    
      _logger.info("TEST N (order): " + k1.getParameters().getN().toString(16));
      
      _logger.info("TEST H (cofactor): " + k1.getParameters().getH());
    }
    {
      ECPoint q = k2.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();
      
      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      _logger.info("TEST k2 Public Key Parameters:");

      _logger.info("TEST X: " + x.toString(16));
      
      _logger.info("TEST Y: " + y.toString(16));
      
      _logger.info("TEST Curve: " + k2.getParameters().getCurve());
      
      _logger.info("TEST G (generator): " + k2.getParameters().getG());
      
      _logger.info("TEST N (order): " + k2.getParameters().getN().toString(16));
      
      _logger.info("TEST H (cofactor): " + k2.getParameters().getH());
    }

    return false;
  }

  public static boolean TEST_localSignAndCheck() {
    
    String plaintext = "nvsbv8lnjc3oj0oncp2jekgobrmxwuxoxu0qdf6ams4=:1749089207:b438b97c-5559-4da9-801e-25fd376b538a:bcsym1ahva6n3tcyrtrw//tdc0fh+qsx1fwbdal/gb2mx4qrtbod1bmsxk3ki/flklnvlzd9u0pb5ffz74hnulq=";

    String sign = AnontionSecurityECDSA.sign(plaintext);
    
    if (AnontionSecurityECDSA.check(plaintext, sign, AnontionSecurityECDSA.pub())) {
      
      _logger.info("TEST localSignAndCheck OK");
      
      return true;
    }

    _logger.severe("TEST localSignAndCheck FAILED");

    return false;
  }
   
  public static boolean TEST_encryptDecrypt() {
    
    int length = 1 + _random.nextInt(400);
    
    StringBuilder buffer = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
    
      buffer.append(_CHARACTERS.charAt(_random.nextInt(_CHARACTERS.length())));
    }

    String plaintext = buffer.toString();
    
    String encrypted = AnontionSecurityECIES_ECDH.encrypt(AnontionSecurityECDSA.pub(), buffer.toString());

    String decrypted = AnontionSecurityECIES_ECDH.decrypt(AnontionSecurityECDSA.root(), encrypted);

    if (plaintext.equals(decrypted)) {
      
      _logger.info("TEST encryptDecrypt OK");

      return true;
    }
    
    _logger.severe("TEST encryptDecrypt FAILED");
    
    return false;
  }

  final private static AnontionLog _logger = new AnontionLog(AnontionSecurityTests.class.getName());
}



