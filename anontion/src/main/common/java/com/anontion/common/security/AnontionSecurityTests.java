package com.anontion.common.security;

import java.util.Base64;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

public class AnontionSecurityTests {

  public static boolean convertionKeyToStringToKeyToStringCompare() {

    ECPublicKeyParameters k1 = AnontionSecurity.pub();

    {

      ECPoint q = k1.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();
      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      System.out.println("k1 Public Key Parameters:");
      System.out.println("X: " + x.toString(16));
      System.out.println("Y: " + y.toString(16));
      System.out.println("Curve: " + k1.getParameters().getCurve());
      System.out.println("G (generator): " + k1.getParameters().getG());
      System.out.println("N (order): " + k1.getParameters().getN().toString(16));
      System.out.println("H (cofactor): " + k1.getParameters().getH());
    }
    
    String s1 = AnontionSecurity.encodePubK1XYUncompressed(k1);
    
    ECPublicKeyParameters k2 = AnontionSecurity.decodeECPublicKeyParametersFromBase64XYUncompressed(s1);
    
    {
      ECPoint q = k2.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();
      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      System.out.println("k2 Public Key Parameters:");
      System.out.println("X: " + x.toString(16));
      System.out.println("Y: " + y.toString(16));
      System.out.println("Curve: " + k2.getParameters().getCurve());
      System.out.println("G (generator): " + k2.getParameters().getG());
      System.out.println("N (order): " + k2.getParameters().getN().toString(16));
      System.out.println("H (cofactor): " + k2.getParameters().getH());
    }
    
    String s2 = AnontionSecurity.encodePubK1XYUncompressed(k2);

    System.out.println("DEBUG Key as string k1 " + s1 + "' k2 '" + s2 + "'");

    if (s1.compareTo(s2) == 0) {

      return true;
    }
    
    return false;
  }

  public static boolean conversionStringToKeyToStringCompare() {
    
    String xyEncodedUncompressedRawBase64String = "BAD/yB0qBYQNJGLQ0VSMdhFlNXD1Qil+RVVwd1EUP/n5V+mClQSDl65Qtchr6QSY3450GU4GN94Xic7PUdi4/Uk=";
    
    ECPublicKeyParameters k1 = AnontionSecurity.decodeECPublicKeyParametersFromBase64XYUncompressed(xyEncodedUncompressedRawBase64String);

    {
      ECPoint q = k1.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();
      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      System.out.println("k1 Public Key Parameters:");
      System.out.println("X: " + x.toString(16));
      System.out.println("Y: " + y.toString(16));
      System.out.println("Curve: " + k1.getParameters().getCurve());
      System.out.println("G (generator): " + k1.getParameters().getG());
      System.out.println("N (order): " + k1.getParameters().getN().toString(16));
      System.out.println("H (cofactor): " + k1.getParameters().getH());
    }
    
    String s1 = AnontionSecurity.encodePubK1XYUncompressed(k1);
    
    ECPublicKeyParameters k2 = AnontionSecurity.decodeECPublicKeyParametersFromBase64XYUncompressed(s1);

    {
      ECPoint q = k2.getQ();
      
      java.math.BigInteger x = q.getAffineXCoord().toBigInteger();
      java.math.BigInteger y = q.getAffineYCoord().toBigInteger();

      System.out.println("k2 Public Key Parameters:");
      System.out.println("X: " + x.toString(16));
      System.out.println("Y: " + y.toString(16));
      System.out.println("Curve: " + k2.getParameters().getCurve());
      System.out.println("G (generator): " + k2.getParameters().getG());
      System.out.println("N (order): " + k2.getParameters().getN().toString(16));
      System.out.println("H (cofactor): " + k2.getParameters().getH());
    }
    
    String s2 = AnontionSecurity.encodePubK1XYUncompressed(k2);

    System.out.println("DEBUG Key as string k1 " + s1 + "' k2 '" + s2 + "'");

    if (s1.compareTo(s2) == 0) {

      return true;
    }
    
    return false;
  }

}

