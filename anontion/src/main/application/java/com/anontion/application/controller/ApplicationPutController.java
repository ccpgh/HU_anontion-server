package com.anontion.application.controller;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;

@RestController
public class ApplicationPutController {

  @PutMapping(path = "/application/*")
  public ResponseEntity<ResponseDTO> putApplication() {

    return Responses.getNYI();    
  }
}
