package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;

@RestController
public class ConnectionDeleteController {
  
  @DeleteMapping("/connection/")
  public ResponseEntity<ResponseDTO> deleteConnection() {

    _logger.severe("NYI");

    return Responses.getNYI();    
  }
  
  final private static AnontionLog _logger = new AnontionLog(ConnectionDeleteController.class.getName());
}

