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
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint; 
import org.bouncycastle.util.BigIntegers;

abstract public class AnontionSecurity {

  private static String _curve = "secp256k1";
  
  private static SecureRandom _secureRandom = new SecureRandom();

  private static ECPrivateKeyParameters _root = AnontionSecurity.load();

  private static ECPublicKeyParameters _pub = toPublicKey(_root);

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
    
    InputStream in = AnontionSecurity.class.getClassLoader().getResourceAsStream("server.key");
    
    if (in != null) {

      BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      
      StringBuilder sb = new StringBuilder();
      
      String line;

      try {
      
        while ((line = reader.readLine()) != null) {
      
          sb.append(line);
        }
        
      } catch (IOException e) {
        
        System.out.println("DEBUG Server key load eror " + e);

        e.printStackTrace();

        return null;
      }

      String base64Key = sb.toString().trim();

      if (base64Key.isEmpty()) {
        
        System.out.println("DEBUG Server keyfile is empty");
        
        return null;
      }

      return decodeECPrivateKeyParametersFromBase64D(base64Key);
      
    } else {
    
      System.out.println("DEBUG Server key not found");
    }
    
    return null;
  }
  
  public static String encodePubK1XYUncompressed(ECPublicKeyParameters pub) {

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
    
    System.out.println("DEBUG encodePubK1XYUncompressed result s '" + s + "'");
    
    return s;
  }
    
  public static String encodePubK1XYCompressed(ECPublicKeyParameters pub) {

    ECPoint publicKeyPoint = pub.getQ();

    byte[] compressed = publicKeyPoint.getEncoded(true); 

    return Base64.getEncoder().encodeToString(compressed);
  }
  
  public static ECPublicKeyParameters decodeECPublicKeyParametersFromBase64XYUncompressed(String base64Key) { 
  
    byte[] decodedKey = Base64.getDecoder().decode(base64Key);

    byte[] xBytes = new byte[32];
    
    byte[] yBytes = new byte[32];
    
    System.arraycopy(decodedKey, 1, xBytes, 0, 32);
    
    System.arraycopy(decodedKey, 33, yBytes, 0, 32);
        
    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(_curve);

    ECCurve curve = params.getCurve();

    org.bouncycastle.math.ec.ECPoint point = curve.decodePoint(decodedKey);

    ECDomainParameters domainParams = new ECDomainParameters(curve, params.getG(), params.getN(), params.getH());

    return new ECPublicKeyParameters(point, domainParams);
  }
  
  //////////////////////////////////////////////////////////////////// SANITY LINE 
  //////////////////////////////////////////////////////////////////// SANITY LINE  
  //////////////////////////////////////////////////////////////////// SANITY LINE 
  //////////////////////////////////////////////////////////////////// SANITY LINE   
  
  public static String sign(String plaintext) { // TODO - check works

    ECPrivateKeyParameters root = AnontionSecurity.root();

    byte[] bytes = plaintext.getBytes(StandardCharsets.UTF_8);
    
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
  
  public static boolean check(String text, String counter, ECPublicKeyParameters key) { // TODO - change to use raw signature. 
    
    try {

      System.out.println("DEBUG text '" + text + "'");

      System.out.println("DEBUG counter '" + counter + "'");

      byte[] messageBytes = text.getBytes(StandardCharsets.UTF_8);
      
      SHA256Digest digest = new SHA256Digest();
      
      digest.update(messageBytes, 0, messageBytes.length);
      
      byte[] hash = new byte[digest.getDigestSize()];
      
      digest.doFinal(hash, 0);
    
      byte[] sigBytes = Base64.getDecoder().decode(counter);

      BigInteger r = null;
      
      BigInteger s = null;
      
      if (sigBytes.length == 64) {

        System.out.println("DEBUG raw path'");

        byte[] rBytes = new byte[32];
        
        byte[] sBytes = new byte[32];
        
        System.arraycopy(sigBytes, 0, rBytes, 0, 32);
        
        System.arraycopy(sigBytes, 32, sBytes, 0, 32);
        
        r = new BigInteger(1, rBytes);
      
        s = new BigInteger(1, sBytes);
      
      } else {

        System.out.println("DEBUG DER path");

        ASN1Sequence seq = (ASN1Sequence) ASN1Primitive.fromByteArray(sigBytes);
        
        r = ((ASN1Integer) seq.getObjectAt(0)).getValue();
        
        s = ((ASN1Integer) seq.getObjectAt(1)).getValue();
      }
      
      ECDSASigner signer = new ECDSASigner();
      
      signer.init(false, key);
      
      return signer.verifySignature(hash, r, s);
      
    } catch (Exception e) {

      System.out.println("DEBUG exception " + e);

      e.printStackTrace();
      
      return false;
    }
  }
  
  private static byte[] pad(BigInteger value, int length) { // TODO - check works
    
    byte[] bytes = value.toByteArray();

    if (bytes.length == length) {
    
      return bytes;
    }

    // Drop leading zero if present (sign byte)
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
  
  // WORKLINE
  
  public static String encodePubK1XY(PublicKey pub) { // TODO - check works

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
  

  public static PublicKey decodePublicKeyFromBase64XY(String base64Key) { // TODO - check works

    byte[] decodedKey = Base64.getDecoder().decode(base64Key);

    if (decodedKey.length != 65 || decodedKey[0] != 0x04) { // TODO 65 case handled?

      return null;
    }

    byte[] xBytes = new byte[32];

    byte[] yBytes = new byte[32];

    System.arraycopy(decodedKey, 1, xBytes, 0, 32);

    System.arraycopy(decodedKey, 33, yBytes, 0, 32);

    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(_curve);

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

      e.printStackTrace();
    }

    return null;
  }

  public static String hash(String name, Long ts, UUID client, String pub) { // TODO - check works

    String[] tokens = { name, ts.toString(), client.toString(), pub};
    
    return AnontionSecurity.hash(concat(tokens, ":"));
  }
  
  public static String concat(String[] tokens, String delimiter) {

    StringBuffer buffer = new StringBuffer();
    
    boolean first = true;
    
    for (String token: tokens) {
      
      if (!first) {
        
        buffer.append(delimiter);
      
      } else {
      
        first = false;
      }
      
      buffer.append(token.toLowerCase());      
    } 
    
    return buffer.toString();
  }

  public static String hash(String text) { // TODO - check works

    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

    SHA256Digest digest = new SHA256Digest();

    digest.update(bytes, 0, bytes.length);

    byte[] hash = new byte[digest.getDigestSize()];

    digest.doFinal(hash, 0);

    return Base64.getEncoder().encodeToString(hash);
  }
   
  public static String encodeKeyD(ECPrivateKeyParameters key) { // TODO - check works
    
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

  public static ECPrivateKeyParameters decodeECPrivateKeyParametersFromBase64D(String base64Key) { // TODO - check works

    byte[] decoded = Base64.getDecoder().decode(base64Key);
    
    if (decoded.length != 32) {
        
      throw new IllegalArgumentException("Invalid private key length, expected 32 bytes");
    }

    BigInteger d = new BigInteger(1, decoded);

    ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec(_curve);
 
    ECDomainParameters domain = new ECDomainParameters(
        params.getCurve(),
        params.getG(),
        params.getN(),
        params.getH());
      
    return new ECPrivateKeyParameters(d, domain);
  }
  
  public static ECPublicKeyParameters toPublicKey(ECPrivateKeyParameters privateKey) { // TODO - check works
    
    ECDomainParameters params = privateKey.getParameters();
  
    BigInteger d = privateKey.getD();

    ECPoint q = params.getG().multiply(d).normalize();

    return new ECPublicKeyParameters(q, params);
  }
  
  public static String encrypt(String s) { // TODO tidy. extract repetition.
    
    try {

      X9ECParameters e = CustomNamedCurves.getByName(_curve);
      
      if (e == null) {
        
        throw new IllegalArgumentException("Invalid curve");
      }
      
      ECDomainParameters params = new ECDomainParameters(e.getCurve(), e.getG(), e.getN(), e.getH());
      
      BigInteger n = params.getN();
      
      BigInteger d = new BigInteger(n.bitLength() + 8, _secureRandom).mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
      
      ECPoint q = params.getG().multiply(d).normalize();

      ECPrivateKeyParameters ekey = new ECPrivateKeyParameters(d, params);
      
      ECPublicKeyParameters epub = new ECPublicKeyParameters(q, params);
      
      ECDHBasicAgreement agreement = new ECDHBasicAgreement();

      agreement.init(ekey);
      
      BigInteger sharedSecret = agreement.calculateAgreement(pub());

      KDF2BytesGenerator kdf = new KDF2BytesGenerator(new SHA256Digest());
      
      byte[] secret = BigIntegers.asUnsignedByteArray((params.getCurve().getFieldSize() + 7) / 8, sharedSecret);

      byte[] bytes = new byte[32];
      
      kdf.init(new KDFParameters(secret, null));
      
      kdf.generateBytes(bytes, 0, bytes.length);

      byte[] iv = new byte[16];

      _secureRandom.nextBytes(iv);
      
      KeyParameter aesKey = new KeyParameter(bytes);

      AEADParameters aeadParams = new AEADParameters(aesKey, 128, iv, null);
      
      GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());

      cipher.init(true, aeadParams);
      
      byte[] input = s.getBytes("UTF-8");
      
      byte[] output = new byte[cipher.getOutputSize(input.length)];
      
      int len = cipher.processBytes(input, 0, input.length, output, 0);
      
      len += cipher.doFinal(output, len);

      byte[] text = new byte[len];
      
      System.arraycopy(output, 0, text, 0, len);

      byte[] epubBytes = epub.getQ().getEncoded(false);

      byte[] result = new byte[epubBytes.length + iv.length + text.length];

      System.arraycopy(epubBytes, 0, result, 0, epubBytes.length);
      
      System.arraycopy(iv, 0, result, epubBytes.length, iv.length);
      
      System.arraycopy(text, 0, result, epubBytes.length + iv.length, text.length);

      return Base64.getEncoder().encodeToString(result);

    } catch (Exception e) {
      
      throw new RuntimeException("Encryption failed", e);
    }
  }
  
  public static String decrypt(String s) { // TODO tidy. extract repetition.
    
    try {

      X9ECParameters e = CustomNamedCurves.getByName(_curve);
      
      if (e == null) {
        
        throw new IllegalArgumentException("Invalid curve");
      }
      
      ECDomainParameters params = new ECDomainParameters(e.getCurve(), e.getG(), e.getN(), e.getH());
         
      byte[] data = Base64.getDecoder().decode(s);

      byte[] ephPubBytes = Arrays.copyOfRange(data, 0, 65);
      
      byte[] iv = Arrays.copyOfRange(data, 65, 81);
      
      byte[] ciphertext = Arrays.copyOfRange(data, 81, data.length);

      ECPoint ephQ = params.getCurve().decodePoint(ephPubBytes).normalize();
      
      ECPublicKeyParameters epub = new ECPublicKeyParameters(ephQ, params);

      ECDHBasicAgreement agreement = new ECDHBasicAgreement();
      
      agreement.init(root());
      
      BigInteger shared = agreement.calculateAgreement(epub);
      
      byte[] secret = BigIntegers.asUnsignedByteArray((params.getCurve().getFieldSize() + 7) / 8, shared);

      KDF2BytesGenerator kdf = new KDF2BytesGenerator(new SHA256Digest());
      
      byte[] keyBytes = new byte[32];
      
      kdf.init(new KDFParameters(secret, null));
      
      kdf.generateBytes(keyBytes, 0, keyBytes.length);

      KeyParameter aesKey = new KeyParameter(keyBytes);
      
      AEADParameters aeadParams = new AEADParameters(aesKey, 128, iv, null);

      GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());
      
      cipher.init(false, aeadParams);

      byte[] output = new byte[cipher.getOutputSize(ciphertext.length)];
      
      int len = cipher.processBytes(ciphertext, 0, ciphertext.length, output, 0);
      
      len += cipher.doFinal(output, len);

      return new String(output, 0, len, "UTF-8");

    } catch (Exception e) {
      
      throw new RuntimeException("Decryption failed", e);
    }
  }
 

//private static byte[] encodeDER(BigInteger r, BigInteger s) {
// 
//  try (java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream()) {
//  
//    org.bouncycastle.asn1.DERSequenceGenerator seq = new org.bouncycastle.asn1.DERSequenceGenerator(bos);
//    
//    seq.addObject(new org.bouncycastle.asn1.ASN1Integer(r));
//    
//    seq.addObject(new org.bouncycastle.asn1.ASN1Integer(s));
//    
//    seq.close();
//    
//    return bos.toByteArray();
//  
//  } catch (Exception e) {
// 
//    throw new RuntimeException(e);
//  }
//}
  
//@SuppressWarnings("unused")
//private static ECKeyPairGenerator getGenerator() {
//
//  ECNamedCurveParameterSpec curve = ECNamedCurveTable.getParameterSpec(_curve);
//
//  ECDomainParameters domainParams = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH(),
//      curve.getSeed());
//
//  ECKeyGenerationParameters keyParams = new ECKeyGenerationParameters(domainParams, _secureRandom);
//
//  ECKeyPairGenerator generator = new ECKeyPairGenerator();
//
//  generator.init(keyParams);
//
//  return generator;
//}

}


