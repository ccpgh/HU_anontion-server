package com.anontion.application.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.request.RequestPostApplicationDTO;

import com.anontion.common.dto.response.ResponsePostDTO;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionSecuritySCRYPT;
import com.anontion.common.security.AnontionPOW;
import com.anontion.common.dto.response.ResponsePostApplicationBodyDTO;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;

import com.anontion.models.application.model.AnontionApplication;
import com.anontion.models.application.model.AnontionApplicationId;
import com.anontion.models.application.repository.AnontionApplicationRepository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

@RestController
public class ApplicationPostController {

  @Autowired
  private AnontionApplicationRepository applicationRepository;

  @Autowired
  private ServletContext servletContext;
  
  private String _applicationPowTarget = "";
    
  Cache<String, Boolean> nonceCache = Caffeine.newBuilder()
      .expireAfterWrite(AnontionConfig._REQUEST_TS_VALIDITY_MARGIN, TimeUnit.SECONDS)
      .build();
  
  @PostMapping(path = "/application/")
  public ResponseEntity<ResponseDTO> postApplication(@Valid @RequestBody RequestPostApplicationDTO request, 
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream()
          .map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      return Responses.getBAD_REQUEST("Bad arguments", message);
    }

    String nonce  = request.getBody().getNonce();  
    UUID   clientId   = request.getBody().getClientId();
    String clientPub  = request.getBody().getClientPub();  
    String enceyptedName = null;
    Long diff = request.getBody().getNowTs() - AnontionTime.tsN();

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
    
    while (true) { 
      
      // Given low chance of clash this is safe enough for now. 
      // TODO put in auto-retry transaction.

      enceyptedName = 
          AnontionSecuritySCRYPT.hashBase64(UUID.randomUUID().toString());

      if (!enceyptedName.isBlank()) {

        if (!applicationRepository.existsByClientName(enceyptedName)) {
          
          break;
        }
      }
      
      AnontionTime.sleep();
    }
     
    Long nowTs = AnontionTime.tsN();

    boolean exists = applicationRepository.existsById(new AnontionApplicationId(
        enceyptedName, 
        nowTs, 
        clientId));

    if (exists) {

      return Responses.getBAD_REQUEST(
          "Application already exists!");
    }

    AnontionPOW pow = new AnontionPOW(_applicationPowTarget);

    String clientUID = AnontionStrings.concatLowercase(new String[] { 
        enceyptedName, 
        nowTs.toString(), 
        clientId.toString() }, ":");

    AnontionApplication application = 
        new AnontionApplication(enceyptedName, 
            nowTs, 
            clientId,
            clientPub, 
            clientUID, 
            AnontionSecurityECDSA.sign(clientUID),
            pow.getText(), 
            pow.getTarget().toLowerCase(), 
            false);

    applicationRepository.save(application);

    ResponsePostApplicationBodyDTO body =
        new ResponsePostApplicationBodyDTO(application.getPowText(), 
            application.getPowTarget(), 
            enceyptedName, 
            AnontionSecurityECDSA.pubS(),
            clientUID, 
            application.getServerSignature(), 
            nowTs);

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 
            0, 
            "Ok"), 
        body);

    return new ResponseEntity<>(response, 
        HttpStatus.OK);    
  }
  
  @PostConstruct
  public void init() {
    
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

  
  final private static AnontionLog _logger = new AnontionLog(ApplicationPostController.class.getName());
}


