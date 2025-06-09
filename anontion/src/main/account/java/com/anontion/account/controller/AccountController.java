package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

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

import com.anontion.application.model.AnontionApplicationId;
import com.anontion.application.model.AnontionApplication;
import com.anontion.common.dto.request.RequestAccountDTO;

import com.anontion.application.repository.AnontionApplicationRepository;
import com.anontion.account.model.AnontionAccount;
import com.anontion.account.repository.AnontionAccountRepository;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurityDSA;
import com.anontion.common.dto.response.ResponseAccountBodyDTO;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;

import jakarta.validation.Valid;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;

@RestController
@RequestMapping("/account")
public class AccountController {

  static final private int _ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT = 24 * 60 * 60; // 1 day

  private int applicationTimeout = _ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT;
  
  @Autowired
  private AnontionAccountRepository accountRepository;

  @Autowired
  private AnontionApplicationRepository applicationRepository;

  @Autowired
  private ServletContext servletContext;

  @GetMapping("/")
  public ResponseEntity<ResponseDTO> getAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());

    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @PostMapping(path = "/")
  public ResponseEntity<ResponseDTO> postAccount(@Valid @RequestBody RequestAccountDTO requestAccountDTO,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Invalid parameters!"),
          new ResponseBodyErrorDTO(message));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    UUID    client      = requestAccountDTO.getBody().getId();
    String  countersign = requestAccountDTO.getBody().getCountersign();
    String  high        = requestAccountDTO.getBody().getHigh();
    String  low         = requestAccountDTO.getBody().getLow();
    String  name        = requestAccountDTO.getBody().getName();
    Long    target      = requestAccountDTO.getBody().getTarget();
    String  text        = requestAccountDTO.getBody().getText();
    Long    ts          = requestAccountDTO.getBody().getTs();

    AnontionApplicationId applicationId = new AnontionApplicationId(name, ts, client);

    Optional<AnontionApplication> applicationOptional = applicationRepository.findById(applicationId);

    if (!applicationOptional.isPresent()) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "No application."),
          new ResponseBodyErrorDTO("Application could not be found." + "name = '" + name + "', ts = '" + ts + "', client UUID " + client + "'"));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    AnontionApplication application = applicationOptional.get();
    
    if (!application.getText().equals(text) || !application.getTarget().equals(target)) {
      
      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "No application."),
          new ResponseBodyErrorDTO("Application POW mismatch."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    String pub = application.getPub();
    
    String plaintext = application.getPlaintext();
    
    if (AnontionSecurityDSA.decodePublicKeyFromBase64XY(pub) == null) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad pub."),
          new ResponseBodyErrorDTO("Received client pub key invalid PublicKey."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    ECPublicKeyParameters publicKey = AnontionSecurityDSA.decodeECPublicKeyParametersFromBase64XY(pub);

    if (publicKey == null) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad pub."),
          new ResponseBodyErrorDTO("Received client pub key invalid ECPublicKeyParameters."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (!AnontionSecurityDSA.check(plaintext, countersign, publicKey)) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad pub."),
          new ResponseBodyErrorDTO("Client countersign of proof token invalid."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    } 
    
    
    boolean approved = false; 
    
    Long now = AnontionTime.tsN();
    
    if ((now - ts) > applicationTimeout) {

      approved = true;
    }
    
    Optional<AnontionAccount> accountOptional = accountRepository.findByTsAndNameAndApplication(ts, name, client);

    AnontionAccount account = null;
    
    ResponseAccountBodyDTO body = null;
    
    if (accountOptional.isPresent()) {
      
      account = accountOptional.get();
      
      body = new ResponseAccountBodyDTO(account.getId(), account.getTs(), account.getName(),
             account.getApplication(), 100.0f, true);
      
    } else {

      if (approved) {
        
        AnontionAccount newAccount = new AnontionAccount(ts, name, client, pub, plaintext, countersign, true);

        _logger.info("New Account: " + newAccount);

        account = accountRepository.save(newAccount);
        
        body = new ResponseAccountBodyDTO(account.getId(), account.getTs(), account.getName(),
            account.getApplication(), 100.0f, approved);
      
      } else {
      
        Float progress = ((now - ts) / (float) applicationTimeout) * 100.0f;
        
        body = new ResponseAccountBodyDTO(new UUID(0L, 0L), ts, name, client, progress, approved);
      }
    }

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping(path = "/")
  public ResponseEntity<ResponseDTO> putAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());

    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  @DeleteMapping(path = "/")
  public ResponseEntity<ResponseDTO> deleteAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());

    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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

  final private static AnontionLog _logger = new AnontionLog(AccountController.class.getName());
}


