package com.anontion.application.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;

@RestController
public class ApplicationDeleteController {
    
  @DeleteMapping(path = "/application/{id}")
  public ResponseEntity<ResponseDTO> deleteApplication(@PathVariable String id) {

    _logger.severe("NYI " + id);
    
    return Responses.getNYI();    
  }
  
  final private static AnontionLog _logger = new AnontionLog(ApplicationDeleteController.class.getName());
}

