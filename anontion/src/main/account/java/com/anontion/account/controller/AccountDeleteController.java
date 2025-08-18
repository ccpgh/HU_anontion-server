package com.anontion.account.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

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
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.account.repository.AnontionAccountRepository;
import com.anontion.models.application.model.AnontionApplication;
import com.anontion.models.application.model.AnontionApplicationId;
import com.anontion.models.application.repository.AnontionApplicationRepository;
import com.anontion.services.service.AscountService;

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
  private AscountService accountService;
 
  @DeleteMapping("/account/{id}")
  public ResponseEntity<ResponseDTO> deleteApplication(@PathVariable("id") String id) {

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
    
    if (!accountService.deleteTxApplicationAndAccountAndEndpoint(
        applicationO.orElse(null), 
        accountO.orElse(null),
        (endpoint0 == null ? null : endpoint0.orElse(null)),
        auths,
        aors)) {
      
      return Responses.getBAD_REQUEST(
          "Failed application, account and/or asterisk update.");
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


