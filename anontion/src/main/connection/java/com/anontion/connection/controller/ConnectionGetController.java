package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;

@RestController
public class ConnectionGetController {
  
  @GetMapping("/connection/")
  public ResponseEntity<ResponseDTO> getConnection() {

    _logger.severe("NYI");

    return Responses.getNYI();    
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionGetController.class.getName());
}

