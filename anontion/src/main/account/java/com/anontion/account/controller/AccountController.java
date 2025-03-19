package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;

import com.anontion.account.model.AnontionAccount;
import com.anontion.account.service.AccountBean;

@RestController
public class AccountController {

  @Autowired
  private AccountBean accountBean;
  
  @GetMapping("/")
  public AnontionAccount getAccount() {

    AnontionAccount blah = accountBean.getAnontionAccount();
    
    System.out.println("OK " + blah);
    
    return blah;
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

