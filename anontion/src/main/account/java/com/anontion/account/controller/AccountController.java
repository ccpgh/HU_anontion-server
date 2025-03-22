package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.account.model.AnontionAccount;
import com.anontion.account.repository.AnontionAccountRepository;
import com.anontion.account.service.AccountBean;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.util.Optional;

//import java.util.UUID;

import com.anontion.common.dto.response.ApiResponseDTO;
import com.anontion.common.dto.response.ApiResponseHeaderDTO;
import com.anontion.common.dto.response.ApiResponseBodyErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class AccountController {

  @Autowired
  //private AnontionAccountRepository accountRepository;
  
  @GetMapping("/")
  public ResponseEntity<ApiResponseDTO> getAccount() {

    ApiResponseDTO response = new ApiResponseDTO(new ApiResponseHeaderDTO(false, 1, "Work in progress"), new ApiResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
  @PostMapping("/")
  public ResponseEntity<ApiResponseDTO> postAccount(HttpServletRequest request) {

    // Print the received JSON (via the DTO object)
    //System.out.println("Received DTO: " + myDto);
    
    //String json = "{\n" +
    //        "  \"foor\": \"" + dto.getBar() + "\",\n" +
    //        "}";   
    //System.out.println("Raw JSON received: " + json);
    
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

    ApiResponseDTO response = new ApiResponseDTO(new ApiResponseHeaderDTO(false, 1, "Work in progress"), new ApiResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @PutMapping("/")
  public ResponseEntity<ApiResponseDTO> putAccount() {

    ApiResponseDTO response = new ApiResponseDTO(new ApiResponseHeaderDTO(false, 1, "Work in progress"), new ApiResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @DeleteMapping("/")
  public ResponseEntity<ApiResponseDTO> deleteAccount() {

    ApiResponseDTO response = new ApiResponseDTO(new ApiResponseHeaderDTO(false, 1, "Work in progress"), new ApiResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
  
}

