package com.anontion.account.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.application.controller.ApplicationDeleteController;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
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
  private AscountService accountService;
 
  @DeleteMapping("/account/{id}")
  public ResponseEntity<ResponseDTO> deleteApplication(@PathVariable("id") String id) {

    String adjusted = id.replace('@', '/');
    
    String decoded = URLDecoder.decode(adjusted, StandardCharsets.UTF_8);
    
    String[] tokens = AnontionStrings.split(decoded, ":");
    
    if (tokens.length < 4) {
      
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
    
    AnontionApplication application = null;
    
    Optional<AnontionApplication> applicationO = 
        applicationRepository.findById(index);

    if (applicationO.isPresent()) {

      application = applicationO.get();
    }

    AnontionAccount account = null;
    
    Optional<AnontionAccount> accountO = 
        accountRepository.findByTsAndNameAndApplication(ts, name, uuid);

    if (accountO.isPresent()) {

      account = accountO.get();
    }

    if (account != null && application != null) {
      
      if (!accountService.deleteTxApplicationAndAccount(application, account)) {
        
        return Responses.getBAD_REQUEST(
            "Account+Application delete error");
      }
      
    } else if (account == null && application == null) {
      
    } else {

      if (account != null) {

        try {

          accountRepository.delete(account);

        } catch (Exception e) {

          _logger.exception(e);

          return Responses.getBAD_REQUEST(
              "Account delete error - " + e.getClass().getName());
        }
      }

      if (application != null) {

        try {

          applicationRepository.delete(application);

        } catch (Exception e) {

          _logger.exception(e);

          return Responses.getBAD_REQUEST(
              "Application delete error - " + e.getClass().getName());
        }
      }
    }
    
    return Responses.getOK();    
  }
  
  final private static AnontionLog _logger = new AnontionLog(ApplicationDeleteController.class.getName());
}


