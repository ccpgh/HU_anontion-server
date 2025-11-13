package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.asterisk.repository.AsteriskAuthRepository;
import com.anontion.asterisk.repository.AsteriskEndpointRepository;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.account.repository.AnontionAccountRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.anontion.common.dto.response.ResponseGetAccountBodyDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.ResponsePostDTO;

@RestController
public class AccountGetController {

  @Autowired
  private AsteriskEndpointRepository endpointRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;

  @Autowired
  private AnontionAccountRepository accountRepository;

  private Cache<String, Boolean> nonceCache = Caffeine.newBuilder()
      .expireAfterWrite(AnontionConfig._REQUEST_TS_VALIDITY_MARGIN, TimeUnit.SECONDS)
      .build();
  
  @GetMapping(path = "/account/")
  public ResponseEntity<ResponseDTO> getAccount(@RequestParam("sipUserId") String sipUserId,
      @RequestParam("sipPassword") String sipPassword, @RequestParam("nowTs") Long nowTs, @RequestParam("nonce") String nonce) {

    if (!AnontionStrings.isValidName(sipUserId) ||
        !AnontionStrings.isValidPassword(sipPassword)) {
      
      return Responses.getBAD_REQUEST(
          "Parameters failed validity check.", 
          "Transaction rejected.");
    }
    
    Long diff = nowTs - AnontionTime.tsN();

    if (diff > AnontionConfig._REQUEST_TS_VALIDITY_MAX ||
        diff < AnontionConfig._REQUEST_TS_VALIDITY_MIN) {

      return Responses.getBAD_REQUEST(
          "Invalid parameters time", 
          "bad nowTs"); 
    }
    
    if (nonce.isBlank() || nonceCache.getIfPresent(nonce) != null) {

      return Responses.getBAD_REQUEST("Bad message");
    }
    
    nonceCache.put(nonce, true);
      
    String md5Input = sipUserId + ":asterisk:" + sipPassword;
    
    String md5Cred = DigestUtils.md5DigestAsHex(md5Input.getBytes());
    
    Optional<AsteriskAuth> auth0 = 
        authRepository.findByUsernameAndMd5Cred(sipUserId, md5Cred);
    
    if (auth0.isEmpty()) {
      return Responses.getBAD_REQUEST(
          "No account(auth)", 
          "");
    }

    AsteriskAuth auth = auth0.get();

    Optional<AsteriskEndpoint> endpoint0 = 
        endpointRepository.findByAuth(auth.getId());
    
    if (endpoint0.isEmpty()) {
      return Responses.getBAD_REQUEST(
          "No account(endpoint).", 
          "");      
    }

    AsteriskEndpoint endpoint = endpoint0.get();

    String clientPub = endpoint.getId();
    
    String clientPubSafe = AnontionSecurity.encodeToSafeBase64(clientPub);

    String clientPubNonSafe = AnontionSecurity.decodeFromSafeBase64(clientPubSafe);

    if (clientPubNonSafe.isBlank()) { 
      return Responses.getBAD_REQUEST(
          "No account.", 
          "");      
    } 

    Optional<AnontionAccount> accountO = accountRepository.findByClientPubAuthorized(clientPubNonSafe);

    if (accountO.isEmpty()) { 
      return Responses.getBAD_REQUEST(
          "No account.", 
          "");      
    } 
    
    ResponseGetAccountBodyDTO body = new ResponseGetAccountBodyDTO(AnontionSecurityECDSA.pubS(),
        endpoint.getId());
        
    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  final private static AnontionLog _logger = new AnontionLog(AccountGetController.class.getName());
}


