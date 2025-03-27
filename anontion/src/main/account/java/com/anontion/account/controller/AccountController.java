package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.request.RequestAccountDTO;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;

import jakarta.validation.Valid;

import com.anontion.account.model.AnontionApplication;
import com.anontion.account.model.AnontionApplicationId;
import com.anontion.account.repository.AnontionApplicationRepository;

@RestController
@Valid
public class AccountController {

  //@Autowired
  //private AnontionAccountRepository accountRepository;
  
  @Autowired
  private AnontionApplicationRepository applicationRepository;
  
  @GetMapping("/")
  public ResponseEntity<ResponseDTO> getAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
  @PostMapping(path = "/")
  public ResponseEntity<ResponseDTO> postAccount(@Valid @RequestBody RequestAccountDTO requestAccountDTO, BindingResult bindingResult) {

	  if (bindingResult.hasErrors()) {
	    
		  String message = bindingResult.getAllErrors().stream()
				  .map(ObjectError::getDefaultMessage)
				  .collect(Collectors.joining(" "));

		  ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Invalid parameters!"), new ResponseBodyErrorDTO(message));

		  return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);    
	  }

    String name = requestAccountDTO.getBody().getName();
    Integer ts = requestAccountDTO.getBody().getTs();
    UUID client = requestAccountDTO.getBody().getId();
    
    System.out.println(String.format("Name/Id/Client: %s/%d/%s", name, ts, client));

    AnontionApplicationId applicationId = new AnontionApplicationId(name, ts, client);

    boolean exists = applicationRepository.existsById(applicationId);

    if (exists) {

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Application already exists!"), new ResponseBodyErrorDTO()); // TODO - fix.
    
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    AnontionApplication newApplication = new AnontionApplication(name, ts, client);

    System.out.println("Application: " + newApplication);

    applicationRepository.save(newApplication);

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 0, "Success"), new ResponseBodyErrorDTO()); // TODO - fix.
    
    return new ResponseEntity<>(response, HttpStatus.CREATED);    
  }

  @PutMapping("/")
  public ResponseEntity<ResponseDTO> putAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @DeleteMapping("/")
  public ResponseEntity<ResponseDTO> deleteAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
}

