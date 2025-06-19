package com.anontion.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
//import jakarta.transaction.Transactional;

import com.anontion.account.model.AnontionAccount;
import com.anontion.asterisk.model.AsteriskAor;
import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;

import com.anontion.account.repository.AnontionAccountRepository;
import com.anontion.asterisk.repository.AsteriskAorRepository;
import com.anontion.asterisk.repository.AsteriskAuthRepository;
import com.anontion.asterisk.repository.AsteriskEndpointRepository;

@Service
public class AccountServices implements AccountServicesI {

  @Autowired
  private AnontionAccountRepository accountRepository;
  
  @Autowired
  private AsteriskEndpointRepository endpointRepository;

  @Autowired
  private AsteriskAorRepository aorRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;
  
  public AccountServices() {
  }

  @Transactional("transactionManager") 
  public AnontionAccount createAccountAndEndpoint(AnontionAccount account, AsteriskEndpoint endpoint, AsteriskAuth auth, AsteriskAor aor) {

    endpointRepository.save(endpoint);    

    aorRepository.save(aor);
    
    authRepository.save(auth);
    
    return accountRepository.save(account);
  }
}
