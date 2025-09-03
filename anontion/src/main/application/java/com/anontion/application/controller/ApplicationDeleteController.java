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
    
  @DeleteMapping(path = "/application/")
  public ResponseEntity<ResponseDTO> deleteApplication() {

    return Responses.getNYI();    
  }
}

