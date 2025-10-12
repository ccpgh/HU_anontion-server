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
import com.anontion.services.service.AccountService;
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
import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionPOW;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.dto.response.ResponsePostAccountBodyDTO;

import jakarta.validation.Valid;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;

@RestController
public class AccountPostController {

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
  private AccountService accountService;
  
  private double _applicationTimeout = AnontionPOW._ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT;
  
  private String _applicationPowTarget = "";
  
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
    
    String powLow  = dto.getPowLow();
    String powHigh = dto.getPowHigh();
    String powNonce = dto.getPowNonce();

    AnontionApplicationId primaryKey = new AnontionApplicationId(
        dto.getClientName(),
        dto.getClientTs(),
        dto.getClientId());

    Optional<AnontionApplication> applicationO = applicationRepository.findById(primaryKey);

    if (!applicationO.isPresent()) {

      return Responses.getBAD_REQUEST(
          "No application.", 
          "Application missing " + primaryKey.description());
    }

    AnontionApplication application = applicationO.get();
    
    if (!application.getPowText().equals(dto.getPowText()) || 
        !application.getPowTarget().equals(dto.getPowTarget()) || 
        application.isDisabled() ||
        !AnontionStrings.isHex(powLow) ||
        !AnontionStrings.isHex(powHigh) || 
        !AnontionStrings.isHex(powNonce)) {
      
      _logger.info("account post mismnatch application text '" + 
          application.getPowText() + 
          "' vs. '" + 
          dto.getPowText() + 
          "' target '" + 
          application.getPowTarget() + 
          "' vs. '" + 
          dto.getPowTarget() + 
          "' nonce '" +
          dto.getPowNonce() + 
          "' low '" +
          powLow + 
          "' high '" +
          powHigh + 
          "'");
      
      return Responses.getBAD_REQUEST(
          "No active application.", 
          "Application POW mismatch.");
    }

    if (AnontionSecurityECDSA.decodePublicKeyFromBase64XY(application.getClientPub()) == null) {

      return Responses.getBAD_REQUEST(
          "Bad pub.", 
          "Received client pub key invalid PublicKey.");
    }

    ECPublicKeyParameters publicKey = 
        AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(application.getClientPub());

    if (publicKey == null) {

      return Responses.getBAD_REQUEST(
          "Bad pub.", 
          "Received client pub key invalid ECPublicKeyParameters.");
    }

    if (!AnontionSecurityECDSA.checkSignature(application.getClientUID(), dto.getClientSignature(), publicKey)) {

      return Responses.getBAD_REQUEST(
          "Bad signature.", 
          "Client signature of proof token invalid.");
    } 
    
    Long nowTs = AnontionTime.tsN();

    Optional<AnontionAccount> accountO = accountRepository.findByClientTsAndClientNameAndClientId(
        dto.getClientTs(),
        dto.getClientName(),
        dto.getClientId());

    ResponsePostAccountBodyDTO body = null;
    
    if (accountO.isPresent()) {
      
      AnontionAccount account = accountO.get();

      Optional<AsteriskAuth> authO = authRepository.findById(
          AnontionSecurity.makeSafeIfRequired(account.getClientPub()));

      if (authO.isPresent()) {
        
        AsteriskAuth auth = authO.get();

        body = new ResponsePostAccountBodyDTO(
            account.getAccountId(), 
            account.getClientTs(), 
            account.getClientName(),
            account.getClientId(), 
            AnontionSecurity.makeSafeIfRequired(account.getClientPub()),
            auth.getUsername(),
            "",
            AnontionAccount.CONSTANT_COMPLETED_POW_PERCENTAGE, 
            account.getSipExpiration(),
            true);

      } else {

        return Responses.getBAD_REQUEST(
            "Asterisk account auth missing.", 
            "Preexiisting account auth should but does not exist.");
      }    
      
    } else {

      if (AnontionPOW.isApprovedTime(dto.getClientTs(), nowTs, _applicationTimeout) && 
          AnontionPOW.isApprovedPow(powLow, powHigh, application.getPowText(), application.getPowTarget(), powNonce)) {

        String foreignKey1 = AnontionSecurity.generateForeignKey();
        String foreignKey2 = AnontionSecurity.generateForeignKey();
        
        AnontionAccount account = new AnontionAccount(
            dto.getClientTs(),
            dto.getClientName(),
            dto.getClientId(),
            dto.getClientSignature(), 
            application.getServerSignature(), 
            application.getClientPub(), 
            application.getClientUID(), 
            AnontionAccount.DEFAULT_defaultExpiration,
            AnontionAccount.DEFAULT_minimumExpiration,
            AnontionAccount.DEFAULT_maximumExpiration,
            false, "anonymous");
              
        AsteriskEndpoint endpoints = endpointBean.createAsteriskEndppint(
            AnontionSecurity.makeSafeIfRequired(account.getClientPub()),
            foreignKey1);
                     
        String password = AnontionSecurity.generatePassword();

        String md5Passsword = AnontionStrings.generateAsteriskMD5(foreignKey2, AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM, password);

        AsteriskAuth auths = authBean.createAsteriskAuth(
            foreignKey1,
            foreignKey2, //account.getClientName(),
            md5Passsword,
            AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM,
            AnontionConfig._ASTERISK_PASSWORD_ENCODING_AUTHTYPE);

        if (auths.getUsername().isEmpty()) {
          
          return Responses.getBAD_REQUEST(
              "Id key invalid.");
        }
        
        AsteriskAor aors = aorBean.createAsteriskAor(
            AnontionSecurity.makeSafeIfRequired(application.getClientPub()));
        
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
            account.getAccountId(), 
            account.getClientTs(), 
            account.getClientName(),
            account.getClientId(), 
            AnontionSecurity.makeSafeIfRequired(account.getClientPub()), 
            auths.getUsername(), 
            password, 
            AnontionAccount.CONSTANT_COMPLETED_POW_PERCENTAGE, 
            account.getSipExpiration(),
            true);
      
      } else {
              
        body = new ResponsePostAccountBodyDTO(
            new UUID(0L, 0L), 
            dto.getClientTs(),
            dto.getClientName(),
            dto.getClientId(),
            "", 
            "", 
            "", 
            (float) AnontionPOW.calculateProgress(powNonce), 
            0,
            false);
      }
    }

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostConstruct
  public void init() {
    
    this.initApplicationTimeout();

    this.initApplicationPOW();
  }

  private void initApplicationPOW() { // NYI merge
    
    String context = servletContext.getInitParameter("pow-initial-target");

    if (context != null) {
      
      try {
      
        String pow = context.trim();
     
        if (!AnontionStrings.isHex(pow) ||
            pow.length() != 64) {
          
          _applicationPowTarget = AnontionPOW.CONST_TARGET;
        
        } else {
          
          _applicationPowTarget = pow.toLowerCase();  
        }
     
        _logger.info("Application POW target is " + _applicationPowTarget);

      } catch (Exception e) {

        _applicationPowTarget = AnontionPOW.CONST_TARGET;        
      }
    }
  }

  private void initApplicationTimeout() {
    
    String context = servletContext.getInitParameter("application-timeout");

    if (context != null) {
      
      try {
      
        _applicationTimeout = Integer.parseInt(context);
      
      } catch (NumberFormatException e) {
    
        _applicationTimeout = AnontionPOW._ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT;
      }
    
    } else {
    
      _applicationTimeout =  AnontionPOW._ACCOUNT_CONTROLLER_ACCOUNT_TIMEOUT; 
    }

    _logger.info("Application timeout is " + _applicationTimeout);
  }

  final private static AnontionLog _logger = new AnontionLog(AccountPostController.class.getName());
}

