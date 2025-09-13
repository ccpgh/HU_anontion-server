package com.anontion.services.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.anontion.asterisk.bean.AsteriskAorBean;
import com.anontion.asterisk.bean.AsteriskAuthBean;
import com.anontion.asterisk.bean.AsteriskEndpointBean;
import com.anontion.asterisk.model.AsteriskAor;
import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionSecurityECIES_ECDH;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;
import com.anontion.services.service.ConnectionService;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ConnectionService {
   
  @Autowired
  private AnontionConnectionRepository connectionRepository;
    
  @Autowired
  private AsteriskEndpointBean endpointBean;

  @Autowired
  private AsteriskAorBean aorBean;

  @Autowired
  private AsteriskAuthBean authBean;
  
  @Autowired
  private AccountService accountService;
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionIndirectBase(Long sipTsA, String sipEndpointA, String sipSignatureA, 
      Long sipTsB, String sipEndpointB, String sipSignatureB, AtomicBoolean isRetry, StringBuilder buffer) {
    
    _logger.info("DEBUG saveTxConnectionIndirectBase called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive());
    
    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG saveTxConnectionIndirectBase exception " + e.toString() + " when trying to find connection.");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG saveTxConnectionIndirectBase empty");

      AnontionConnection connection = new AnontionConnection(null, sipEndpointA, sipSignatureA, null, sipEndpointB, sipSignatureA, "indirect");
      
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
    
    _logger.info("DEBUG saveTxConnectionIndirectBase found connection");

    AnontionConnection connection = connection0.get();

    if (connection.getSipEndpointA().equals(sipEndpointA) && connection.getSipEndpointB().equals(sipEndpointB) && sipEndpointA.equals(sipEndpointB)) {

      Optional<AnontionConnection> connection1 = connectionRepository.findIndirectBySipEndpointAAndSipSignatureARelaxed(sipEndpointA, sipSignatureA);
      
      if (connection1.isEmpty()) {
        
        connection1 = connectionRepository.findIndirectBySipEndpointBAndSipSignatureBRelaxed(sipEndpointA, sipSignatureA);

        if (!connection1.isEmpty()) {

          buffer.append(connection1.get().getSipEndpointA());
        }
        
      } else {
      
        buffer.append(connection1.get().getSipEndpointB());
      }
      
      if (connection1.isEmpty()) {

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        _logger.info("DEBUG saveTxConnectionIndirectBase match on base connection but did not find corresponding update connection");

        isRetry.set(true);

        return false;
      }
      
      try {
      
        connectionRepository.delete(connection);
      
      } catch (Exception e) {

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        _logger.exception(e);

        isRetry.set(false);
        
        return false;
      }
      
      _logger.info("DEBUG saveTxConnectionIndirectBase got corresponding remote '" + buffer.toString() + "'");
      
      isRetry.set(true);

      return true;      
    }

    _logger.info("DEBUG saveTxConnectionIndirectBase not match on state 1");
    
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

    isRetry.set(false);

    return false;
  }
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionDirect(Long sipTsA, String sipEndpointA, String sipSignatureA, Long sipTsB, 
      String sipEndpointB, String sipSignatureB, AtomicBoolean isRetry) {

    _logger.info("DEBUG saveTxConnectionDirect called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive());
    
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
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionIndirectUpdate(Long sipTsA, String sipEndpointA, String sipSignatureA, Long sipTsB, String sipEndpointB, 
      String sipSignatureB, AtomicBoolean isRetry, String localSipAddress, String remoteSipAddress) {
    
    _logger.info("DEBUG saveTxConnectionIndirectUpdate called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive());
    
    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(remoteSipAddress, remoteSipAddress);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG saveTxConnectionIndirectUpdate exception " + e.toString() + " when trying to find connection.");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG saveTxConnectionIndirectUpdate empty");
      
      Optional<AnontionConnection> connection1 = null;

      try {
        
        if (localSipAddress.compareTo(remoteSipAddress) < 0) {

          connection1 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(localSipAddress, remoteSipAddress);
        
        } else {
                    
          connection1 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(remoteSipAddress, localSipAddress);
        }

        if (connection1.isEmpty()) {

          _logger.info("DEBUG saveTxConnectionIndirectUpdate not matched on stage 2 indirect connection");

          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

          isRetry.set(true);
        
          return false;
        }
        
        _logger.info("DEBUG saveTxConnectionIndirectUpdate matched on stage 2 indirect connection");
        
        isRetry.set(false);
        
        return true;

      } catch (Exception e) {
      
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        _logger.info("DEBUG saveTxConnectionIndirectUpdate exception " + e.toString() + " when trying to find indirect unequal connection.");
        
        _logger.exception(e);

        isRetry.set(false);
        
        return false;
      }
    }

    AnontionConnection connection = connection0.get();
    
    AnontionConnection newConnection = null;

    if (sipEndpointA != null) {
      
      newConnection = new AnontionConnection(sipTsA, sipEndpointA, sipSignatureA, sipTsA, connection.getSipEndpointB(), connection.getSipSignatureB(), "indirect");

    } else if (sipEndpointB != null) {
            
      newConnection = new AnontionConnection(sipTsB, connection.getSipEndpointA(), connection.getSipSignatureA(), sipTsB, sipEndpointB, sipSignatureB, "indirect");

    } else {
      
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG either setSipEndpointA set or setSipEndpointB set must be set");

      isRetry.set(false);
      
      return false;
    }

    try {

      newConnection = connectionRepository.save(newConnection);

      isRetry.set(true);
      
      return true;

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
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionMultipleBase(Long sipTsA, String sipEndpointA, String sipSignatureA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, AtomicBoolean isRetry) {
    
    _logger.info("DEBUG saveTxConnectionMultipleBase called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive() + " with sipEndpointA " + sipEndpointA + " sipEndpointB " + sipEndpointB);
    
    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findMultipleBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG saveTxConnectionMultipleBase exception " + e.toString() + " when trying to find connection.");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG saveTxConnectionMultipleBase empty");

      AnontionConnection connection = new AnontionConnection(sipTsA, sipEndpointA, sipSignatureA, sipTsA, sipEndpointB, sipSignatureA, "multiple");
      
      try {
        
        connection = connectionRepository.save(connection);

        isRetry.set(false);
        
        return true;

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
    
    _logger.info("DEBUG saveTxConnectionMultipleBase matched");

    isRetry.set(false);
    
    return true;
  }
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionMultipleUpdate(Long sipTsA, String sipEndpointA, String sipSignatureA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, AtomicBoolean isRetry, String localSipAddress, String remoteSipAddress) {

    _logger.info("DEBUG saveTxConnectionMultipleUpdate called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive() + " with sipEndpointA " + sipEndpointA + " sipEndpointB " + sipEndpointB);

    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findMultipleBySipEndpointAAndSipEndpointBRelaxed(remoteSipAddress, remoteSipAddress);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG saveTxConnectionMultipleUpdate exception " + e.toString() + " when trying to find remote multiple connection '" + remoteSipAddress + "'");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG saveTxConnectionMultipleUpdate empty");
      
      Optional<AnontionConnection> connection1 = null;
      
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(true);

      return false;
    }

    Optional<AnontionConnection> connection1 = null;

    try {

      connection1 = connectionRepository.findMultipleBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG saveTxConnectionMultipleUpdate exception " + e.toString() + " when trying to find existing multiple connection '" + sipEndpointA + " and " + sipEndpointB + "'");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (!connection1.isEmpty()) {

      _logger.info("DEBUG saveTxConnectionMultipleUpdate connection already exists.");
      
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(false);

      return true;
    }

    AnontionConnection connection = connection0.get();
    
    AnontionConnection newConnection = null;

    if (sipSignatureA == null) {
      
      newConnection = new AnontionConnection(sipTsA, sipEndpointA, connection.getSipSignatureA(), sipTsB, sipEndpointB, sipSignatureB, "multiple");

    } else if (sipSignatureB == null) {
            
      newConnection = new AnontionConnection(sipTsA, sipEndpointA, sipSignatureA, sipTsB, sipEndpointB, connection.getSipSignatureB(), "multiple");

    } else {
      
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG saveTxConnectionMultipleUpdate either sipSignatureA set or sipSignatureB set must be null");

      isRetry.set(false);
      
      return false;
    }

    try {

      newConnection = connectionRepository.save(newConnection);

      isRetry.set(false);
      
      return true;

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
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean createTxAccountIfConnected(String sipEndpointA, String sipEndpointB, AtomicBoolean isRetry, String localSipAddress, String remoteSipAddress, StringBuilder returnPassword, 
      StringBuilder returnSipUserId, Long clientTs, String clientName, UUID clientId, String clientUID) {

    _logger.info("DEBUG createTxAccountIfConnected called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive() + " with sipEndpointA " + sipEndpointA + " sipEndpointB " + sipEndpointB);

    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findBySipEndpointAAndSipEndpointB(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG createTxAccountIfConnected exception " + e.toString() + " when trying to find existing connection '" + sipEndpointA + " and " + sipEndpointB + "'");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG createTxAccountIfConnected no connection exists");

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(true);

      return false;
    }
    
    AnontionConnection connection = connection0.get();
    
    String password = AnontionSecurity.generatePassword();
    
    password = "password"; //TODO remove
    
    String foreignKey1 = AnontionSecurity.generateForeignKey();
    
    String foreignKey2 = AnontionSecurity.generateForeignKey();
    
    String md5Passsword = AnontionStrings.generateAsteriskMD5(foreignKey2, AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM, password);
    
    _logger.info("DEBUG createTxAccountIfConnected updating with foreignKey1 '" + foreignKey1 + "' foreignKey2 '" + foreignKey2 + "' md5Passsword '" + md5Passsword + "'");

    if (sipEndpointA.equals(localSipAddress)) {
      
      _logger.info("DEBUG createTxAccountIfConnected updating A with endpoint '" + localSipAddress + "'");

      if (!connection.getSipPasswordA().isEmpty()) {
        
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        returnPassword.append(connection.getSipPasswordA());
        
        _logger.info("DEBUG createTxAccountIfConnected set connection foreignKey2 " + foreignKey2);

        isRetry.set(false);

        return true;
      }
      
      String unsafeSipEndpointA = AnontionSecurity.decodeFromSafeBase64(sipEndpointA);
          
      ECPublicKeyParameters pubA = null;
      
      try {
        
        pubA = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(unsafeSipEndpointA);
      
      } catch (Exception e) {
        
        _logger.exception(e);

        isRetry.set(false);

        return false;
      }
      
      String passwordA = AnontionSecurityECIES_ECDH.encrypt(pubA, password);

      returnPassword.append(passwordA);
      
      returnSipUserId.append(foreignKey2);

      _logger.info("DEBUG createTxAccountIfConnected set NOT connection foreignKey2 " + foreignKey2);

      connection.setSipPasswordA(passwordA);
      
    } else if (sipEndpointB.equals(localSipAddress)) {

      _logger.info("DEBUG createTxAccountIfConnected updating B with endpoint '" + localSipAddress + "'");
      
      if (!connection.getSipPasswordB().isEmpty()) {
        
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        returnPassword.append(connection.getSipPasswordB());
        
        _logger.info("DEBUG createTxAccountIfConnected set connection foreignKey2 " + foreignKey2);

        isRetry.set(false);

        return true;
      }
      
      String unsafeSipEndpointB = AnontionSecurity.decodeFromSafeBase64(sipEndpointB);

      ECPublicKeyParameters pubB = null;
      
      try {
       
        pubB = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(unsafeSipEndpointB); 
      
      } catch (Exception e) {
        
        _logger.exception(e);

        isRetry.set(false);

        return false;
      }

      String passwordB = AnontionSecurityECIES_ECDH.encrypt(pubB, password);

      returnPassword.append(passwordB);

      returnSipUserId.append(foreignKey2);

      _logger.info("DEBUG createTxAccountIfConnected set NOT connection foreignKey2 " + foreignKey2);

      connection.setSipPasswordB(passwordB);
    }
    
    AnontionAccount account = new AnontionAccount(
        clientTs, // NYI primary key issues 
        localSipAddress,
        clientId,
        "", // NYI do later 
        "", // NYI do later ! 
        localSipAddress, 
        localSipAddress, // clientUID // NYI use ? needed?
        AnontionAccount.DEFAULT_defaultExpiration,
        AnontionAccount.DEFAULT_minimumExpiration,
        AnontionAccount.DEFAULT_maximumExpiration,
        false);
    
    AsteriskEndpoint endpoint = endpointBean.createAsteriskEndppint(
        AnontionSecurity.makeSafeIfRequired(localSipAddress),
        foreignKey1);

    AsteriskAor aor = aorBean.createAsteriskAor(
        AnontionSecurity.makeSafeIfRequired(localSipAddress));
    
    AsteriskAuth auth = authBean.createAsteriskAuth(
        foreignKey1,
        foreignKey2, //account.getClientName(),
        md5Passsword,
        AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM,
        AnontionConfig._ASTERISK_PASSWORD_ENCODING_AUTHTYPE);
    
    if (!accountService.saveTxAccountAndEndpoint(
        null,
        account, 
        endpoint, 
        auth, 
        aor)) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(false);

      return false;
    }
    
    try {

      connection = connectionRepository.save(connection);

      isRetry.set(false);
      
      return true;

    } catch (DataIntegrityViolationException e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    } 
  }
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean createTxAccountIfConnected(String sipEndpoint, AtomicBoolean isRetry, String localSipAddress, StringBuilder returnPassword, 
      StringBuilder returnSipUserId, Long clientTs, String clientName, UUID clientId, String clientUID) {

    _logger.info("DEBUG createTxAccountIfConnected called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive() + " with sipEndpoint '" + sipEndpoint + "'");

    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findBySipEndpointAAndSipEndpointBIdentical(sipEndpoint);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG createTxAccountIfConnected exception " + e.toString() + " when trying to find existing connection '" + sipEndpoint + "'");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG createTxAccountIfConnected no connection exists");

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(true);

      return false;
    }
    
    AnontionConnection connection = connection0.get();
    
    String password = AnontionSecurity.generatePassword();
    
    password = "password"; //TODO remove

    String foreignKey1 = AnontionSecurity.generateForeignKey();
    
    String foreignKey2 = AnontionSecurity.generateForeignKey();
    
    String md5Passsword = AnontionStrings.generateAsteriskMD5(foreignKey2, AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM, password);
    
    _logger.info("DEBUG createTxAccountIfConnected updating with foreignKey1 '" + foreignKey1 + "' foreignKey2 '" + foreignKey2 + "' md5Passsword '" + md5Passsword + "'");

    _logger.info("DEBUG createTxAccountIfConnected updating with endpoint '" + localSipAddress + "'");

    if (!connection.getSipPasswordA().isEmpty() && 
        !connection.getSipPasswordA().equals(connection.getSipPasswordB())) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(false);

      return false;
    }
    
    if (!connection.getSipPasswordA().isEmpty()) {

      returnPassword.append(connection.getSipPasswordA());

      _logger.info("DEBUG createTxAccountIfConnected set connection foreignKey2 " + foreignKey2);

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(false);

      return true;
    }

    String unsafeSipEndpoint = AnontionSecurity.decodeFromSafeBase64(sipEndpoint);

    ECPublicKeyParameters pub = null;

    try {

      pub = AnontionSecurityECDSA.decodeECPublicKeyParametersFromBase64XY(unsafeSipEndpoint);

    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);

      return false;
    }

    String passwordA = AnontionSecurityECIES_ECDH.encrypt(pub, password);

    returnPassword.append(passwordA);

    returnSipUserId.append(foreignKey2);

    _logger.info("DEBUG createTxAccountIfConnected set NOT connection foreignKey2 " + foreignKey2);

    connection.setSipPasswordA(passwordA); 

    connection.setSipPasswordB(passwordA); 

    AnontionAccount account = new AnontionAccount(
        clientTs, // NYI primary key issues 
        localSipAddress,
        clientId,
        "", // NYI do later 
        "", // NYI do later ! 
        localSipAddress, 
        localSipAddress, // clientUID // NYI use ? needed?
        AnontionAccount.DEFAULT_defaultExpiration,
        AnontionAccount.DEFAULT_minimumExpiration,
        AnontionAccount.DEFAULT_maximumExpiration,
        false);
    
    AsteriskEndpoint endpoint = endpointBean.createAsteriskEndppint(
        AnontionSecurity.makeSafeIfRequired(localSipAddress),
        foreignKey1);

    AsteriskAor aor = aorBean.createAsteriskAor(
        AnontionSecurity.makeSafeIfRequired(localSipAddress));
    
    AsteriskAuth auth = authBean.createAsteriskAuth(
        foreignKey1,
        foreignKey2, //account.getClientName(),
        md5Passsword,
        AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM,
        AnontionConfig._ASTERISK_PASSWORD_ENCODING_AUTHTYPE);
    
    if (!accountService.saveTxAccountAndEndpoint(
        null,
        account, 
        endpoint, 
        auth, 
        aor)) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(false);

      return false;
    }
    
    try {

      connection = connectionRepository.save(connection);

      isRetry.set(false);
      
      return true;

    } catch (DataIntegrityViolationException e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    } 
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionService.class.getName());
}




