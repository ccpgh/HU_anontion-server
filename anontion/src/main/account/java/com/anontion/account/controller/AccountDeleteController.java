package com.anontion.account.controller;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;

@RestController
public class AccountDeleteController {
  
  @DeleteMapping("/account/*")
  public ResponseEntity<ResponseDTO> getAccount() {

    _logger.severe("NYI");

    return Responses.getNYI();    
  }
  
  
  final private static AnontionLog _logger = new AnontionLog(AccountDeleteController.class.getName());
}


