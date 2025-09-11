package com.anontion.services.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.anontion.common.misc.AnontionLog;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;
import com.anontion.services.service.ConnectionService; 

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ConnectionService {
   
  @Autowired
  private AnontionConnectionRepository connectionRepository;
    
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
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionService.class.getName());
}


