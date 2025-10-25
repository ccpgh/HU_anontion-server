package com.anontion.common.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

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
import org.bouncycastle.math.ec.ECPoint; 
import org.bouncycastle.util.BigIntegers;

import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;

abstract public class AnontionSecurityECIES_ECDH {

  private static SecureRandom _secureRandom = new SecureRandom();
  
  public static String encrypt(ECPublicKeyParameters pub, String s) {
    
    try {

      X9ECParameters e = CustomNamedCurves.getByName(AnontionConfig._ECDSA_CURVE);
      
      if (e == null) {
        
        throw new IllegalArgumentException("Invalid curve");
      }
      
      ECDomainParameters params = new ECDomainParameters(e.getCurve(), e.getG(), e.getN(), e.getH());
      
      BigInteger n = params.getN();
      
      BigInteger d = new BigInteger(n.bitLength() + 8, _secureRandom).mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
      
      ECPoint q = params.getG().multiply(d).normalize();

      if (!q.isValid()) {
        
        throw new IllegalArgumentException("Invalid public key point");
      }
      
      ECPrivateKeyParameters ekey = new ECPrivateKeyParameters(d, params);
      
      ECPublicKeyParameters epub = new ECPublicKeyParameters(q, params);
      
      ECDHBasicAgreement agreement = new ECDHBasicAgreement();

      agreement.init(ekey);
      
      BigInteger sharedSecret = agreement.calculateAgreement(pub);

      KDF2BytesGenerator kdf = new KDF2BytesGenerator(new SHA256Digest());
      
      byte[] secret = BigIntegers.asUnsignedByteArray((params.getCurve().getFieldSize() + 7) / 8, sharedSecret);

      byte[] bytes = new byte[32];
      
      kdf.init(new KDFParameters(secret, null));
      
      kdf.generateBytes(bytes, 0, bytes.length);

      byte[] iv = new byte[12];

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
  
  public static String decrypt(ECPrivateKeyParameters key, String s) {
    
    try {

      X9ECParameters e = CustomNamedCurves.getByName(AnontionConfig._ECDSA_CURVE);
      
      if (e == null) {
        
        throw new IllegalArgumentException("Invalid curve");
      }
      
      ECDomainParameters params = new ECDomainParameters(e.getCurve(), e.getG(), e.getN(), e.getH());
         
      byte[] data = Base64.getDecoder().decode(s);
      
      if (data.length < 78) {

        throw new RuntimeException("Decrypt data was too short");
      }

      byte[] ephPubBytes = Arrays.copyOfRange(data, 0, 65);
      
      byte[] iv = Arrays.copyOfRange(data, 65, 77);
      
      byte[] ciphertext = Arrays.copyOfRange(data, 77, data.length);

      ECPoint ephQ = params.getCurve().decodePoint(ephPubBytes).normalize();
      
      if (!ephQ.isValid()) {
      
        throw new IllegalArgumentException("Invalid public key point");
      }
      
      ECPublicKeyParameters epub = new ECPublicKeyParameters(ephQ, params);

      ECDHBasicAgreement agreement = new ECDHBasicAgreement();
      
      agreement.init(key);
      
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

      String decoded = new String(output, 0, len, "UTF-8");
      
      return decoded;
      
    } catch (Exception e) {
      
      _logger.exception(e);
      
      throw new RuntimeException("Decrypt failed", e);
    }
  }
 
  final private static AnontionLog _logger = new AnontionLog(AnontionSecurityECIES_ECDH.class.getName());
}



