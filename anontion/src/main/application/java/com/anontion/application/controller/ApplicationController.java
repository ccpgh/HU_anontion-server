package com.anontion.application.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.request.RequestApplicationDTO;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.misc.AnontionStrings;
import com.anontion.common.misc.AnontionTime;
import com.anontion.common.security.AnontionSecurityECDSA;
import com.anontion.common.security.AnontionPOW;
import com.anontion.common.dto.response.ResponseApplicationBodyDTO;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;

import jakarta.validation.Valid;

import com.anontion.models.application.model.AnontionApplication;
import com.anontion.models.application.model.AnontionApplicationId;
import com.anontion.models.application.repository.AnontionApplicationRepository;

@RestController
@RequestMapping("/application")
public class ApplicationController {

  final private static ResponseDTO _NYI = new ResponseDTO(
      new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
  
  @Autowired
  private AnontionApplicationRepository applicationRepository;

  @PostMapping(path = "/")
  public ResponseEntity<ResponseDTO> postApplication(@Valid @RequestBody RequestApplicationDTO request, BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {

      String message = bindingResult.getAllErrors().stream()
          .map(ObjectError::getDefaultMessage)
          .collect(Collectors.joining(" "));

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 
          1, 
          "Invalid parameters!"), 
          new ResponseBodyErrorDTO(message));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);    
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

    boolean exists = 
        applicationRepository.existsById(id);

    if (exists) {

      ResponseDTO response = new ResponseDTO(
          new ResponseHeaderDTO(false, 
              1, 
              "Application already exists!"), 
          new ResponseBodyErrorDTO(id.description()));

      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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

    ResponseApplicationBodyDTO body = 
        new ResponseApplicationBodyDTO(application.getText(), 
            application.getTarget(), 
            remote, 
            plaintext, 
            sign, 
            ts);

    ResponseDTO response = new ResponseDTO(
        new ResponseHeaderDTO(true, 
            0, 
            "Ok"), 
        body);

    return new ResponseEntity<>(response, 
        HttpStatus.OK);    
  }

  @GetMapping(path = "/")
  public ResponseEntity<ResponseDTO> getApplication() {

    return new ResponseEntity<>(_NYI, 
        HttpStatus.UNAUTHORIZED);    
  }

  @PutMapping(path = "/")
  public ResponseEntity<ResponseDTO> putApplication() {

    return new ResponseEntity<>(_NYI, 
        HttpStatus.UNAUTHORIZED);    
  }

  @DeleteMapping(path = "/")
  public ResponseEntity<ResponseDTO> deleteApplication() {

    return new ResponseEntity<>(_NYI, 
        HttpStatus.UNAUTHORIZED);    
  }
}


