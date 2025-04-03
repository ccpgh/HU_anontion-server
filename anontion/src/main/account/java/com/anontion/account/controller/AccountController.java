package com.anontion.account.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.application.model.AnontionApplicationId;
import com.anontion.application.model.AnontionApplication;
import com.anontion.common.dto.request.RequestAccountDTO;

import com.anontion.application.repository.AnontionApplicationRepository;
import com.anontion.account.model.AnontionAccount;
import com.anontion.account.repository.AnontionAccountRepository;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;
import com.anontion.common.dto.response.ResponseBodyAccountDTO;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired
  private AnontionAccountRepository accountRepository;

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
    String high = requestAccountDTO.getBody().getHigh();
    String low = requestAccountDTO.getBody().getLow();
    String text = requestAccountDTO.getBody().getText();
    Long target = requestAccountDTO.getBody().getTarget();
        
    String message = String.format("Primary Key is Name '%s' ts '%d' Client '%s' AND High '%s' Low '%s' Text '%s' Target '%l'", name, ts, client, high, low, text, target);
    
    AnontionApplicationId applicationId = new AnontionApplicationId(name, ts, client);

    Optional<AnontionApplication> applicationOptional = applicationRepository.findById(applicationId);

    if (!applicationOptional.isPresent()) {
            
      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "No application."), new ResponseBodyErrorDTO("Application could not be found."));
      
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
    }

    AnontionAccount newAccount = new AnontionAccount(ts, name, client);

    System.out.println("Account: " + newAccount);

    AnontionAccount account = accountRepository.save(newAccount);
    
    ResponseBodyAccountDTO body = new ResponseBodyAccountDTO(account.getId(), account.getTs(), account.getName(), account.getAppplication(), account.getKey());
    
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(true, 0, "Ok."), body);
      
    return new ResponseEntity<>(response, HttpStatus.OK);    
  }

  @PutMapping(path = "/")
  public ResponseEntity<ResponseDTO> putAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }

  @DeleteMapping(path = "/")
  public ResponseEntity<ResponseDTO> deleteAccount() {

    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "NYI"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  }
}

