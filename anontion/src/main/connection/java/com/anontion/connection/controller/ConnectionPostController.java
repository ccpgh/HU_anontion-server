package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.asterisk.bean.AsteriskEndpointBean;
import com.anontion.common.dto.request.RequestPostAccountBodyDTO;
import com.anontion.common.dto.request.RequestPostAccountDTO;
import com.anontion.common.dto.request.RequestPostConnectionDTO;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.ResponsePostDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionSecurityECIES_ECDH;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.account.repository.AnontionAccountRepository;
import com.anontion.common.dto.request.RequestPostConnectionBodyDTO;
import com.anontion.common.dto.response.ResponsePostConnectionBodyDTO;
import com.anontion.services.service.ConnectionService; 

import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import jakarta.persistence.Column;
import jakarta.validation.Valid;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class ConnectionPostController {
  
  @Autowired
  private AnontionAccountRepository accountRepository;
  
  @Autowired
  private ConnectionService connectionService;
  
  @PostMapping(path = "/connection/")
  public ResponseEntity<ResponseDTO> postConnection(@Valid @RequestBody RequestPostConnectionDTO request,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      return Responses.getBAD_REQUEST(
          "Invalid parameters!", 
          message);      
    }

    RequestPostConnectionBodyDTO dto = request.getBody();

    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature1 = dto.getClientSignature1();   
    String clientSignature2 = dto.getClientSignature2();   
    Long nowTs = dto.getNowTs();
    
    String localSipAddressUnsafe = AnontionSecurity.decodeFromSafeBase64(localSipAddress);
    
    Long diff = nowTs - AnontionTime.tsN();
    
    if (diff > AnontionConfig._CONTACT_CONNECTION_MAX ||
        diff < AnontionConfig._CONTACT_CONNECTION_MIN) {
      
      return Responses.getBAD_REQUEST(
          "Invalid parameters!", 
          "bad nowTs"); 
    }
    
    _logger.info("DEBUG connection account keys clientTs '" + clientTs + "' clientName '" + clientName + "' clientId '" + clientId + "'");

    Optional<AnontionAccount> accountO = accountRepository.findByClientTsAndClientNameAndClientId(
        clientTs,
        clientName,
        clientId);
    
    if (accountO.isEmpty()) {

      StringBuilder buffer0 = new StringBuilder();

      buffer0.append(localSipAddress);
      buffer0.append("_");  
      buffer0.append(remoteSipAddress);
      buffer0.append("_"); 
      buffer0.append(nowTs);
      buffer0.append("_true_false_false");
      
      String signature = AnontionSecurityECDSA.sign(buffer0.toString());
      
      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);
      
      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "No account."), body);
      
      _logger.info("DEBUG connection no account");
      
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    AnontionAccount account = accountO.get();
    
    StringBuilder buffer1 = new StringBuilder();
    buffer1.append(clientId.toString().toLowerCase());
    buffer1.append("_");
    buffer1.append(clientName);
    buffer1.append("_");
    buffer1.append(clientTs.toString());
    buffer1.append("_");  
    buffer1.append(localSipAddress);
    buffer1.append("_");  
    buffer1.append(remoteSipAddress);
    buffer1.append("_"); 
    buffer1.append(nowTs);

    _logger.info("DEBUG connection cleartext1 '" + buffer1.toString() + "'");
    
    ECPublicKeyParameters pub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(account.getClientPub());
    
    if (!AnontionSecurityECDSA.checkSignature(buffer1.toString(), clientSignature1, pub)) {

      StringBuilder buffer2 = new StringBuilder();

      buffer2.append(localSipAddress);
      buffer2.append("_");  
      buffer2.append(remoteSipAddress);
      buffer2.append("_"); 
      buffer2.append(nowTs);
      buffer2.append("_true_false_false");
      
      String signature = AnontionSecurityECDSA.sign(buffer2.toString());
      
      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);
      
      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad master signature."), body);
      
      _logger.info("DEBUG connection bad master signature");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    StringBuilder buffer6 = new StringBuilder();
    buffer6.append(localSipAddress);
    buffer6.append("_");  
    buffer6.append(remoteSipAddress);
     
    _logger.info("DEBUG connection cleartext2 '" + buffer6.toString() + "' localSipAddress '" + localSipAddress + "' localSipAddressUnsafe '" + localSipAddressUnsafe + "'");

    String localPubString = AnontionSecurity.decodeFromSafeBase64(localSipAddress);
        
    ECPublicKeyParameters localPub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(localPubString);

    if (!AnontionSecurityECDSA.checkSignature(buffer6.toString(), clientSignature2, localPub)) {

      StringBuilder buffer2 = new StringBuilder();

      buffer2.append(localSipAddress);
      buffer2.append("_");  
      buffer2.append(remoteSipAddress);
      buffer2.append("_"); 
      buffer2.append(nowTs);
      buffer2.append("_true_false_false");
      
      String signature = AnontionSecurityECDSA.sign(buffer2.toString());
      
      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);
      
      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad local signature."), body);
      
      _logger.info("DEBUG connection bad local signature");
      
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    Long now = AnontionTime.tsN();
    
    Long sipTsA = null;
    String sipEndpointA = null;
    String sipSignatureA = null;

    Long sipTsB = null;
    String sipEndpointB = null;
    String sipSignatureB = null;

    if (localSipAddress.compareTo(remoteSipAddress) < 0) {

      sipTsA = now;
      sipEndpointA = localSipAddress;
      sipSignatureA = clientSignature2;

      sipEndpointB = remoteSipAddress;

    } else {
      
      sipEndpointA = remoteSipAddress;

      sipTsB = now;
      sipEndpointB = localSipAddress;
      sipSignatureB = clientSignature2;
    }
    
    AtomicBoolean isRetry = new AtomicBoolean(false);
     
    if (!connectionService.saveTxConnection(sipTsA, sipEndpointA, sipSignatureA, sipTsB, sipEndpointB, sipSignatureB, isRetry)) {
      
      StringBuilder buffer4 = new StringBuilder();

      buffer4.append(localSipAddress);
      buffer4.append("_");  
      buffer4.append(remoteSipAddress);
      buffer4.append("_"); 
      buffer4.append(nowTs);
      buffer4.append(isRetry.get() ? "_false_false_true" : "_true_false_false");
      
      String signature = AnontionSecurityECDSA.sign(buffer4.toString());
      
      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          isRetry.get() ? false : true,
          false,
          isRetry.get() ? true : false);
      
      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "db update not completed."), body);
      
      _logger.info("DEBUG connection db update not completed");
      
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    StringBuilder buffer5 = new StringBuilder();

    buffer5.append(localSipAddress);
    buffer5.append("_");  
    buffer5.append(remoteSipAddress);
    buffer5.append("_"); 
    buffer5.append(nowTs);
    buffer5.append("_false_true_false");
    
    String signature = AnontionSecurityECDSA.sign(buffer5.toString());
    
    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        true,
        false);
    
    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);
    
    _logger.info("DEBUG connection ok");
    
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionPostController.class.getName());
}


