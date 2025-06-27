package com.anontion.application.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.request.RequestPostApplicationDTO;

import com.anontion.common.dto.response.ResponsePostDTO;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionPOW;
import com.anontion.common.dto.response.ResponsePostApplicationBodyDTO;

import jakarta.validation.Valid;

import com.anontion.models.application.model.AnontionApplication;
import com.anontion.models.application.model.AnontionApplicationId;
import com.anontion.models.application.repository.AnontionApplicationRepository;

@RestController
public class ApplicationPostController {

  @Autowired
  private AnontionApplicationRepository applicationRepository;

  @PostMapping(path = "/application/")
  public ResponseEntity<ResponseDTO> postApplication(@Valid @RequestBody RequestPostApplicationDTO request, 
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream()
          .map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      return Responses.getBAD_REQUEST("Bad arguments", message);
    }

    UUID   client  = request.getBody().getId();
    String pub     = request.getBody().getPub();   
    String name    = request.getBody().getName();
    String encrypt = request.getBody().getEncrypt();

    Long ts = AnontionTime.tsN();

    AnontionApplicationId id = new AnontionApplicationId(
        name, 
        ts, 
        client);

    boolean exists = applicationRepository.existsById(id);

    if (exists) {

      return Responses.getBAD_REQUEST("Application already exists!");
    }

    AnontionPOW pow = new AnontionPOW();

    String remote = AnontionSecurityECDSA.encodePubK1XY(AnontionSecurityECDSA.pub());

    String plaintext = AnontionStrings.concat(new String[] { 
        name, 
        ts.toString(), 
        client.toString(), 
        pub }, ":");

    String sign = AnontionSecurityECDSA.sign(plaintext);

    AnontionApplication application = 
        new AnontionApplication(name, 
            ts, 
            client, 
            pub, 
            plaintext, 
            sign,
            pow.getText(), 
            pow.getTarget(), 
            encrypt, 
            false);

    applicationRepository.save(application);

    ResponsePostApplicationBodyDTO body = 
        new ResponsePostApplicationBodyDTO(application.getText(), 
            application.getTarget(), 
            remote, 
            plaintext, 
            sign, 
            ts);

    ResponsePostDTO response = new ResponsePostDTO(
        new ResponseHeaderDTO(true, 
            0, 
            "Ok"), 
        body);

    return new ResponseEntity<>(response, 
        HttpStatus.OK);    
  }
}


