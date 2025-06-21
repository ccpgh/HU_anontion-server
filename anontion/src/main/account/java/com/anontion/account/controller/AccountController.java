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

import com.anontion.models.application.model.AnontionApplicationId;
import com.anontion.models.application.model.AnontionApplication;
import com.anontion.common.dto.request.RequestAccountDTO;

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
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionSecuritySHA;
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
  AscountService accountService;
  
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
    
    if (!application.getText().equals(text) || !application.getTarget().equals(target) || application.getDisabled()) {
      
      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "No active application."),
          new ResponseBodyErrorDTO("Application POW mismatch."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    String pub = application.getPub();
    
    String plaintext = application.getPlaintext();
    
    if (AnontionSecurityECDSA.decodePublicKeyFromBase64XY(pub) == null) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad pub."),
          new ResponseBodyErrorDTO("Received client pub key invalid PublicKey."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    ECPublicKeyParameters publicKey = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(pub);

    if (publicKey == null) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad pub."),
          new ResponseBodyErrorDTO("Received client pub key invalid ECPublicKeyParameters."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (!AnontionSecurityECDSA.check(plaintext, countersign, publicKey)) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad pub."),
          new ResponseBodyErrorDTO("Client countersign of proof token invalid."));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    } 
    

    Long now = AnontionTime.tsN();

    boolean approved = valid(ts, now); 
       
    Optional<AnontionAccount> accountOptional = accountRepository.findByTsAndNameAndApplication(ts, name, client);

    ResponseAccountBodyDTO body = null;
    
    if (accountOptional.isPresent()) {
      
      _logger.info("DEBUG isPresent");
      
      AnontionAccount account = accountOptional.get();

      Optional<AsteriskAuth> authOptional = authRepository.findById(account.getPub());

      if (authOptional.isPresent()) {
        
        _logger.info("DEBUG isPresent");
        
        AsteriskAuth auth = authOptional.get();

        body = new ResponseAccountBodyDTO(account.getId(), account.getTs(), account.getName(),
            account.getApplication(), 100.0f, true, auth.getUsername(), auth.getPassword());

      } else {
        
        ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Asterisk account auth missing."),
            new ResponseBodyErrorDTO("Preexiisting account auth should but does not exist."));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }    
      
    } else {

      if (approved) {

        _logger.info("DEBUG approved");

        AnontionAccount newAccount = new AnontionAccount(ts, 
            name, 
            client, 
            pub, 
            plaintext, 
            countersign, 
            false);
               
        AsteriskEndpoint endpoints = endpointBean.createAsteriskEndppint(newAccount.getPub(), 
            newAccount.getName());
                                    
        AsteriskAuth auths = authBean.createAsteriskAuth(newAccount.getPub(), 
            AnontionSecurity.tobase93FromBase64(AnontionSecuritySHA.hash(newAccount.getPub())), 
            AnontionSecurity.generatePassword());

        if (auths.getUsername().isEmpty()) {
          
          ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Id key invalid."),
              new ResponseBodyErrorDTO("Id key invalid."));

          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        AsteriskAor aors = aorBean.createAsteriskAor(pub);
        
        AnontionAccount account = null;
        
        try {

          _logger.info("DEBUG saving Account(+Asterisk)");
          
          account = accountService.saveTxAccountAndEndpoint(newAccount, endpoints, auths, aors);
        
        } catch (Exception e) {
          
          _logger.exception(e);
          
          ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Failed asterisk update."),
              new ResponseBodyErrorDTO("Failed asterisk update."));

          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        body = new ResponseAccountBodyDTO(account.getId(), account.getTs(), account.getName(),
            account.getApplication(), 100.0f, approved, auths.getUsername(), auths.getPassword());
      
      } else {
      
        _logger.info("DEBUG NOT approved");

        Float progress = ((now - ts) / (float) applicationTimeout) * 100.0f;
        
        body = new ResponseAccountBodyDTO(new UUID(0L, 0L), ts, name, client, progress, approved, "", "");
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

  public boolean valid(Long ts, Long now) { 
    
    return ((now - ts) > applicationTimeout);
  }
  
  final private static AnontionLog _logger = new AnontionLog(AccountController.class.getName());
}


