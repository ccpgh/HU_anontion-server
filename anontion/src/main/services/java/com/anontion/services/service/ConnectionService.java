package com.anontion.services.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.anontion.common.misc.AnontionLog;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ConnectionService {
   
  @Autowired
  private AnontionConnectionRepository connectionRepository;

  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxConnection(Long sipTsA, String sipEndpointA, String sipSignatureA, Long sipTsB, String sipEndpointB, String sipSignatureB, AtomicBoolean isRetry) {

    _logger.info("DEBUG saveTxConnection called");
    
    Optional<AnontionConnection> connection0 = null;
    
    try {

      connection0 = connectionRepository.findBySipEndpointAAndSipEndpointB(sipEndpointA, sipEndpointB);
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.info("DEBUG exception " + e.toString() + " when trying to find for update.");
      
      _logger.exception(e);

      isRetry.set(false);
      
      return false;
    }
    
    if (connection0.isEmpty()) {

      _logger.info("DEBUG saveTxConnection empty");

      AnontionConnection connection = new AnontionConnection(null, sipEndpointA, null, null, sipEndpointB, null);
      
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

    _logger.info("DEBUG saveTxConnection not empty");

    AnontionConnection connection = connection0.get();
    
    if (connection.getSipTsA() != null && connection.getSipSignatureA() != null &&
        connection.getSipTsB() != null && connection.getSipSignatureB() != null) { 

      _logger.info("DEBUG saveTxConnection all populated");

      isRetry.set(false);
      
      return true;
    }

    // else check which side i am and update 
    
    if (sipTsA != null && sipSignatureA != null) {
      
      _logger.info("DEBUG saveTxConnection A update");

      if (connection.getSipTsA() == null) {
       
        _logger.info("DEBUG saveTxConnection A update 1");

        connection.setSipTsA(sipTsA);
      }
      
      if (connection.getSipSignatureA() == null) {

        _logger.info("DEBUG saveTxConnection A update 2");

        connection.setSipSignatureA(sipSignatureA);
      }
      
      try {
      
        _logger.info("DEBUG saveTxConnection A update save");

        connectionRepository.save(connection);
        
      } catch (Exception e) {

        _logger.exception(e);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      
      isRetry.set(true);
      
      return false;      
    }

    if (sipTsB != null && sipSignatureB != null) {

      _logger.info("DEBUG saveTxConnection B update");

      if (connection.getSipTsB() == null) {

        _logger.info("DEBUG saveTxConnection B update 1");

        connection.setSipTsB(sipTsB);
      }
      
      if (connection.getSipSignatureB() == null) {
        
        _logger.info("DEBUG saveTxConnection B update 2");
        
        connection.setSipSignatureB(sipSignatureB);
      }
      
      try {
        
        _logger.info("DEBUG saveTxConnection B update save");

        connectionRepository.save(connection);
        
      } catch (Exception e) {

        _logger.exception(e);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      }
      
      isRetry.set(true);
      
      return false;      
    }

    // NOTE: as the saying goes - should not get here!
    
    _logger.info("DEBUG saveTxConnection hit bottom");
    
    isRetry.set(false);
    
    return false;
  }

  final private static AnontionLog _logger = new AnontionLog(ConnectionService.class.getName());
}
