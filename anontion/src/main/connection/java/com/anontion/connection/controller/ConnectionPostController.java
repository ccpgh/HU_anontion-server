package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;

@RestController
public class ConnectionPostController {
  
  @PostMapping(path = "/connection/")
  public ResponseEntity<ResponseDTO> postConnection() {

    _logger.severe("NYI");

    return Responses.getNYI();    
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionPostController.class.getName());
}

