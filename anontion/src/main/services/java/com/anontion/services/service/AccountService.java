package com.anontion.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.stereotype.Service;
//import jakarta.transaction.Transactional;

import com.anontion.asterisk.model.AsteriskAor;
import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.asterisk.repository.AsteriskAorRepository;
import com.anontion.asterisk.repository.AsteriskAuthRepository;
import com.anontion.asterisk.repository.AsteriskEndpointRepository;
import com.anontion.common.misc.AnontionLog;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.account.repository.AnontionAccountRepository;
import com.anontion.models.application.model.AnontionApplication;
import com.anontion.models.application.repository.AnontionApplicationRepository;

@Service
public class AccountService {

  @Autowired
  private AsteriskEndpointRepository endpointRepository;

  @Autowired
  private AsteriskAorRepository aorRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;
  
  @Autowired
  private AnontionAccountRepository accountRepository;

  @Autowired
  private AnontionApplicationRepository applicationRepository;

  public AccountService() {
  }

  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean saveTxAccountAndEndpoint(AnontionApplication application, AnontionAccount account, AsteriskEndpoint endpoint, AsteriskAuth auth, AsteriskAor aor) {

    try {
      
      _logger.info("DEBUG saveTxAccountAndEndpoint called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive());

      applicationRepository.delete(application);
      
      endpointRepository.save(endpoint);    

      aorRepository.save(aor);

      authRepository.save(auth);

      accountRepository.save(account);
    
      return true;
      
    } catch (Exception e) {

      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);
      
      return false;
    }
  }
  
  @Transactional(transactionManager = "transactionManagerService", rollbackFor = { Exception.class } )
  public boolean deleteTxApplicationAndAccountAndEndpoint(AnontionApplication application, AnontionAccount account, AsteriskEndpoint endpoint, AsteriskAuth auth, AsteriskAor aor) {
    
    // NYI - add delete of connections
    
    try {
      
      _logger.info("DEBUG deleteTxApplicationAndAccountAndEndpoint called is transaction active " + TransactionSynchronizationManager.isActualTransactionActive());

      if (account != null) {

        accountRepository.delete(account);
      }
      
      if (application != null) {

        applicationRepository.delete(application);
      }

      if (auth != null) {

        authRepository.delete(auth);
      }

      if (aor != null) {

        aorRepository.delete(aor);
      }

      if (endpoint != null) {

        endpointRepository.delete(endpoint);
      }

      return true;
      
    } catch (Exception e) {
      
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

      _logger.exception(e);
      
      return false;      
    }
  }
  
  final private static AnontionLog _logger = new AnontionLog(AccountService.class.getName());
}

