package com.anontion.account.controller;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;

@RestController
public class AccountPutController {
  
  @PutMapping("/account/")
  public ResponseEntity<ResponseDTO> putAccount() {

    return Responses.getNYI();    
  }
}

