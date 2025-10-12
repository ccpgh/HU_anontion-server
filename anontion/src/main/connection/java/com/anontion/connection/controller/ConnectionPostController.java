package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.anontion.models.image.model.AnontionImage;
import com.anontion.models.image.repository.AnontionImageRepository;

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

  @Autowired
  private AnontionImageRepository imageRepository;
  
  @PostMapping(path = "/connection/")
  public ResponseEntity<ResponseDTO> postConnection(@Valid @RequestBody RequestPostConnectionDTO request,
      BindingResult bindingResult) { // NYI clean up file code !!!!

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      return Responses.getBAD_REQUEST(
          "Invalid parameters bindings!", 
          message);      
    }
    
    String connectionType = request.getBody().getConnectionType();
    
    if (connectionType.equals("multiple") || connectionType.equals("broadcast")) {
      
      return postMultipleOrBroadcastConnection(request.getBody());
    }

    if (connectionType.equals("direct")) {
      
      return postDirectConnection(request.getBody());
    }

    if (connectionType.equals("indirect") || connectionType.equals("roll")) {
     
      return postIndirectOrRollConnection(request.getBody());
    }
    
    return Responses.getBAD_REQUEST(
        "Invalid parameters as connection type '" + connectionType + "' is unknown!"); 
  }
  

  public ResponseEntity<ResponseDTO> postDirectConnection(RequestPostConnectionBodyDTO dto) {
    
    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature1 = dto.getClientSignature1();   
    String clientSignature2 = dto.getClientSignature2();  
    String rollSipAddress = dto.getRollSipAddress(); // NYI reset this to null after final connection step.
    String label = dto.getLabel();
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
          false,
          "",
          "",
          "",
          "",
          "Direct connection same local and remote accounts.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Direct connection same local and remote accounts."), body);

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
          false,
          "",
          "",
          "",
          "",
          "Direct connection no account");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "No account."), body);

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
          false,
          "",
          "",
          "",
          "",
          "Direct connection bad master signature.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad master signature."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    String buffer6 = AnontionStrings.concat(new String[] {
        localSipAddress, 
        remoteSipAddress }, 
        "_");
    
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
          false,
          "",
          "",
          "",
          "",
          "Direct connection bad local signature");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Direct connection bad local signature."), body);

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

    StringBuilder returnPassword = new StringBuilder();

    StringBuilder returnSipUserId = new StringBuilder();
    
    StringBuilder returnSipLabel = new StringBuilder();
    
    StringBuilder returnPhoto = new StringBuilder();

    String clientUID = AnontionStrings.concatLowercase(new String[] { 
        clientName, 
        nowTs.toString(), 
        clientId.toString() }, ":");

    if (connectionService.createTxAccountIfConnected(sipEndpointA, sipEndpointB, isRetry, localSipAddress, 
        remoteSipAddress, returnPassword, returnSipUserId, returnSipLabel, returnPhoto, clientTs, clientName, clientId, clientUID)) {
      
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
          false,
          returnPassword.toString(),
          returnSipUserId.toString(),
          returnSipLabel.toString(),
          returnPhoto.toString(),
          "Ok connection connected and account exists.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Ok."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    if (!connectionService.saveTxConnectionDirect(sipTsA, sipEndpointA, sipSignatureA,
        sipTsB, sipEndpointB, sipSignatureB, isRetry)) {

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
          !isRetry.get(),
          false,
          isRetry.get(),
          "",
          "",
          "",
          "",
          "Direct connection db updates not completed");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Direct connection db update not completed."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    String buffer5 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress,
        nowTs.toString(),
        "false_true_true"},
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer5);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        true,
        true,
        "",
        "",
        "",
        "",
        "Ok connection connected.");

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public ResponseEntity<ResponseDTO> postIndirectOrRollConnection(RequestPostConnectionBodyDTO dto) {
        
    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature1 = dto.getClientSignature1();   
    String clientSignature2 = dto.getClientSignature2();   
    String label = dto.getLabel();
    String photo = dto.getPhoto();
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
          false,
          "",
          "",
          "",
          "",
          "Indirect or Roll connection no account");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Indirect or Roll connection no account."), body);

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
          false,
          "",
          "",
          "",
          "",
          "Indirect or Roll connection bad master signature.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad master signature."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    String buffer6 = AnontionStrings.concat(new String[] {
        localSipAddress, 
        remoteSipAddress }, 
        "_");
    
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
          false,
          "",
          "",
          "",
          "",
          "indirect or roll connection bad local signature.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad local signature."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    if (!photo.isEmpty()) {
    
      Optional<AnontionImage> image0 = imageRepository.findById(localSipAddress);
      
      if (image0.isEmpty()) {
       
        try {
          
          AnontionImage image = new AnontionImage(localSipAddress, photo);
        
          image = imageRepository.save(image);
        
        } catch (DataIntegrityViolationException e) {
        
          _logger.exception(e);
        
        } catch (Exception e) {
          
          _logger.exception(e);
        }        
      }
    }    
    
    if (!localSipAddress.equals(remoteSipAddress)) {
      
      return postIndirectOrRollConnectionDifferent(dto);
    }

    return postIndirectOrRollConnectionSame(dto);
  }
  
  public ResponseEntity<ResponseDTO> postIndirectOrRollConnectionSame(RequestPostConnectionBodyDTO dto) {
        
    String localSipAddress = dto.getLocalSipAddress();
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature2 = dto.getClientSignature2();   
    String rollSipAddress = dto.getRollSipAddress();
    String connectionType = dto.getConnectionType();
    String label = dto.getLabel();
    Long nowTs = dto.getNowTs();

    Long sipTsA = AnontionTime.tsN();
    String sipEndpointA = localSipAddress;
    String sipSignatureA = clientSignature2;
    String sipLabelA = label;

    Long sipTsB = null;
    String sipEndpointB = localSipAddress;
    String sipSignatureB = null;
    String sipLabelB = label;

    AtomicBoolean isRetry = new AtomicBoolean(false);
    
    StringBuilder buffer = new StringBuilder();
    
    if (!connectionService.saveTxConnectionIndirectOrRollBase(sipTsA, sipEndpointA, sipSignatureA, sipLabelA,
        sipTsB, sipEndpointB, sipSignatureB, sipLabelB, isRetry, buffer, rollSipAddress, connectionType)) {

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
          !isRetry.get(),
          false,
          isRetry.get(),
          "",
          "",
          "",
          "",
          "Connection db base insert not completed");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "connection indirect or roll db base insert not completed."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    String buffer5 = AnontionStrings.concat(new String[] {
        localSipAddress, 
        buffer.toString(), // remoteSipAddress,
        nowTs.toString(),
        "false_true_true"}, 
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer5);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        buffer.toString(), // remoteSipAddress,
        nowTs,
        signature,
        false,
        true,
        true,
        "",
        "",
        "",
        "",
        "OK. connection connected.");

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  public ResponseEntity<ResponseDTO> postIndirectOrRollConnectionDifferent(RequestPostConnectionBodyDTO dto) {

    String localSipAddress = dto.getLocalSipAddress();
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature2 = dto.getClientSignature2();   
    String connectionType = dto.getConnectionType();
    String label = dto.getLabel();
    Long nowTs = dto.getNowTs();

    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();

    Long sipTsA = null;
    String sipEndpointA = null;
    String sipSignatureA = null;
    String sipSignatureACreate = null;
    String sipLabelA = null;

    Long sipTsB = null;
    String sipEndpointB = null;
    String sipSignatureB = null;
    String sipSignatureBCreate = null;
    String sipLabelB = null;
    
    if (localSipAddress.compareTo(remoteSipAddress) < 0) {

      sipEndpointA = localSipAddress;
      sipSignatureA = clientSignature2;
      sipTsA = nowTs;
      sipLabelA = label;
      
      sipSignatureACreate = localSipAddress;
      sipSignatureBCreate = remoteSipAddress;
      
    } else {
      
      sipEndpointB = localSipAddress;      
      sipSignatureB = clientSignature2;
      sipTsB = nowTs;
      sipLabelB = label;
      
      sipSignatureBCreate = localSipAddress;
      sipSignatureACreate = remoteSipAddress;
    }
    
    AtomicBoolean isRetry = new AtomicBoolean(false);
    
    StringBuilder returnPassword = new StringBuilder();

    StringBuilder returnSipUserId = new StringBuilder();
    
    StringBuilder returnSipLabel = new StringBuilder();

    StringBuilder returnPhoto = new StringBuilder();

    String clientUID = AnontionStrings.concatLowercase(new String[] { 
        clientName, 
        nowTs.toString(), 
        clientId.toString() }, ":");
    
    if (connectionService.createTxAccountIfConnected(sipSignatureACreate, sipSignatureBCreate, isRetry, localSipAddress, remoteSipAddress, 
        returnPassword, returnSipUserId, returnSipLabel, returnPhoto, clientTs, clientName, clientId, clientUID)) { 
      
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
          false,
          returnPassword.toString(),
          returnSipUserId.toString(),
          returnSipLabel.toString(),
          returnPhoto.toString(),
          "Ok connection connected and account exists.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Ok."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    if (!connectionService.saveTxConnectionIndirectOrRollUpdate(sipTsA, sipEndpointA, sipSignatureA, sipLabelA,
        sipTsB, sipEndpointB, sipSignatureB, sipLabelB, isRetry, localSipAddress, remoteSipAddress, connectionType)) {

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
          !isRetry.get(),
          false,
          isRetry.get(),
          "",
          "",
          "",
          "",
          "Connection indirect or roll base update not completed");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "connection base update not completed."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    String buffer5 = AnontionStrings.concat(new String[] {
        localSipAddress, 
        remoteSipAddress,
        nowTs.toString(),
        "false_true_true"}, 
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer5);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        true,
        true,
        "",
        "",
        "",
        "",
        "Ok. Connection indirect or roll connected. Odd values.");

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);
    
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public ResponseEntity<ResponseDTO> postMultipleOrBroadcastConnection(RequestPostConnectionBodyDTO dto) {

    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String localSipAddress = dto.getLocalSipAddress();   
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature1 = dto.getClientSignature1();   
    String clientSignature2 = dto.getClientSignature2();   
    String label = dto.getLabel();
    String photo = dto.getPhoto();
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
          false,
          "",
          "",
          "",
          "",
          "Multiple connection no account");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Multiple connection no account."), body);

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
          false,
          "",
          "",
          "",
          "",
          "Multiple connection bad master signature.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad master signature."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    String buffer6 = AnontionStrings.concat(new String[] {
        localSipAddress, 
        remoteSipAddress }, 
        "_");
    
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
          false,
          "",
          "",
          "",
          "",
          "Multiple connection bad local signature.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Bad local signature."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    if (!photo.isEmpty()) {
      
      Optional<AnontionImage> image0 = imageRepository.findById(localSipAddress);
      
      if (image0.isEmpty()) {
       
        try {
          
          AnontionImage image = new AnontionImage(localSipAddress, photo);
        
          image = imageRepository.save(image);
        
        } catch (DataIntegrityViolationException e) {
        
          _logger.exception(e);
        
        } catch (Exception e) {
          
          _logger.exception(e);
        }        
      }
    }    
    
    if (!localSipAddress.equals(remoteSipAddress)) {
      
      return postMultipleConnectionDifferent(dto);
    }

    return postMultipleOrBroadcastConnectionSame(dto);
  }
  
  public ResponseEntity<ResponseDTO> postMultipleConnectionDifferent(RequestPostConnectionBodyDTO dto) {
    
    String localSipAddress = dto.getLocalSipAddress();
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature2 = dto.getClientSignature2();   
    String connectionType = dto.getConnectionType();
    Long nowTs = dto.getNowTs();

    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();
    String sipLabel = dto.getLabel();
    
    Long sipTsA = null;
    String sipEndpointA = null;
    String sipSignatureA = null;
    String sipLabelA = null;
    
    Long sipTsB = null;
    String sipEndpointB = null;
    String sipSignatureB = null;
    String sipLabelB = null;
    
    if (localSipAddress.compareTo(remoteSipAddress) < 0) {

      sipEndpointA = localSipAddress;
      sipSignatureA = clientSignature2;
      sipTsA = nowTs;
      sipLabelA = sipLabel;

      sipEndpointB = remoteSipAddress;
      sipTsB = nowTs;
      sipLabelB = sipLabel;

    } else {

      sipEndpointA = remoteSipAddress;
      sipTsA = nowTs;
      sipLabelA = sipLabel;

      sipEndpointB = localSipAddress;      
      sipSignatureB = clientSignature2;
      sipTsB = nowTs;
      sipLabelB = sipLabel;
    }

    AtomicBoolean isRetry = new AtomicBoolean(false);
    
    if (!connectionService.saveTxConnectionMultipleUpdate(sipTsA, sipEndpointA, sipSignatureA, sipLabelA,
        sipTsB, sipEndpointB, sipSignatureB, sipLabelB, isRetry, localSipAddress, remoteSipAddress, connectionType)) {

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
          !isRetry.get(),
          false,
          isRetry.get(),
          "",
          "",
          "",
          "",
          "Connection db base update failed");

      ResponsePostDTO response = new ResponsePostDTO(
         new ResponseHeaderDTO(true, 0, "connection db base update failed."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    StringBuilder returnPassword = new StringBuilder();

    StringBuilder returnSipUserId = new StringBuilder();

    StringBuilder returnSipLabel = new StringBuilder();

    StringBuilder returnPhoto = new StringBuilder();

    String clientUID = AnontionStrings.concatLowercase(new String[] { 
        clientName, 
        nowTs.toString(), 
        clientId.toString() }, ":");
    
    if (connectionService.createTxAccountIfConnected(sipEndpointA, sipEndpointB, isRetry, localSipAddress, 
        remoteSipAddress, returnPassword, returnSipUserId, returnSipLabel, returnPhoto, clientTs, clientName, clientId, clientUID)) {
      
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
          false,
          returnPassword.toString(),
          returnSipUserId.toString(),
          returnSipLabel.toString(),
          returnPhoto.toString(),
          "Ok connection connected and account exists.");

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Ok."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    String buffer5 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress,
        nowTs.toString(),
        "false_true_true"},
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer5);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        true,
        true,
        "",
        "",
        "",
        "",
        "OK. connection connected.");

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  public ResponseEntity<ResponseDTO> postMultipleOrBroadcastConnectionSame(RequestPostConnectionBodyDTO dto) {
 
    String localSipAddress = dto.getLocalSipAddress();
    String remoteSipAddress = dto.getRemoteSipAddress();
    String clientSignature2 = dto.getClientSignature2();  
    String connectionType = dto.getConnectionType();   
    Double latitude = dto.getLatitude();
    Double longitude = dto.getLongitude();
    String sipLabel = dto.getLabel();
    Long nowTs = dto.getNowTs();

    UUID clientId = dto.getClientId();
    String clientName = dto.getClientName();   
    Long clientTs = dto.getClientTs();

    Long sipTsA = AnontionTime.tsN();
    String sipEndpointA = localSipAddress;
    String sipSignatureA = clientSignature2;
    String sipLabelA = sipLabel;

    Long sipTsB = null;
    String sipEndpointB = localSipAddress;
    String sipSignatureB = null;
    String sipLabelB = sipLabel;
    
    AtomicBoolean isRetry = new AtomicBoolean(false);
    
    Long timeoutTs = AnontionTime.tsN() + AnontionConfig._CONTACT_CONNECTION_BROADCAST_TIMEOUT;
    
    if (!connectionService.saveTxConnectionMultipleOrBroadcastBase(sipTsA, sipEndpointA, sipSignatureA, sipLabelA,
        sipTsB, sipEndpointB, sipSignatureB, sipLabelB, isRetry, connectionType, latitude, longitude, timeoutTs)) {

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
          !isRetry.get(),
          false,
          isRetry.get(),
          "",
          "",
          "",
          "",
          "Connection db base insert not completed",
          timeoutTs);

      ResponsePostDTO response = new ResponsePostDTO(
         new ResponseHeaderDTO(true, 0, "connection db base insert not completed."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }

    StringBuilder returnPassword = new StringBuilder();

    StringBuilder returnSipUserId = new StringBuilder();

    StringBuilder returnSipLabel = new StringBuilder();

    StringBuilder returnPhoto = new StringBuilder();

    StringBuilder returnTimeoutTs = new StringBuilder();

    String clientUID = AnontionStrings.concatLowercase(new String[] { 
        clientName, 
        nowTs.toString(), 
        clientId.toString() }, ":");

    if (connectionService.createTxAccountIfConnected(sipEndpointA, isRetry, localSipAddress, remoteSipAddress,
        returnPassword, returnSipUserId, returnSipLabel, returnPhoto, clientTs, clientName, clientId, clientUID, returnTimeoutTs, connectionType)) { 
      
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
          false,
          returnPassword.toString(),
          returnSipUserId.toString(),
          returnSipLabel.toString(),
          returnPhoto.toString(),
          "Ok connection connected and account exists.",
          returnTimeoutTs.isEmpty() ? 0 : Long.parseLong(returnTimeoutTs.toString()));

      ResponsePostDTO response = new ResponsePostDTO(
          new ResponseHeaderDTO(true, 0, "Ok."), body);

      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    String buffer5 = AnontionStrings.concat(new String[] { 
        localSipAddress, 
        remoteSipAddress,
        nowTs.toString(),
        "false_true_true"}, 
        "_");
    
    String signature = AnontionSecurityECDSA.sign(buffer5);

    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
        remoteSipAddress,
        nowTs,
        signature,
        false,
        true,
        true,
        "",
        "",
        "",
        "",
        "OK. connection connected.",
        returnTimeoutTs.isEmpty() ? 0 : Long.parseLong(returnTimeoutTs.toString()));

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionPostController.class.getName());
}


