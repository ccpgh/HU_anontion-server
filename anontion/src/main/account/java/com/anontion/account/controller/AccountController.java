package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Set;
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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import com.anontion.account.config.ValidationConfig;

@RestController
@Validated
public class AccountController {

  //@Autowired
  //private AnontionAccountRepository accountRepository;
  
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
				  .collect(Collectors.joining("|"));

		  ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Invalid parameters!"), new ResponseBodyErrorDTO(message));

		  return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);    
	  }

	  System.out.println("ID: " + requestAccountDTO.getBody().getId());
    
//      boolean accountExists = accountService.accountExists(body.getId());
//
//      if (accountExists) {
//          // Return response indicating account exists
//          return new ResponseEntity<>(new ApiResponseDTO("Account already exists"), HttpStatus.BAD_REQUEST);
//      }
//
//      // If account does not exist, you can create or perform other logic
//      accountService.createAccount(body);
//
//      // Return a success response
//      return new ResponseEntity<>(new ApiResponseDTO("Account created successfully"), HttpStatus.OK);
//  }
//  
//  @PostMapping("/account")
//  public ResponseEntity<ResponseDTO> postAccount(@Valid @RequestBody RequestAccountDTO requestAccountDTO, 
//                                                 BindingResult bindingResult) {
//      if (bindingResult.hasErrors()) {
//          // You can collect all errors and send them as a response
//          List<String> errors = bindingResult.getAllErrors().stream()
//                                            .map(ObjectError::getDefaultMessage)
//                                            .collect(Collectors.toList());
//          return new ResponseEntity<>(new ResponseDTO(errors), HttpStatus.BAD_REQUEST);
//      }
//
//      // If no validation errors, process the account creation logic
//      ResponseDTO responseDTO = accountService.createAccount(requestAccountDTO);
//      return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
	  
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 0, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
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

