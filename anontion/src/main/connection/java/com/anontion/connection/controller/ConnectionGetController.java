package com.anontion.connection.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.security.AnontionSecurity;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;
import com.anontion.services.service.ConnectionService;

@RestController
public class ConnectionGetController {
    
  @Autowired
  private AnontionConnectionRepository connectionRepository;
  
  @GetMapping(path = "/connection/exists")
  public ResponseEntity<ResponseDTO> isExists(@RequestParam("sipUsername1") String sipUsername1, @RequestParam("sipUsername2") String sipUsername2) {

    _logger.info("Called isExists '" + sipUsername1 + "', '" + sipUsername2 + "'");

    if (sipUsername1.isBlank() ||
        sipUsername2.isBlank()) {

      _logger.info("failed param check blank");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }

    String convertedSipUsername1 = AnontionSecurity.fromSipAddressToSipname(sipUsername1);
    
    String convertedSipUsername2 = AnontionSecurity.fromSipAddressToSipname(sipUsername2);

    if (convertedSipUsername1.isBlank() ||
        convertedSipUsername2.isBlank()) {

      _logger.info("failed param check converted blank");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }

    if ((!AnontionSecurity.isBase64(convertedSipUsername1) && !AnontionSecurity.isSafeBase64(convertedSipUsername1)) ||
        (!AnontionSecurity.isBase64(convertedSipUsername2) && !AnontionSecurity.isSafeBase64(convertedSipUsername2))) {

      _logger.info("failed param check type");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }

    String sipEndpointA = AnontionSecurity.encodeToSafeBase64(convertedSipUsername1);

    String sipEndpointB = AnontionSecurity.encodeToSafeBase64(convertedSipUsername2);

    if (!AnontionSecurity.isSafeBase64(sipEndpointA) ||
        !AnontionSecurity.isSafeBase64(sipEndpointB)) {

      _logger.info("failed param check converted");

      return Responses.getBAD_REQUEST(
          "Failed param check");
    }
    
    Optional<AnontionConnection> connection0 = 
        (sipEndpointA.compareTo(sipUsername2) < 0) ? connectionRepository.findBySipEndpointAAndSipEndpointB(sipEndpointA, sipEndpointB)
            : connectionRepository.findBySipEndpointAAndSipEndpointB(sipEndpointB, sipEndpointA);

    if (connection0.isEmpty()) {

      _logger.info("failed search '" + sipEndpointA + "', '" + sipEndpointB + "'");

      return Responses.getBAD_REQUEST(
          "failed search");
    }
      
    _logger.info("ok");

    return Responses.getOK();    
  }

  @GetMapping(path = "/connection/")
  public ResponseEntity<ResponseDTO> getConnection() {

    return Responses.getNYI();    
  }

  final private static AnontionLog _logger = new AnontionLog(ConnectionGetController.class.getName());
}

