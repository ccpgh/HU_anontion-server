package com.anontion.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
//import jakarta.transaction.Transactional;

import com.anontion.asterisk.model.AsteriskAor;
import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.asterisk.repository.AsteriskAorRepository;
import com.anontion.asterisk.repository.AsteriskAuthRepository;
import com.anontion.asterisk.repository.AsteriskEndpointRepository;
import com.anontion.models.account.model.AnontionAccount;
import com.anontion.models.account.repository.AnontionAccountRepository;

@Service
public class AscountService {

  @Autowired
  private AsteriskEndpointRepository endpointRepository;

  @Autowired
  private AsteriskAorRepository aorRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;
  
  @Autowired
  private AnontionAccountRepository accountRepository;

  public AscountService() {
  }

  @Transactional("transactionManagerService") 
  public AnontionAccount saveTxAccountAndEndpoint(AnontionAccount account,  AsteriskEndpoint endpoint, AsteriskAuth auth, AsteriskAor aor) {

    endpointRepository.save(endpoint);    

    aorRepository.save(aor);
    
    authRepository.save(auth);
    
    return accountRepository.save(account);
  }
}

