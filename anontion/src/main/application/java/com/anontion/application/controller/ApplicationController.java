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
import com.anontion.common.security.pow.AnontionPOW;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;
import com.anontion.common.dto.response.ResponseBodyPOWDTO;

import jakarta.validation.Valid;

import com.anontion.application.model.AnontionApplication;
import com.anontion.application.model.AnontionApplicationId;
import com.anontion.application.repository.AnontionApplicationRepository;

@RestController
@RequestMapping("/application")
public class ApplicationController {

  @Autowired
  private AnontionApplicationRepository applicationRepository;
  
  @GetMapping(path = "/")
  public ResponseEntity<ResponseDTO> getApplication() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
  @PostMapping(path = "/")
  public ResponseEntity<ResponseDTO> postApplication(@Valid @RequestBody RequestApplicationDTO requestApplicationDTO, BindingResult bindingResult) {

	  if (bindingResult.hasErrors()) {
	    
		  String message = bindingResult.getAllErrors().stream()
				  .map(ObjectError::getDefaultMessage)
				  .collect(Collectors.joining(" "));

		  ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Invalid parameters!"), new ResponseBodyErrorDTO(message));

		  return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);    
	  }

    String name = requestApplicationDTO.getBody().getName();
    Integer ts = requestApplicationDTO.getBody().getTs();
    UUID client = requestApplicationDTO.getBody().getId();
    
    String message = String.format("Primary Key is Name '%s' ts '%d' Client '%s'", name, ts, client);
    
    AnontionApplicationId applicationId = new AnontionApplicationId(name, ts, client);

    boolean exists = applicationRepository.existsById(applicationId);

    if (exists) {

      System.out.println("Pre-existing application for: " + message);

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Application already exists!"), new ResponseBodyErrorDTO(message));
    
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    AnontionApplication newApplication = new AnontionApplication(name, ts, client);

    System.out.println("Application: " + newApplication);

    applicationRepository.save(newApplication);

    AnontionPOW pow = new AnontionPOW();
    
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(true, 0, "Success"), new ResponseBodyPOWDTO(pow.getText(), pow.getTarget()));
    
    return new ResponseEntity<>(response, HttpStatus.OK);    
  }

  @PutMapping(path = "/")
  public ResponseEntity<ResponseDTO> putApplication() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @DeleteMapping(path = "/")
  public ResponseEntity<ResponseDTO> deleteApplication() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
}

