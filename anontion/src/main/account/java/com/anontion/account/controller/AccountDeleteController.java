package com.anontion.account.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.application.controller.ApplicationDeleteController;
import com.anontion.asterisk.model.AsteriskAor;
import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.asterisk.repository.AsteriskAorRepository;
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
import com.anontion.models.application.model.AnontionApplication;
import com.anontion.models.application.model.AnontionApplicationId;
import com.anontion.models.application.repository.AnontionApplicationRepository;
import com.anontion.services.service.AccountService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@RestController
public class AccountDeleteController {
  
  @Autowired
  private AnontionAccountRepository accountRepository;

  @Autowired
  private AnontionApplicationRepository applicationRepository;

  @Autowired
  private AsteriskEndpointRepository endpointRepository;

  @Autowired
  private AsteriskAorRepository aorRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;
  
  @Autowired
  private AccountService accountService;
 
  private Cache<String, Boolean> nonceCache = Caffeine.newBuilder()
      .expireAfterWrite(AnontionConfig._REQUEST_TS_VALIDITY_MARGIN, TimeUnit.SECONDS)
      .build();
  
  @DeleteMapping("/account/{id}")
  public ResponseEntity<ResponseDTO> deleteAccount(@PathVariable("id") String id, @RequestParam("nowTs") Long nowTs, 
      @RequestParam("nonce") String nonce, @RequestParam("signature") String signature) {

    if (nowTs <= 0 ||
        nonce.isBlank() ||
        signature.isBlank()) {
      
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

      return Responses.getBAD_REQUEST("Bad nonce");
    }
    
    nonceCache.put(nonce, true);
    
    String[] tokens = AnontionStrings.split(id, ":");
    
    if (tokens.length < 3) {
      
      return Responses.getBAD_REQUEST(
          "Invalid parameters - expected 4+ parameter elements");
    }
    
    String name = tokens[0];

    if (name.isBlank()) {
      
      _logger.severe("Invalid parameter - name was empty.");

      return Responses.getBAD_REQUEST(
          "Invalid parameter - name was empty.");
    }

    Long ts = null;
    
    try {
      
      ts = Long.parseLong(tokens[1]);
      
    } catch (NumberFormatException e) {
      
      _logger.exception(e);
      
      return Responses.getBAD_REQUEST(
          "Invalid parameter - ts was not a valid Long.");
    }

    UUID uuid = null;
    
    try {
      
      uuid = UUID.fromString(tokens[2]);
    
    } catch (IllegalArgumentException e) {
      
      _logger.exception(e);
      
      return Responses.getBAD_REQUEST(
          "Invalid parameter - UUID was not in correct format.");
    }

    AnontionApplicationId index = new AnontionApplicationId(
        name, 
        ts, 
        uuid);
    
    Optional<AnontionApplication> applicationO = 
        applicationRepository.findById(index);

    Optional<AnontionAccount> accountO = 
        accountRepository.findByClientTsAndClientNameAndClientId(ts, name, uuid);
    
    Optional<AsteriskEndpoint> endpoint0 = null;  
    
    String pub = AccountDeleteController.getPub(applicationO, accountO);

    AsteriskAor aors = null;

    AsteriskAuth auths = null;

    if (pub != null && !pub.isBlank()) {

      endpoint0 = endpointRepository.findById(AnontionSecurity.encodeToSafeBase64(pub));
      
      Optional<AsteriskAor> aors0 = 
          aorRepository.findById(AnontionSecurity.encodeToSafeBase64(pub));
      
      if (aors0.isPresent()) {
        
        aors = aors0.get();
      }

      if (endpoint0.isPresent()) {

        AsteriskEndpoint endpoint = endpoint0.get();
        
        Optional<AsteriskAuth> auth0 = 
            authRepository.findById(endpoint.getAuth());

        if (auth0.isPresent()) {

          auths = auth0.get();
        }
      }
    }
    
    if (!accountO.isEmpty()) {

      AnontionAccount account = accountO.get();

      String text = nowTs.toString() + nonce;

      ECPublicKeyParameters publicKey = 
          AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(account.getClientPub());

      if (publicKey == null) {

        return Responses.getBAD_REQUEST(
            "Bad pub.", 
            "Pub invalid.");
      }

      if (!AnontionSecurityECDSA.checkSignature(text, signature, publicKey)) {

        return Responses.getBAD_REQUEST(
            "Bad signature.", 
            "Signature invalid.");
      } 

      if (!accountService.deleteTxApplicationAndAccountAndEndpoint(
          applicationO.orElse(null), 
          accountO.orElse(null),
          (endpoint0 == null ? null : endpoint0.orElse(null)),
          auths,
          aors)) {

        return Responses.getBAD_REQUEST(
            "Failed application, account and/or asterisk update.");
      }
      
      _logger.info("account deletd " + account.getAccountId());
    }
    
    return Responses.getOK();    
  }
  
  private static String getPub(Optional<AnontionApplication> applicationO, Optional<AnontionAccount> account) {
    
    if (applicationO.isPresent()) {
      
      return applicationO.get().getClientPub();
    }

    if (account.isPresent()) {
      
      return account.get().getClientPub();
    }

    return null;
  }
  
  final private static AnontionLog _logger = new AnontionLog(ApplicationDeleteController.class.getName());
}


