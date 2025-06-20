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
import com.anontion.common.misc.AnontionLog;
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

    UUID client = requestApplicationDTO.getBody().getId();
    String pub = requestApplicationDTO.getBody().getPub();   
    String name = requestApplicationDTO.getBody().getName();
    String encrypt = requestApplicationDTO.getBody().getEncrypt();

    Long ts = AnontionTime.tsN();
    
    AnontionApplicationId applicationId = new AnontionApplicationId(name, ts, client);

    boolean exists = applicationRepository.existsById(applicationId);

    if (exists) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Application already exists!"), 
          new ResponseBodyErrorDTO(String.format("Primary Key is name '%s' ts '%d' client '%s'", name, ts, client)));
    
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    AnontionPOW pow = new AnontionPOW();
    
    String remote = AnontionSecurityECDSA.encodePubK1XY(AnontionSecurityECDSA.pub());
    
    String[] tokens = { name, ts.toString(), client.toString(), pub};

    String plaintext = AnontionStrings.concat(tokens, ":");

    String sign = AnontionSecurityECDSA.sign(plaintext);

    AnontionApplication newApplication = new AnontionApplication(name, ts, client, pub, plaintext, sign, pow.getText(), pow.getTarget(), encrypt);
    
    applicationRepository.save(newApplication);
    
    ResponseApplicationBodyDTO body =  new ResponseApplicationBodyDTO(newApplication.getText(), newApplication.getTarget(), remote, plaintext, sign, ts);
    
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(true, 0, "Ok"), body);
    
    _logger.info("Application created ok");
    
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
  
  final private static AnontionLog _logger = new AnontionLog(ApplicationController.class.getName());
}
 

