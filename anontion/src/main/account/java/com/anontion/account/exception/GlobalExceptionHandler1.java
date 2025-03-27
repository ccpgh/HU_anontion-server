package com.anontion.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.anontion.common.dto.response.ResponseBodyErrorDTO;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;

@ControllerAdvice
public class GlobalExceptionHandler1 {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ResponseDTO> handleInvalidJson(HttpMessageNotReadableException ex) {
    
    String errorMessage = "Invalid JSON format or missing required fields.";
    
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad Request Information(HttpMessageNotReadable)"), new ResponseBodyErrorDTO(errorMessage));
    
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
