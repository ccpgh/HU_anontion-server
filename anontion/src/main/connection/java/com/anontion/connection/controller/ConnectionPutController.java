package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;

@RestController
public class ConnectionPutController {
  
  @PutMapping("/connection/")
  public ResponseEntity<ResponseDTO> putConnection() {

    return Responses.getNYI();    
  }
}

