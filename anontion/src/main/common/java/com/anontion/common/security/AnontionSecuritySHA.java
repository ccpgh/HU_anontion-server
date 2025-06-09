package com.anontion.common.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.bouncycastle.crypto.digests.SHA256Digest;

import com.anontion.common.misc.AnontionLog;

abstract public class AnontionSecuritySHA {

  public static String hash(String text) { 

    byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

    SHA256Digest digest = new SHA256Digest();

    digest.update(bytes, 0, bytes.length);

    byte[] hash = new byte[digest.getDigestSize()];

    digest.doFinal(hash, 0);

    return Base64.getEncoder().encodeToString(hash);
  }
  
  
  @SuppressWarnings("unused") 
  final private static AnontionLog _logger = new AnontionLog(AnontionSecuritySHA.class.getName());
}



