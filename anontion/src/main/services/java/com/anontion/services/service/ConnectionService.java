package com.anontion.services.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConnectionService {
   
  @Autowired
  private AnontionConnectionRepository connectionRepository;

  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionDirect(Long sipTsA, String sipEndpointA, String sipSignatureA, Long sipTsB, String sipEndpointB, String sipSignatureB, AtomicBoolean isRetry) {

    _logger.info("DEBUG saveTxConnectionDirect called");
    
    Optional<AnontionConnection> connection0 = null;
    
    try {

      connection0 = connectionRepository.findDirectBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG saveTxConnectionDirect exception " + e.toString() + " when trying to find for update.");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG saveTxConnectionDirect empty");

      AnontionConnection connection = new AnontionConnection(null, sipEndpointA, null, null, sipEndpointB, null, "direct");
      
      try {
        
        connection = connectionRepository.save(connection);

        isRetry.set(true);
        
        return false;

      } catch (DataIntegrityViolationException e) {

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        _logger.exception(e);

        isRetry.set(true);
        
        return false;
      }
      catch (Exception e) {

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        _logger.exception(e);

        isRetry.set(false);
        
        return false;
      }
    }

    _logger.info("DEBUG saveTxConnectionDirect not empty");

    AnontionConnection connection = connection0.get();
    
    if (connection.getSipTsA() != null && connection.getSipSignatureA() != null &&
        connection.getSipTsB() != null && connection.getSipSignatureB() != null) { 

      _logger.info("DEBUG saveTxConnection all populated");

      isRetry.set(false);
      
      return true;
    }

    // else check which side i am and update 
    
    if (sipTsA != null && sipSignatureA != null) {
      
      _logger.info("DEBUG saveTxConnectionDirect A update");

      if (connection.getSipTsA() == null) {
       
        _logger.info("DEBUG saveTxConnectionDirect A update 1");

        connection.setSipTsA(sipTsA);
      }
      
      if (connection.getSipSignatureA() == null) {

        _logger.info("DEBUG saveTxConnectionDirect A update 2");

        connection.setSipSignatureA(sipSignatureA);
      }
      
      try {
      
        _logger.info("DEBUG saveTxConnectionDirect A update save");

        connectionRepository.save(connection);
        
      } catch (Exception e) {

        _logger.exception(e);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      
      isRetry.set(true);
      
      return false;      
    }

    if (sipTsB != null && sipSignatureB != null) {

      _logger.info("DEBUG saveTxConnectionDirect B update");

      if (connection.getSipTsB() == null) {

        _logger.info("DEBUG saveTxConnectionDirect B update 1");

        connection.setSipTsB(sipTsB);
      }
      
      if (connection.getSipSignatureB() == null) {
        
        _logger.info("DEBUG saveTxConnectionDirect B update 2");
        
        connection.setSipSignatureB(sipSignatureB);
      }
      
      try {
        
        _logger.info("DEBUG saveTxConnectionDirect B update save");

        connectionRepository.save(connection);
        
      } catch (Exception e) {

        _logger.exception(e);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      
      isRetry.set(true);
      
      return false;      
    }

    // NOTE: as the saying goes - should not get here!
    
    _logger.info("DEBUG saveTxConnectionDirect hit bottom");
    
    isRetry.set(false);
    
    return false;
  }
  
  public ResponseEntity<ResponseDTO> postDirectConnection() {
    
//  UUID clientId = dto.getClientId();
//  String clientName = dto.getClientName();   
//  Long clientTs = dto.getClientTs();
//  String localSipAddress = dto.getLocalSipAddress();   
//  String remoteSipAddress = dto.getRemoteSipAddress();
//  String clientSignature1 = dto.getClientSignature1();   
//  String clientSignature2 = dto.getClientSignature2();   
//  Long nowTs = dto.getNowTs();
//  
//  String localSipAddressUnsafe = AnontionSecurity.decodeFromSafeBase64(localSipAddress);
//  
//  Long diff = nowTs - AnontionTime.tsN();
//  
//  if (diff > AnontionConfig._CONTACT_CONNECTION_MAX ||
//      diff < AnontionConfig._CONTACT_CONNECTION_MIN) {
//    
//    return Responses.getBAD_REQUEST(
//        "Invalid parameters!", 
//        "bad nowTs"); 
//  }
//  
//  _logger.info("DEBUG connection account keys clientTs '" + clientTs + "' clientName '" + clientName + "' clientId '" + clientId + "'");
//
//  Optional<AnontionAccount> accountO = accountRepository.findByClientTsAndClientNameAndClientId(
//      clientTs,
//      clientName,
//      clientId);
//  
//  if (accountO.isEmpty()) {
//
//    StringBuilder buffer0 = new StringBuilder();
//
//    buffer0.append(localSipAddress);
//    buffer0.append("_");  
//    buffer0.append(remoteSipAddress);
//    buffer0.append("_"); 
//    buffer0.append(nowTs);
//    buffer0.append("_true_false_false");
//    
//    String signature = AnontionSecurityECDSA.sign(buffer0.toString());
//    
//    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
//        remoteSipAddress,
//        nowTs,
//        signature,
//        true,
//        false,
//        false);
//    
//    ResponsePostDTO response = new ResponsePostDTO(
//        new ResponseHeaderDTO(true, 0, "No account."), body);
//    
//    _logger.info("DEBUG connection no account");
//    
//    return new ResponseEntity<>(response, HttpStatus.OK);
//  }
//  
//  AnontionAccount account = accountO.get();
//  
//  StringBuilder buffer1 = new StringBuilder();
//  buffer1.append(clientId.toString().toLowerCase());
//  buffer1.append("_");
//  buffer1.append(clientName);
//  buffer1.append("_");
//  buffer1.append(clientTs.toString());
//  buffer1.append("_");  
//  buffer1.append(localSipAddress);
//  buffer1.append("_");  
//  buffer1.append(remoteSipAddress);
//  buffer1.append("_"); 
//  buffer1.append(nowTs);
//
//  _logger.info("DEBUG connection cleartext1 '" + buffer1.toString() + "'");
//  
//  ECPublicKeyParameters pub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(account.getClientPub());
//  
//  if (!AnontionSecurityECDSA.checkSignature(buffer1.toString(), clientSignature1, pub)) {
//
//    StringBuilder buffer2 = new StringBuilder();
//
//    buffer2.append(localSipAddress);
//    buffer2.append("_");  
//    buffer2.append(remoteSipAddress);
//    buffer2.append("_"); 
//    buffer2.append(nowTs);
//    buffer2.append("_true_false_false");
//    
//    String signature = AnontionSecurityECDSA.sign(buffer2.toString());
//    
//    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
//        remoteSipAddress,
//        nowTs,
//        signature,
//        true,
//        false,
//        false);
//    
//    ResponsePostDTO response = new ResponsePostDTO(
//        new ResponseHeaderDTO(true, 0, "Bad master signature."), body);
//    
//    _logger.info("DEBUG connection bad master signature");
//
//    return new ResponseEntity<>(response, HttpStatus.OK);
//  }
//
//  _logger.info("DEBUG connection signature1 ok");
//
//  StringBuilder buffer6 = new StringBuilder();
//  buffer6.append(localSipAddress);
//  buffer6.append("_");  
//  buffer6.append(remoteSipAddress);
//   
//  _logger.info("DEBUG connection cleartext2 '" + buffer6.toString() + "' localSipAddress '" + localSipAddress + "' localSipAddressUnsafe '" + localSipAddressUnsafe + "'");
//
//  String localPubString = AnontionSecurity.decodeFromSafeBase64(localSipAddress);
//      
//  ECPublicKeyParameters localPub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(localPubString);
//
//  if (!AnontionSecurityECDSA.checkSignature(buffer6.toString(), clientSignature2, localPub)) {
//
//    StringBuilder buffer2 = new StringBuilder();
//
//    buffer2.append(localSipAddress);
//    buffer2.append("_");  
//    buffer2.append(remoteSipAddress);
//    buffer2.append("_"); 
//    buffer2.append(nowTs);
//    buffer2.append("_true_false_false");
//    
//    String signature = AnontionSecurityECDSA.sign(buffer2.toString());
//    
//    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
//        remoteSipAddress,
//        nowTs,
//        signature,
//        true,
//        false,
//        false);
//    
//    ResponsePostDTO response = new ResponsePostDTO(
//        new ResponseHeaderDTO(true, 0, "Bad local signature."), body);
//    
//    _logger.info("DEBUG connection bad local signature");
//    
//    return new ResponseEntity<>(response, HttpStatus.OK);
//  }
//  
//  _logger.info("DEBUG connection signature2 ok");
//
//  Long now = AnontionTime.tsN();
//  
//  Long sipTsA = null;
//  String sipEndpointA = null;
//  String sipSignatureA = null;
//
//  Long sipTsB = null;
//  String sipEndpointB = null;
//  String sipSignatureB = null;
//
//  if (localSipAddress.compareTo(remoteSipAddress) < 0) {
//
//    _logger.info("DEBUG connection selected A");
//    
//    sipTsA = now;
//    sipEndpointA = localSipAddress;
//    sipSignatureA = clientSignature2;
//
//    sipEndpointB = remoteSipAddress;
//
//  } else {
//
//    _logger.info("DEBUG connection selected B");
//
//    sipEndpointA = remoteSipAddress;
//
//    sipTsB = now;
//    sipEndpointB = localSipAddress;
//    sipSignatureB = clientSignature2;
//  }
//  
//  AtomicBoolean isRetry = new AtomicBoolean(false);
//   
//  _logger.info("DEBUG connection calling tx");
//
//  if (!connectionService.saveTxConnection(sipTsA, sipEndpointA, sipSignatureA, sipTsB, sipEndpointB, sipSignatureB, isRetry)) {
//    
//    StringBuilder buffer4 = new StringBuilder();
//
//    buffer4.append(localSipAddress);
//    buffer4.append("_");  
//    buffer4.append(remoteSipAddress);
//    buffer4.append("_"); 
//    buffer4.append(nowTs);
//    buffer4.append(isRetry.get() ? "_false_false_true" : "_true_false_false");
//    
//    String signature = AnontionSecurityECDSA.sign(buffer4.toString());
//    
//    ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
//        remoteSipAddress,
//        nowTs,
//        signature,
//        isRetry.get() ? false : true,
//        false,
//        isRetry.get() ? true : false);
//    
//    ResponsePostDTO response = new ResponsePostDTO(
//        new ResponseHeaderDTO(true, 0, "db update not completed."), body);
//    
//    _logger.info("DEBUG connection db update not completed");
//    
//    return new ResponseEntity<>(response, HttpStatus.OK);
//  }
//  
//  _logger.info("DEBUG connection OK");
//
//  StringBuilder buffer5 = new StringBuilder();
//
//  buffer5.append(localSipAddress);
//  buffer5.append("_");  
//  buffer5.append(remoteSipAddress);
//  buffer5.append("_"); 
//  buffer5.append(nowTs);
//  buffer5.append("_false_true_false");
//  
//  String signature = AnontionSecurityECDSA.sign(buffer5.toString());
//  
//  ResponsePostConnectionBodyDTO body = new ResponsePostConnectionBodyDTO(localSipAddress,
//      remoteSipAddress,
//      nowTs,
//      signature,
//      false,
//      true,
//      false);
//  
//  ResponsePostDTO response = new ResponsePostDTO(
//      new ResponseHeaderDTO(true, 0, "Ok."), body);
//  
//  _logger.info("DEBUG connection ok");
//  
//  return new ResponseEntity<>(response, HttpStatus.OK);
    
    return Responses.getBAD_REQUEST(
        "Invalid parameters!"); 
  }

  public ResponseEntity<ResponseDTO> postIndirectConnection() {
    
    _logger.info("NYI");
    
    return Responses.getNYI();
  }

  public ResponseEntity<ResponseDTO> postMultipleConnection() {

    _logger.info("NYI");
    
    return Responses.getNYI();
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionService.class.getName());
}
