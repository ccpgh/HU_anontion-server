package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.asterisk.model.AsteriskAuth;
import com.anontion.asterisk.model.AsteriskEndpoint;
import com.anontion.asterisk.repository.AsteriskAuthRepository;
import com.anontion.asterisk.repository.AsteriskEndpointRepository;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.dto.response.ResponseGetAccountBodyDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.ResponsePostDTO;

@RestController
public class AccountGetController {

  @Autowired
  private AsteriskEndpointRepository endpointRepository;

  @Autowired
  private AsteriskAuthRepository authRepository;

  @GetMapping(path = "/account/")
  public ResponseEntity<ResponseDTO> getAccount(@RequestParam("sipUsername") String sipUsername,
      @RequestParam("sipPassword") String sipPassword) {

    if (!AnontionStrings.isValidName(sipUsername) ||
        !AnontionStrings.isValidPassword(sipPassword)) {
      
      return Responses.getBAD_REQUEST(
          "Parameters Username/Password failed validity check.", 
          "Transaction rejected.");
    }
      
    Optional<AsteriskEndpoint> auth0 = 
        endpointRepository.findById(sipUsername);
    
    if (auth0.isEmpty()) {
      return Responses.getBAD_REQUEST(
          "No matching account found.", 
          "Transaction rejected.");
    }

    AsteriskEndpoint endpoint = auth0.get();

    _logger.info("DEBUG Found account with aors = '" + endpoint.getAors() + "'");
        
    ResponseGetAccountBodyDTO body = new ResponseGetAccountBodyDTO("placeholder");
        
    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  final private static AnontionLog _logger = new AnontionLog(AccountGetController.class.getName());
}


