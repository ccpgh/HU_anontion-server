package com.anontion.account.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.models.application.model.AnontionApplicationId;
import com.anontion.models.application.model.AnontionApplication;
import com.anontion.common.dto.request.RequestPostAccountBodyDTO;
import com.anontion.common.dto.request.RequestPostAccountDTO;

import com.anontion.models.application.repository.AnontionApplicationRepository;
import com.anontion.services.service.AscountService;
import com.anontion.asterisk.bean.AsteriskAorBean;
import com.anontion.asterisk.bean.AsteriskAuthBean;
import com.anontion.asterisk.bean.AsteriskEndpointBean;
import com.anontion.asterisk.model.AsteriskAor;
import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.asterisk.repository.AsteriskAuthRepository;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.account.repository.AnontionAccountRepository;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponsePostDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionSecuritySCRYPT;
import com.anontion.common.dto.response.ResponsePostAccountBodyDTO;

import jakarta.validation.Valid;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;

@RestController
public class AccountPostController {

  static final private int _ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT = 24 * 60 * 60; // 1 day
  
  private int applicationTimeout = _ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT;
  
  @Autowired
  private AnontionAccountRepository accountRepository;

  @Autowired
  private AnontionApplicationRepository applicationRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;
  
  @Autowired
  private ServletContext servletContext;

  @Autowired
  private AsteriskEndpointBean endpointBean;

  @Autowired
  private AsteriskAorBean aorBean;

  @Autowired
  private AsteriskAuthBean authBean;
  
  @Autowired
  private AscountService accountService;
  
  @PostMapping(path = "/account/")
  public ResponseEntity<ResponseDTO> postAccount(@Valid @RequestBody RequestPostAccountDTO request,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      return Responses.getBAD_REQUEST(
          "Invalid parameters!", 
          message);      
    }

    RequestPostAccountBodyDTO dto = request.getBody();
    
    String  low         = dto.getLow();
    String  high        = dto.getHigh();

    AnontionApplicationId id = new AnontionApplicationId(
        dto.getName(), 
        dto.getTs(), 
        dto.getId());

    Optional<AnontionApplication> applicationO = applicationRepository.findById(id);

    if (!applicationO.isPresent()) {

      return Responses.getBAD_REQUEST(
          "No application.", 
          "Application missing " + id.description());
    }

    AnontionApplication application = applicationO.get();
    
    if (!application.getText().equals(dto.getText()) || 
        !application.getTarget().equals(dto.getTarget()) || 
        application.getDisabled()) {
      
      return Responses.getBAD_REQUEST(
          "No active application.", 
          "Application POW mismatch.");
    }

    if (AnontionSecurityECDSA.decodePublicKeyFromBase64XY(application.getPub()) == null) {

      return Responses.getBAD_REQUEST(
          "Bad pub.", 
          "Received client pub key invalid PublicKey.");
    }

    ECPublicKeyParameters publicKey = 
        AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(application.getPub());

    if (publicKey == null) {

      return Responses.getBAD_REQUEST(
          "Bad pub.", 
          "Received client pub key invalid ECPublicKeyParameters.");
    }

    if (!AnontionSecurityECDSA.check(application.getPlaintext(), dto.getCountersign(), publicKey)) {

      return Responses.getBAD_REQUEST(
          "Bad pub.", 
          "Client countersign of proof token invalid.");
    } 
    
    Long now = AnontionTime.tsN();

    Optional<AnontionAccount> accountO = accountRepository.findByTsAndNameAndApplication(
        dto.getTs(), 
        dto.getName(), 
        dto.getId());

    ResponsePostAccountBodyDTO body = null;
    
    if (accountO.isPresent()) {
      
      AnontionAccount account = accountO.get();

      Optional<AsteriskAuth> authO = authRepository.findById(account.getPub());

      if (authO.isPresent()) {
        
        AsteriskAuth auth = authO.get();

        body = new ResponsePostAccountBodyDTO(
            account.getId(), 
            account.getTs(), 
            account.getName(),
            account.getApplication(), 
            auth.getUsername(), 
            auth.getPassword(),
            100.0f, 
            true);

      } else {

        return Responses.getBAD_REQUEST(
            "Asterisk account auth missing.", 
            "Preexiisting account auth should but does not exist.");
      }    
      
    } else {

      if (approved(dto.getTs(), now)) {

        AnontionAccount account = new AnontionAccount(
            dto.getTs(), 
            dto.getName(), 
            dto.getId(), 
            dto.getCountersign(), 
            application.getPub(), 
            application.getPlaintext(), 
            false);
               
        AsteriskEndpoint endpoints = endpointBean.createAsteriskEndppint(
            account.getPub(), 
            account.getName());
                                    
        AsteriskAuth auths = authBean.createAsteriskAuth(
            account.getPub(), 
            AnontionSecurity.tobase76FromBase64(AnontionSecuritySCRYPT.hash(account.getPub())),
            AnontionSecurity.generatePassword());

        if (auths.getUsername().isEmpty()) {
          
          return Responses.getBAD_REQUEST(
              "Id key invalid.");
        }
        
        AsteriskAor aors = aorBean.createAsteriskAor(
            application.getPub());
        
        try {

          if (!accountService.saveTxAccountAndEndpoint(
              application,
              account, 
              endpoints, 
              auths, 
              aors)) {
            
            return Responses.getBAD_REQUEST(
                "Failed application, account and/or asterisk update.");
          }
        
        } catch (Exception e) {
          
          _logger.exception(e);
          
          return Responses.getBAD_REQUEST(
              "Failed application, account and/or asterisk update.");
        }
        
        body = new ResponsePostAccountBodyDTO(
            account.getId(), 
            account.getTs(), 
            account.getName(),
            account.getApplication(), 
            auths.getUsername(), 
            auths.getPassword(), 
            100.0f, 
            true);
      
      } else {
      
        Float progress = ((now - dto.getTs()) / (float) applicationTimeout) * 100.0f;
        
        body = new ResponsePostAccountBodyDTO(
            new UUID(0L, 0L), 
            dto.getTs(), 
            dto.getName(), 
            dto.getId(), 
            "", 
            "", 
            progress, 
            false);
      }
    }

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostConstruct
  public void init() {
    
    String context = servletContext.getInitParameter("application-timeout");

    if (context != null) {
      
      try {
      
        applicationTimeout = Integer.parseInt(context);
      
      } catch (NumberFormatException e) {
    
        applicationTimeout = _ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT;
      }
    
    } else {
    
      applicationTimeout = _ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT; 
    }

    _logger.info("Application timeout is " + applicationTimeout);
  }

  public boolean approved(Long ts, Long now) { // TODO expand
    
    return ((now - ts) > applicationTimeout);
  }
  
  final private static AnontionLog _logger = new AnontionLog(AccountPostController.class.getName());
}


