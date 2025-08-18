package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
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
  public ResponseEntity<ResponseDTO> getAccount(@RequestParam("sipUserId") String sipUserId,
      @RequestParam("sipPassword") String sipPassword) {

    if (!AnontionStrings.isValidName(sipUserId) ||
        !AnontionStrings.isValidPassword(sipPassword)) {
      
      return Responses.getBAD_REQUEST(
          "Parameters Username/Password failed validity check.", 
          "Transaction rejected.");
    }
      
    String md5Input = sipUserId + ":asterisk:" + sipPassword;
    
    String md5Cred = DigestUtils.md5DigestAsHex(md5Input.getBytes());
    
    Optional<AsteriskAuth> auth0 = 
        authRepository.findByUsernameAndMd5Cred(sipUserId, md5Cred);
    
    if (auth0.isEmpty()) {
      return Responses.getBAD_REQUEST(
          "No account(auth)", 
          "");
    }

    AsteriskAuth auth = auth0.get();

    _logger.info("DEBUG Found account with auth id = '" + auth.getId() + "'");

    Optional<AsteriskEndpoint> endpoint0 = 
        endpointRepository.findByAuth(auth.getId());
    
    if (endpoint0.isEmpty()) {
      return Responses.getBAD_REQUEST(
          "No account(endpoint).", 
          "");      
    }

    AsteriskEndpoint endpoint = endpoint0.get();

    ResponseGetAccountBodyDTO body = new ResponseGetAccountBodyDTO(endpoint.getId());
        
    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 0, "Ok."), body);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
  
  final private static AnontionLog _logger = new AnontionLog(AccountGetController.class.getName());
}


