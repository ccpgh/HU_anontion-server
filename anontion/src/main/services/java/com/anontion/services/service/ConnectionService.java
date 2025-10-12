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
import com.anontion.models.account.repository.AnontionAccountRepository;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;
import com.anontion.models.image.model.AnontionImage;
import com.anontion.models.image.repository.AnontionImageRepository;
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
  private AnontionAccountRepository accountRepository;

  @Autowired
  private AnontionImageRepository imageRepository;

  @Autowired
  private AsteriskEndpointBean endpointBean;

  @Autowired
  private AsteriskAorBean aorBean;

  @Autowired
  private AsteriskAuthBean authBean;
  
  @Autowired
  private AccountService accountService;
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionIndirectOrRollBase(Long sipTsA, String sipEndpointA, String sipSignatureA, String sipLabelA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, String sipLabelB, AtomicBoolean isRetry, StringBuilder buffer, String rollSipAddress, String connectionType) {
    
    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB, connectionType);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      AnontionConnection connection = new AnontionConnection(null, sipEndpointA, sipSignatureA, sipLabelA, null, sipEndpointB, sipSignatureA, sipLabelB, connectionType,
          (rollSipAddress != null && !rollSipAddress.isEmpty() ? rollSipAddress : null));
      
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
    
    AnontionConnection connection = connection0.get();

    if (connection.getSipEndpointA().equals(sipEndpointA) && connection.getSipEndpointB().equals(sipEndpointB) && sipEndpointA.equals(sipEndpointB)) {

      Optional<AnontionConnection> connection1 = connectionRepository.findIndirectBySipEndpointAAndSipSignatureARelaxed(sipEndpointA, sipSignatureA, connectionType);
      
      if (connection1.isEmpty()) {
        
        connection1 = connectionRepository.findIndirectBySipEndpointBAndSipSignatureBRelaxed(sipEndpointA, sipSignatureA, connectionType);

        if (!connection1.isEmpty()) {

          buffer.append(connection1.get().getSipEndpointA());
        }
        
      } else {
      
        buffer.append(connection1.get().getSipEndpointB());
      }
      
      if (connection1.isEmpty()) {

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

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
      
      isRetry.set(true);

      return true;      
    }

    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

    isRetry.set(false);

    return false;
  }
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionDirect(Long sipTsA, String sipEndpointA, String sipSignatureA, Long sipTsB, 
      String sipEndpointB, String sipSignatureB, AtomicBoolean isRetry) {

    Optional<AnontionConnection> connection0 = null;
    
    try {

      connection0 = connectionRepository.findDirectBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      AnontionConnection connection = new AnontionConnection(null, sipEndpointA, null, "", null, sipEndpointB, null, "", "direct", null);
      
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

    AnontionConnection connection = connection0.get();
    
    if (connection.getSipTsA() != null && connection.getSipSignatureA() != null &&
        connection.getSipTsB() != null && connection.getSipSignatureB() != null) { 

      isRetry.set(false);
      
      return true;
    }

    if (sipTsA != null && sipSignatureA != null) {
      
      if (connection.getSipTsA() == null) {
       
        connection.setSipTsA(sipTsA);
      }
      
      if (connection.getSipSignatureA() == null) {

        connection.setSipSignatureA(sipSignatureA);
      }
      
      try {
      
        connectionRepository.save(connection);
        
      } catch (Exception e) {

        _logger.exception(e);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      
      isRetry.set(true);
      
      return false;      
    }

    if (sipTsB != null && sipSignatureB != null) {

      if (connection.getSipTsB() == null) {

        connection.setSipTsB(sipTsB);
      }
      
      if (connection.getSipSignatureB() == null) {
        
        connection.setSipSignatureB(sipSignatureB);
      }
      
      try {
        
        connectionRepository.save(connection);
        
      } catch (Exception e) {

        _logger.exception(e);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      
      isRetry.set(true);
      
      return false;      
    }

    // NOTE: as the saying goes - should not get here!
    
    isRetry.set(false);
    
    return false;
  }
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionIndirectOrRollUpdate(Long sipTsA, String sipEndpointA, String sipSignatureA, String sipLabelA, Long sipTsB, String sipEndpointB, 
      String sipSignatureB, String sipLabelB, AtomicBoolean isRetry, String localSipAddress, String remoteSipAddress, String connectionType) {
        
    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(remoteSipAddress, remoteSipAddress, connectionType);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      Optional<AnontionConnection> connection1 = null;

      try {
        
        if (localSipAddress.compareTo(remoteSipAddress) < 0) {

          connection1 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(localSipAddress, remoteSipAddress, connectionType);
        
        } else {
                    
          connection1 = connectionRepository.findIndirectBySipEndpointAAndSipEndpointBRelaxed(remoteSipAddress, localSipAddress, connectionType);
        }

        if (connection1.isEmpty()) {

          TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

          isRetry.set(true);
        
          return false;
        }
        
        isRetry.set(false);
        
        return true;

      } catch (Exception e) {
      
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        _logger.exception(e);

        isRetry.set(false);
        
        return false;
      }
    }

    AnontionConnection connection = connection0.get();
    
    AnontionConnection newConnection = null;

    if (sipEndpointA != null) {
      
      newConnection = new AnontionConnection(sipTsA, sipEndpointA, sipSignatureA, sipLabelA, sipTsA, connection.getSipEndpointB(), connection.getSipSignatureB(), connection.getSipLabelB(), connectionType, null);

    } else if (sipEndpointB != null) {
            
      newConnection = new AnontionConnection(sipTsB, connection.getSipEndpointA(), connection.getSipSignatureA(), connection.getSipLabelA(), sipTsB, sipEndpointB, sipSignatureB, sipLabelB, connectionType, null);

    } else {
      
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

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
  public boolean saveTxConnectionMultipleOrBroadcastBase(Long sipTsA, String sipEndpointA, String sipSignatureA, String sipLabelA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, String sipLabelB, AtomicBoolean isRetry, String connectionType, 
      Double latitude, Double longitude, Long timeoutTs) {
    
    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB, connectionType);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      AnontionConnection connection = null;
      
      if (connectionType.equals("multiple")) {
        
        connection = new AnontionConnection(sipTsA, sipEndpointA, sipSignatureA, sipLabelA, sipTsA, sipEndpointB, 
            sipSignatureA, sipLabelB, connectionType, null);
      
      } else if (connectionType.equals("broadcast")) {
        
        connection = new AnontionConnection(sipTsA, sipEndpointA, sipSignatureA, sipLabelA, sipTsA, sipEndpointB, 
            sipSignatureA, sipLabelB, connectionType, null, latitude, longitude, timeoutTs);
      
      } else {
        
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
        
    try {
    
      AnontionConnection connection = connection0.get();
      
      connection.setLongitude(longitude);
      
      connection.setLatitude(latitude);

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
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnectionMultipleUpdate(Long sipTsA, String sipEndpointA, String sipSignatureA, String sipLabelA,
      Long sipTsB, String sipEndpointB, String sipSignatureB, String sipLabelB, AtomicBoolean isRetry, String localSipAddress, 
      String remoteSipAddress, String connectionType) {

    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findBySipEndpointAAndSipEndpointBRelaxed(remoteSipAddress, remoteSipAddress, connectionType);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (connection0.isEmpty()) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(true);

      return false;
    }

    Optional<AnontionConnection> connection1 = null;

    try {

      connection1 = connectionRepository.findBySipEndpointAAndSipEndpointBRelaxed(sipEndpointA, sipEndpointB, connectionType);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (!connection1.isEmpty()) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(false);

      return true;
    }

    AnontionConnection connection = connection0.get();
    
    AnontionConnection newConnection = null;

    if (sipSignatureA == null) {
      
      newConnection = new AnontionConnection(sipTsA, sipEndpointA, connection.getSipSignatureA(), connection.getSipLabelA(), sipTsB, sipEndpointB, sipSignatureB, sipLabelB, "multiple", null);

    } else if (sipSignatureB == null) {
            
      newConnection = new AnontionConnection(sipTsA, sipEndpointA, sipSignatureA, sipLabelA, sipTsB, sipEndpointB, connection.getSipSignatureB(), connection.getSipLabelB(), "multiple", null);

    } else {
      
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

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
      StringBuilder returnSipUserId, StringBuilder returnSipLabel, StringBuilder returnPhoto, Long clientTs, String clientName, UUID clientId, String clientUID) {

    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findBySipEndpointAAndSipEndpointB(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (connection0.isEmpty()) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(true);

      return false;
    }
    
    AnontionConnection connection = connection0.get();
    
    String password = AnontionSecurity.generatePassword();
    
    String foreignKey1 = AnontionSecurity.generateForeignKey();
    
    String foreignKey2 = AnontionSecurity.generateForeignKey();
    
    String md5Passsword = AnontionStrings.generateAsteriskMD5(foreignKey2, AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM, password);
    
    if (sipEndpointA.equals(localSipAddress)) {
      
      if (!connection.getSipPasswordA().isEmpty()) {
        
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        returnPassword.append(connection.getSipPasswordA());
        
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
      
      returnSipLabel.append(connection.getSipLabelB());
      
      Optional<AnontionImage> image0 = imageRepository.findById(remoteSipAddress);
      
      if (!image0.isEmpty()) {
        
        AnontionImage image = image0.get();
        
        if (!image.getBase64EncodedImage().isEmpty()) {
          
          returnPhoto.append(image.getBase64EncodedImage());
        }
      }

      connection.setSipPasswordA(passwordA);
      
    } else if (sipEndpointB.equals(localSipAddress)) {

      if (!connection.getSipPasswordB().isEmpty()) {
        
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        returnPassword.append(connection.getSipPasswordB());
        
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

      returnSipLabel.append(connection.getSipLabelA());
      
      Optional<AnontionImage> image0 = imageRepository.findById(remoteSipAddress);
      
      if (!image0.isEmpty()) {
        
        AnontionImage image = image0.get();
        
        if (!image.getBase64EncodedImage().isEmpty()) {
          
          returnPhoto.append(image.getBase64EncodedImage());
        }
      }

      connection.setSipPasswordB(passwordB);
    }
    
    AnontionAccount account = new AnontionAccount(
        clientTs, // NYI primary key issues 
        localSipAddress,
        clientId,
        "", // NYI do later 
        "", // NYI do later ! 
        AnontionSecurity.decodeFromSafeBase64(localSipAddress), 
        localSipAddress, // clientUID // NYI use ? needed?
        AnontionAccount.DEFAULT_defaultExpiration,
        AnontionAccount.DEFAULT_minimumExpiration,
        AnontionAccount.DEFAULT_maximumExpiration,
        false, "anonymous");
    
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
  public boolean createTxAccountIfConnected(String sipEndpoint, AtomicBoolean isRetry, String localSipAddress, String remoteSipAddress, StringBuilder returnPassword, 
      StringBuilder returnSipUserId, StringBuilder returnSipLabel, StringBuilder returnPhoto, Long clientTs, String clientName, UUID clientId, String clientUID,
      StringBuilder returnTimeoutTs, String connectionType) {

    if (connectionType.equals("broadcast")) {
      
      // NOTE: if account exists exit ... a horrible hack !!! 
      
      Optional<AnontionAccount> account0 = accountRepository.findByClientTsAndClientNameAndClientId(clientTs, localSipAddress, clientId);
      
      if (!account0.isEmpty()) {

        returnPassword.append("blah");

        returnSipUserId.append("blah");

        returnSipLabel.append("blah");
        
        returnTimeoutTs.append("0");
        
        isRetry.set(false);
        
        return true;        
      }
    }
    
    Optional<AnontionConnection> connection0 = null;

    try {

      connection0 = connectionRepository.findBySipEndpointAAndSipEndpointBIdentical(sipEndpoint);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }   
    
    if (connection0.isEmpty()) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(true);

      return false;
    }
    
    AnontionConnection connection = connection0.get();
    
    String password = AnontionSecurity.generatePassword();
    
    String foreignKey1 = AnontionSecurity.generateForeignKey();
    
    String foreignKey2 = AnontionSecurity.generateForeignKey();
    
    String md5Passsword = AnontionStrings.generateAsteriskMD5(foreignKey2, AnontionConfig._ASTERISK_PASSWORD_ENCODING_REALM, password);
    
    if (!connection.getSipPasswordA().isEmpty() &&
        !connection.getSipPasswordA().equals(connection.getSipPasswordB())) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      isRetry.set(false);

      return false;
    }
    
    if (!connection.getSipPasswordA().isEmpty()) {

      returnPassword.append(connection.getSipPasswordA());

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

    returnSipLabel.append(connection.getSipLabelA());
    
    returnTimeoutTs.append(connection.getTimeoutTs().toString());
    
    Optional<AnontionImage> image0 = imageRepository.findById(remoteSipAddress);
    
    if (!image0.isEmpty()) {
      
      AnontionImage image = image0.get();
      
      if (!image.getBase64EncodedImage().isEmpty()) {
        
        returnPhoto.append(image.getBase64EncodedImage());
      }
    }

    connection.setSipPasswordA(passwordA);

    connection.setSipPasswordB(passwordA); 

    AnontionAccount account = new AnontionAccount(
        clientTs, // NYI primary key issues 
        localSipAddress,
        clientId,
        "", // NYI do later 
        "", // NYI do later ! 
        AnontionSecurity.decodeFromSafeBase64(localSipAddress), 
        localSipAddress, // clientUID // NYI use ? needed?
        AnontionAccount.DEFAULT_defaultExpiration,
        AnontionAccount.DEFAULT_minimumExpiration,
        AnontionAccount.DEFAULT_maximumExpiration,
        false, "anonymous");
    
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


