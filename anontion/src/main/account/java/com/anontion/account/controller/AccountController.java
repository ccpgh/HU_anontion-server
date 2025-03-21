package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;

import com.anontion.account.model.AnontionAccount;
import com.anontion.account.repository.AnontionAccountRepository;
import com.anontion.account.service.AccountBean;

import org.springframework.stereotype.Service;

import java.util.Optional;
//import java.util.UUID;

@RestController
public class AccountController {

  @Autowired
  private AnontionAccountRepository accountRepository;
  
  @GetMapping("/")
  public AnontionAccount getAccount() {

    Optional<AnontionAccount> accountOpt = accountRepository.findById(1); 

    if (accountOpt.isPresent()) {
    
      return accountOpt.get();
    
    } else {

      AnontionAccount newAccount = new AnontionAccount();
      
      newAccount.setUsername("cc");

      return accountRepository.save(newAccount);
    }
  }
  
  @PostMapping("/")
  public String postAccount() {

    return "{}";
  }

  @PutMapping("/")
  public String putAccount() {

    return "{}";
  }

  @DeleteMapping("/")
  public String deleteAccount() {

    return "{}";
  }
  
}

