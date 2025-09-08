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
import com.anontion.common.misc.AnontionStrings;
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
  private ConnectionService connectionService;

  @Autowired
  private AnontionAccountRepository accountRepository;

  @PostMapping(path = "/connection/")
  public ResponseEntity<ResponseDTO> postConnection(@Valid @RequestBody RequestPostConnectionDTO request,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      return Responses.getBAD_REQUEST(
          "Invalid parameters bindings!", 
          message);      
    }
    
    RequestPostConnectionBodyDTO dto = request.getBody();

    if (dto.getConnectionType().equals("multiple")) {
      
      return postMultipleConnection(dto);
    }

    if (dto.getConnectionType().equals("direct")) {
      
      return postDirectConnection(dto);
    }

    if (dto.getConnectionType().equals("indirect")) {
     
      return postIndirectConnection(dto);
    }
    
    return Responses.getBAD_REQUEST(
        "Invalid parameters!"); 
  }
  

  public ResponseEntity<ResponseDTO> postDirectConnection(RequestPostConnectionBodyDTO dto) {
    
    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature1 = dto.getClientSignature1();   
    String clientSignature2 = dto.getClientSignature2();   
    String connectionType = dto.getConnectionType();   
    Long nowTs = dto.getNowTs();

    String localSipAddressUnsafe = AnontionSecurity.decodeFromSafeBase64(localSipAddress);

    Long diff = nowTs - AnontionTime.tsN();

    if (diff > AnontionConfig._CONTACT_CONNECTION_MAX ||
        diff < AnontionConfig._CONTACT_CONNECTION_MIN) {

      return Responses.getBAD_REQUEST(
          "Invalid parameters time diff " + diff.toString(), 
          "bad nowTs"); 
    }

    _logger.info("DEBUG direct connection account keys clientTs '" + clientTs + 
        "' clientName '" + clientName + "' clientId '" + clientId + "'");

    if (localSipAddress.equals(remoteSipAddress)) {
      
      String buffer0 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress, 
          nowTs.toString(), 
          "true_false_false" }, 
          "_");
      
      String signature = AnontionSecurityECDSA.sign(buffer0);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "No account."), body);

      _logger.info("DEBUG direct connection same local and remote accounts");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    Optional<AnontionAccount> accountO = accountRepository.findByClientTsAndClientNameAndClientId(
        clientTs,
        clientName,
        clientId);

    if (accountO.isEmpty()) {
      
      String buffer0 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress, 
          nowTs.toString(), 
          "true_false_false" }, 
          "_");
      
      String signature = AnontionSecurityECDSA.sign(buffer0);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "No account."), body);

      _logger.info("DEBUG direct connection no account");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    AnontionAccount account = accountO.get();

    String buffer1 = AnontionStrings.concat(new String[] { 
        clientId.toString().toLowerCase(), 
        clientName, 
        clientTs.toString(), 
        localSipAddress, 
        remoteSipAddress, 
        connectionType, 
        nowTs.toString() }, 
        "_");
    
    _logger.info("DEBUG direct connection cleartext1 '" + buffer1.toString() + "'");

    ECPublicKeyParameters pub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(account.getClientPub());

    if (!AnontionSecurityECDSA.checkSignature(buffer1, clientSignature1, pub)) {

      String buffer2 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress, 
          nowTs.toString(), 
          "true_false_false" }, 
          "_");
      
      String signature = AnontionSecurityECDSA.sign(buffer2);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad master signature."), body);

      _logger.info("DEBUG direct connection bad master signature");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    _logger.info("DEBUG direct connection signature1 ok");

    String buffer6 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress }, 
        "_");
    
    _logger.info("DEBUG direct connection cleartext2 '" + buffer6.toString() + "' localSipAddress '" + localSipAddress + 
        "' localSipAddressUnsafe '" + localSipAddressUnsafe + "'");

    String localPubString = AnontionSecurity.decodeFromSafeBase64(localSipAddress);

    ECPublicKeyParameters localPub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(localPubString);

    if (!AnontionSecurityECDSA.checkSignature(buffer6, clientSignature2, localPub)) {

      String buffer2 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress,
          nowTs.toString(),
          "true_false_false"}, 
          "_");
      
      String signature = AnontionSecurityECDSA.sign(buffer2);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad local signature."), body);

      _logger.info("DEBUG direct connection bad local signature");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    _logger.info("DEBUG direct connection signature2 ok");

    Long now = AnontionTime.tsN();

    Long sipTsA = null;
    String sipEndpointA = null;
    String sipSignatureA = null;

    Long sipTsB = null;
    String sipEndpointB = null;
    String sipSignatureB = null;

    if (localSipAddress.compareTo(remoteSipAddress) < 0) {

      _logger.info("DEBUG direct connection selected A");

      sipTsA = now;
      sipEndpointA = localSipAddress;
      sipSignatureA = clientSignature2;

      sipEndpointB = remoteSipAddress;

    } else {

      _logger.info("DEBUG direct connection selected B");

      sipEndpointA = remoteSipAddress;

      sipTsB = now;
      sipEndpointB = localSipAddress;
      sipSignatureB = clientSignature2;
    }

    AtomicBoolean isRetry = new AtomicBoolean(false);

    _logger.info("DEBUG direct connection calling connectionService");

    if (!connectionService.saveTxConnectionDirect(sipTsA, sipEndpointA, sipSignatureA, sipTsB, sipEndpointB, sipSignatureB, isRetry)) {

      String buffer4 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress,
          nowTs.toString(),
          isRetry.get() ? "false_false_true" : "true_false_false"}, 
          "_");

      String signature = AnontionSecurityECDSA.sign(buffer4);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          isRetry.get() ? false : true,
              false,
              isRetry.get() ? true : false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "db update not completed."), body);

      _logger.info("DEBUG direct connection db update not completed");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    String buffer5 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress,
        nowTs.toString(),
        "false_true_false"}, 
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer5);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        true,
        false);

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    _logger.info("DEBUG direct connection ok");

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public ResponseEntity<ResponseDTO> postIndirectConnection(RequestPostConnectionBodyDTO dto) {
        
    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature1 = dto.getClientSignature1();   
    String clientSignature2 = dto.getClientSignature2();   
    String connectionType = dto.getConnectionType();   
    Long nowTs = dto.getNowTs();
    
    _logger.info("DEBUG clientId " + clientId);
    _logger.info("DEBUG clientName " + clientName);
    _logger.info("DEBUG clientTs " + clientTs);
    _logger.info("DEBUG localSipAddress " + localSipAddress);
    _logger.info("DEBUG remoteSipAddress " + remoteSipAddress);
    _logger.info("DEBUG clientSignature1 " + clientSignature1);
    _logger.info("DEBUG clientSignature2 " + clientSignature2);
    _logger.info("DEBUG connectionType " + connectionType);
    _logger.info("DEBUG nowTs " + nowTs);
    
    String localSipAddressUnsafe = AnontionSecurity.decodeFromSafeBase64(localSipAddress);

    Long diff = nowTs - AnontionTime.tsN();

    if (diff > AnontionConfig._CONTACT_CONNECTION_MAX ||
        diff < AnontionConfig._CONTACT_CONNECTION_MIN) {

      return Responses.getBAD_REQUEST(
          "Invalid parameters time diff " + diff.toString(), 
          "bad nowTs"); 
    }
    
    _logger.info("DEBUG indirect connection nowTs " + nowTs + " tN from message " + AnontionTime.tsN() + " diff " + diff);


    _logger.info("DEBUG indirect connection account keys clientTs '" + clientTs + 
        "' clientName '" + clientName + "' clientId '" + clientId + "'");

    Optional<AnontionAccount> accountO = accountRepository.findByClientTsAndClientNameAndClientId(
        clientTs,
        clientName,
        clientId);

    if (accountO.isEmpty()) {
      
      _logger.info("DEBUG account not found");
      
      String buffer0 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress, 
          nowTs.toString(), 
          "true_false_false" }, 
          "_");
      
      String signature = AnontionSecurityECDSA.sign(buffer0);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "No account."), body);

      _logger.info("DEBUG indirect connection no account");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    _logger.info("DEBUG account found");

    AnontionAccount account = accountO.get();

    String buffer1 = AnontionStrings.concat(new String[] { 
        clientId.toString().toLowerCase(), 
        clientName, 
        clientTs.toString(), 
        localSipAddress, 
        remoteSipAddress, 
        connectionType, 
        nowTs.toString() }, 
        "_");
    
    _logger.info("DEBUG indirect connection cleartext1 '" + buffer1.toString() + "'");

    ECPublicKeyParameters pub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(account.getClientPub());

    if (!AnontionSecurityECDSA.checkSignature(buffer1, clientSignature1, pub)) {

      String buffer2 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress, 
          nowTs.toString(), 
          "true_false_false" }, 
          "_");
      
      String signature = AnontionSecurityECDSA.sign(buffer2);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad master signature."), body);

      _logger.info("DEBUG indirect connection bad master signature");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    _logger.info("DEBUG indirect connection signature1 ok");

    String buffer6 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress }, 
        "_");
    
    _logger.info("DEBUG indirect connection cleartext2 '" + buffer6.toString() + "' localSipAddress '" + localSipAddress + 
        "' localSipAddressUnsafe '" + localSipAddressUnsafe + "'");

    String localPubString = AnontionSecurity.decodeFromSafeBase64(localSipAddress);

    ECPublicKeyParameters localPub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(localPubString);

    if (!AnontionSecurityECDSA.checkSignature(buffer6, clientSignature2, localPub)) {

      String buffer2 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress,
          nowTs.toString(),
          "true_false_false"}, 
          "_");
      
      String signature = AnontionSecurityECDSA.sign(buffer2);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          true,
          false,
          false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad local signature."), body);

      _logger.info("DEBUG indirect connection bad local signature");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    _logger.info("DEBUG indirect connection signature2 ok");
    
    if (!localSipAddress.equals(remoteSipAddress)) {
      
      return postIndirectConnectionDifferent(dto);
    }

    return postIndirectConnectionSame(dto);
  }
  
  public ResponseEntity<ResponseDTO> postIndirectConnectionSame(RequestPostConnectionBodyDTO dto) {
        
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature2 = dto.getClientSignature2();   
    Long nowTs = dto.getNowTs();

    Long sipTsA = AnontionTime.tsN();
    String sipEndpointA = localSipAddress;
    String sipSignatureA = clientSignature2;

    Long sipTsB = null;
    String sipEndpointB = localSipAddress;
    String sipSignatureB = null;

    AtomicBoolean isRetry = new AtomicBoolean(false);

    _logger.info("DEBUG connection calling saveTxConnectionIndirectBase");

    if (!connectionService.saveTxConnectionIndirectBase(sipTsA, sipEndpointA, sipSignatureA, sipTsB, sipEndpointB, sipSignatureB, isRetry)) {

      String buffer4 = AnontionStrings.concat(new String[] { 
          localSipAddress, 
          remoteSipAddress,
          nowTs.toString(),
          isRetry.get() ? "false_false_true" : "true_false_false"}, 
          "_");

      String signature = AnontionSecurityECDSA.sign(buffer4);

      ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
          remoteSipAddress,
          nowTs,
          signature,
          isRetry.get() ? false : true,
              false,
              isRetry.get() ? true : false);

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "db base insert not completed."), body);

      _logger.info("DEBUG connection base insert not completed");

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    _logger.info("DEBUG connection OK");

    String buffer5 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress,
        nowTs.toString(),
        "false_false_true"}, 
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer5);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        false,
        true);

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    _logger.info("DEBUG connection ok");

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  public ResponseEntity<ResponseDTO> postIndirectConnectionDifferent(RequestPostConnectionBodyDTO dto) {

    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature2 = dto.getClientSignature2();   
    Long nowTs = dto.getNowTs();
    
    String buffer2 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress,
        nowTs.toString(),
        "false_false_true"}, 
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer2);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        false,
        true);

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Different indirect connection values."), body);

    _logger.info("DEBUG indirect connection different values");

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public ResponseEntity<ResponseDTO> postMultipleConnection(RequestPostConnectionBodyDTO dto) {

    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature1 = dto.getClientSignature1();   
    String clientSignature2 = dto.getClientSignature2();   
    String connectionType = dto.getConnectionType();   
    Long nowTs = dto.getNowTs();
    
    _logger.info("NYI multiple");
    
    return Responses.getNYI();
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionPostController.class.getName());
}


