package com.anontion.account.service;

import org.springframework.stereotype.Service;

import com.anontion.models.account.model.AnontionAccount;

@Service
public class AccountBean {

  public AnontionAccount getAnontionAccount() {

    return new AnontionAccount();
  }
}
