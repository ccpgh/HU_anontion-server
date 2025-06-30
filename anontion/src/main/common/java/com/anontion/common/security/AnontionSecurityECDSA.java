package com.anontion.common.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings; 

abstract public class AnontionSecurityECDSA {

  private static SecureRandom _secureRandom = new SecureRandom();

  private static ECPrivateKeyParameters _root = AnontionSecurityECDSA.load();

  private static ECPublicKeyParameters _pub = new ECPublicKeyParameters(_root.getParameters().getG().multiply(_root.getD()).normalize(), _root.getParameters());
  
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

  public static String encodeKeyK1(ECPrivateKeyParameters key) {

    return key.getD().toString();
  }

  public static String encodePubK1Q(ECPublicKeyParameters pub) {

    return pub.getQ().toString();
  }

  private static ECPrivateKeyParameters load() {
    
    InputStream in = AnontionSecurityECDSA.class.getClassLoader().getResourceAsStream("server.key");
    
    if (in != null) {

      BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      
      StringBuilder sb = new StringBuilder();
      
      String line;

      try {
      
        while ((line = reader.readLine()) != null) {
      
          sb.append(line);
        }
        
      } catch (IOException e) {
        
        _logger.exception(e);

        return null;
      }

      String base64Key = sb.toString().trim();

      if (base64Key.isEmpty()) {
        
        _logger.severe("Server keyfile is empty");
        
        return null;
      }

      return decodeECPrivateKeyParametersFromBase64D(base64Key);
      
    } else {
    
      _logger.severe("Server key not found");
    }
    
    return null;
  }
  
  public static String encodePubK1XY(ECPublicKeyParameters pub) {

    org.bouncycastle.math.ec.ECPoint publicKeyPoint = pub.getQ();

    byte[] xBytes = publicKeyPoint.getAffineXCoord().toBigInteger().toByteArray(); 

    byte[] yBytes = publicKeyPoint.getAffineYCoord().toBigInteger().toByteArray(); 

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

    System.arraycopy(xBytes, xOffset, x32Bytes, 32 - (xBytes.length - xOffset), (xBytes.length - xOffset));

    System.arraycopy(yBytes, yOffset, y32Bytes, 32 - (yBytes.length - yOffset), (yBytes.length - yOffset));

    byte[] publicKeyBytes = new byte[65];

    publicKeyBytes[0] = 0x04;

    System.arraycopy(x32Bytes, 0, publicKeyBytes, 1, 32);

    System.arraycopy(y32Bytes, 0, publicKeyBytes, 33, 32);

    String s = Base64.getEncoder().encodeToString(publicKeyBytes);
    
    return s;
  }
    
  public static String encodePubK1XYCompressed(ECPublicKeyParameters pub) {

    ECPoint publicKeyPoint = pub.getQ();

    byte[] compressed = publicKeyPoint.getEncoded(true); 

    return Base64.getEncoder().encodeToString(compressed);
  }
  
  public static ECPublicKeyParameters decodeECPublicKeyParametersFromBase64XY(String base64Key) { 
  
    byte[] decodedKey = Base64.getDecoder().decode(base64Key);

    byte[] xBytes = new byte[32];
    
    byte[] yBytes = new byte[32];
    
    System.arraycopy(decodedKey, 1, xBytes, 0, 32);
    
    System.arraycopy(decodedKey, 33, yBytes, 0, 32);
        
    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(AnontionConfig._ECDSA_CURVE);

    ECCurve curve = params.getCurve();

    org.bouncycastle.math.ec.ECPoint point = curve.decodePoint(decodedKey);

    ECDomainParameters domainParams = new ECDomainParameters(curve, params.getG(), params.getN(), params.getH());

    return new ECPublicKeyParameters(point, domainParams);
  }
      
  public static String sign(String text) { 

    ECPrivateKeyParameters root = AnontionSecurityECDSA.root();

    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
    
    SHA256Digest digest = new SHA256Digest();
    
    digest.update(bytes, 0, bytes.length);
    
    byte[] hash = new byte[digest.getDigestSize()];
    
    digest.doFinal(hash, 0);
    
    ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));

    signer.init(true, root);
    
    BigInteger[] components = signer.generateSignature(hash);

    BigInteger r = components[0];

    BigInteger s = components[1];

    byte[] rBytes = pad(r, 32);
  
    byte[] sBytes = pad(s, 32);

    byte[] buffer = new byte[64];
    
    System.arraycopy(rBytes, 0, buffer, 0, 32);
    
    System.arraycopy(sBytes, 0, buffer, 32, 32);

    return Base64.getEncoder().encodeToString(buffer);
  }
  
  public static boolean check(String text, String countersign, ECPublicKeyParameters key) {
    
    try {

      byte[] messageBytes = text.getBytes(StandardCharsets.UTF_8);
      
      SHA256Digest digest = new SHA256Digest();
      
      digest.update(messageBytes, 0, messageBytes.length);
      
      byte[] hash = new byte[digest.getDigestSize()];
      
      digest.doFinal(hash, 0);
    
      byte[] sigBytes = Base64.getDecoder().decode(countersign);

      BigInteger r = null;
      
      BigInteger s = null;
      
      if (sigBytes.length == 64 || sigBytes.length == 65) {

        byte[] rBytes = new byte[32];
        
        byte[] sBytes = new byte[32];
        
        System.arraycopy(sigBytes, 0, rBytes, 0, 32);
        
        System.arraycopy(sigBytes, 32, sBytes, 0, 32);
        
        r = new BigInteger(1, rBytes);
      
        s = new BigInteger(1, sBytes);
      
      } else {

        _logger.severe("signature is wrong size");

        return false;
      }
      
      ECDSASigner signer = new ECDSASigner();
      
      signer.init(false, key);
      
      return signer.verifySignature(hash, r, s);
      
    } catch (Exception e) {

      _logger.exception(e);

      return false;
    }
  }
  
  private static byte[] pad(BigInteger value, int length) {
    
    byte[] bytes = value.toByteArray();

    if (bytes.length == length) {
    
      return bytes;
    }

    if (bytes.length == length + 1 && bytes[0] == 0) {
      
      byte[] tmp = new byte[length];
      
      System.arraycopy(bytes, 1, tmp, 0, length);
      
      return tmp;
    }

    byte[] result = new byte[length];
    
    if (bytes.length > length) {
       
      System.arraycopy(bytes, bytes.length - length, result, 0, length);
    
    } else {
    
      System.arraycopy(bytes, 0, result, length - bytes.length, bytes.length);
    }

    return result;
  }
  
  public static String encodePubK1XY_(PublicKey pub) {

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

    System.arraycopy(xBytes, xOffset, x32Bytes, 32 - (xBytes.length - xOffset), (xBytes.length - xOffset));

    System.arraycopy(yBytes, yOffset, y32Bytes, 32 - (yBytes.length - yOffset), (yBytes.length - yOffset));

    byte[] publicKeyBytes = new byte[65];

    publicKeyBytes[0] = 0x04;

    System.arraycopy(x32Bytes, 0, publicKeyBytes, 1, 32);

    System.arraycopy(y32Bytes, 0, publicKeyBytes, 33, 32);

    return Base64.getEncoder().encodeToString(publicKeyBytes);
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

    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(AnontionConfig._ECDSA_CURVE);

    java.security.spec.ECPoint ecPoint = new java.security.spec.ECPoint(new BigInteger(1, xBytes),
        new BigInteger(1, yBytes));

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

      _logger.exception(e);
    }

    return null;
  }

  public static String hash(String name, Long ts, UUID client, String pub) {

    String[] tokens = { name, ts.toString(), client.toString(), pub};
    
    return AnontionSecuritySHA.hash(AnontionStrings.concat(tokens, ":"));
  }

  public static String encodeKeyD_(ECPrivateKeyParameters key) { // TODO - check works
    
    BigInteger d = key.getD();

    byte[] bytes = d.toByteArray();

    if (bytes.length == 33 && bytes[0] == 0x00) {

        byte[] trimmed = new byte[32];

        System.arraycopy(bytes, 1, trimmed, 0, 32);
        
        bytes = trimmed;
    
    } else if (bytes.length < 32) {

      byte[] padded = new byte[32];
      
      System.arraycopy(bytes, 0, padded, 32 - bytes.length, bytes.length);
      
      bytes = padded;
    
    } else if (bytes.length > 32) {
    
      throw new IllegalArgumentException("Private key too large");
    }

    return Base64.getEncoder().encodeToString(bytes);
  }

  public static ECPrivateKeyParameters decodeECPrivateKeyParametersFromBase64D(String base64Key) {

    byte[] decoded = Base64.getDecoder().decode(base64Key);
    
    if (decoded.length != 32) {
        
      throw new IllegalArgumentException("Invalid private key length, expected 32 bytes");
    }

    BigInteger d = new BigInteger(1, decoded);

    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(AnontionConfig._ECDSA_CURVE);
 
    ECDomainParameters domain = new ECDomainParameters(
        params.getCurve(),
        params.getG(),
        params.getN(),
        params.getH());
      
    return new ECPrivateKeyParameters(d, domain);
  }
   
  final private static AnontionLog _logger = new AnontionLog(AnontionSecurityECDSA.class.getName());
}



