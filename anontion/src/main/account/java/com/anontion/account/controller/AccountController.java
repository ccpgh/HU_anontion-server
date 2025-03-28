package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.request.RequestApplicationDTO;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

  //@Autowired
  //private AnontionAccountRepository accountRepository;
    
  @GetMapping("/")
  public ResponseEntity<ResponseDTO> getAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
  @PostMapping(path = "/")
  public ResponseEntity<ResponseDTO> postAccount(@Valid @RequestBody RequestApplicationDTO requestAccountDTO, BindingResult bindingResult) {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
      
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @PutMapping(path = "/")
  public ResponseEntity<ResponseDTO> putAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @DeleteMapping(path = "/")
  public ResponseEntity<ResponseDTO> deleteAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
}

