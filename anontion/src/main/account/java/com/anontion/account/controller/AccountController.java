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

//import com.anontion.account.model.AnontionAccount;
//import com.anontion.account.repository.AnontionAccountRepository;
//import com.anontion.account.service.AccountBean;
//import org.springframework.stereotype.Service;
//import jakarta.servlet.http.HttpServletRequest;
//import java.io.BufferedReader;
//import java.util.Optional;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import com.anontion.common.api.ParameterErrorCheck;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;

import com.anontion.account.config.ValidationConfig;
//import com.anontion.common.dto.request.PersonForm;



@RestController
@Validated
public class AccountController {

  //@Autowired
  //private AnontionAccountRepository accountRepository;
  
  @Autowired
  private Validator validator;

  @GetMapping("/")
  public ResponseEntity<ResponseDTO> getAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
  @PostMapping(path = "/")
  @Validated
  public ResponseEntity<ResponseDTO> postAccount(@Validated @RequestBody RequestAccountDTO requestAccountDTO, BindingResult bindingResult) {

    //if (bindingResult.hasErrors()) {

    //  ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress 3"), new ResponseBodyErrorDTO());
      
    //  return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
    //}
  
    PersonForm p = new PersonForm();

    Set<ConstraintViolation<PersonForm>> violations1 = validator.validate(p);

    System.out.println("violations p: " + violations1.size());

    RequestAccountDTO a = new RequestAccountDTO();

    Set<ConstraintViolation<RequestAccountDTO>> violations2 = validator.validate(a);

    System.out.println("violations a: " + violations2.size());

    System.out.println("Body: " + requestAccountDTO.getBody());

    System.out.println("validator: " + validator.getClass());

    if (bindingResult.hasErrors()) { //|| !violations.isEmpty()) {

      String message = bindingResult.getAllErrors().stream()
      .map(ObjectError::getDefaultMessage)
      .collect(Collectors.joining("\n"));
        
        ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress 3"), new ResponseBodyErrorDTO(message));
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
      }

    Set<ConstraintViolation<RequestAccountDTO>> violations = validator.validate(requestAccountDTO);

    System.out.println("violations: " + violations.size());

    if (bindingResult.hasErrors() || !violations.isEmpty()) {

      String message = bindingResult.getAllErrors().stream()
      .map(ObjectError::getDefaultMessage)
      .collect(Collectors.joining("\n"));
      
      String violationsString = violations.stream()
          .map(violation -> violation.getMessage())
          .collect(Collectors.joining(", "));
      
      System.err.println("violationsString " + violationsString)
        ;
        ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress 3"), new ResponseBodyErrorDTO(violationsString));
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
      }
    
    if (requestAccountDTO.getBody() != null) {

      System.out.println("body ts: " + requestAccountDTO.getBody().getTs());
      
      System.out.println("body id: " + requestAccountDTO.getBody().getId());
    }
    
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress 2"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
  //@PostMapping("/")
//  @PostMapping(path = "/OFF", consumes = "application/json", produces = "application/json")
//  public ResponseEntity<ResponseDTO> postAccount_OFF(@RequestBody @Valid RequestAccountDTO requestAccountDTO, BindingResult bindingResult) {
//
////    System.err.println("BindingResult " + bindingResult);
//
//    System.err.println("Validator " + validator);
//
//  // if (bindingResult.hasErrors()) {
//    if (bindingResult.hasErrors()) {
//
//      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress 3"), new ResponseBodyErrorDTO());
//      
//      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
//    }

 //if (ParameterErrorCheck.check(requestAccountDTO, bindingResult, validator)) {

    //    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress 3"), new ResponseBodyErrorDTO());
     
    //return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
    //}

//    if (bindingResult.hasErrors() || ParameterErrorCheck.check(requestAccountDTO, bindingResult, validator)) {
//      
//      String message = bindingResult.getAllErrors().stream()
//          .map(ObjectError::getDefaultMessage)
//          .collect(Collectors.joining("\n"));
//      
//      System.err.println("Message: " + message);
//      
//      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad Request Information"), new ResponseBodyErrorDTO(message));
//      
//      return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);    
//    }
//
//    System.err.println("bindingResult " + bindingResult);
//
//    // Extract the header and body
//    RequestAccountHeaderDTO header = requestAccountDTO.getHeader();
//    RequestAccountBodyDTO body = requestAccountDTO.getBody();
//
//    // Debug: Print out values (for example, logging or testing purposes)
//    System.out.println("Originator: " + header.getOriginator());
//    System.out.println("body: " + body);

//  System.out.println("ID: " + body.getId());
//  System.out.println("Timestamp: " + body.getTs());
//  System.out.println("Name: " + body.getName());
    
    // Print the received JSON (via the DTO object)
    //System.out.println("Received DTO: " + myDto);
    
    //String json = "{\n" +
    //        "  \"foor\": \"" + dto.getBar() + "\",\n" +
    //        "}";   
    //System.out.println("Raw JSON received: " + json);
    
    /*
    StringBuilder jsonString = new StringBuilder();
    try (BufferedReader reader = request.getReader()) {
      String line;
      while ((line = reader.readLine()) != null) {
          jsonString.append(line);
      }
    } catch (java.io.IOException e) {

      e.printStackTrace();
    }
    System.out.println("Raw JSON received: " + jsonString.toString());
    */
//    
//    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress 2"), new ResponseBodyErrorDTO());
//    
//    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
//  }

//  private final AccountService accountService;
//
//  public AccountController(AccountService accountService) {
//      this.accountService = accountService;
//  }
//
//  @PostMapping("/account")
//  public ResponseEntity<ApiResponseDTO> postAccount(@RequestBody RequestAccountDTO requestAccountDTO) {
//      // Extract the header and body
//      RequestAccountHeaderDTO header = requestAccountDTO.getHeader();
//      RequestAccountBodyDTO body = requestAccountDTO.getBody();
//
//      // Debug: Print out values (for example, logging or testing purposes)
//      System.out.println("Originator: " + header.getOriginator());
//      System.out.println("ID: " + body.getId());
//      System.out.println("Timestamp: " + body.getTs());
//      System.out.println("Name: " + body.getName());
//
//      // Check if the account exists (add your business logic here)
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
//  }
//  
  
  @PutMapping("/")
  public ResponseEntity<ResponseDTO> putAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @DeleteMapping("/")
  public ResponseEntity<ResponseDTO> deleteAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Work in progress"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
}

