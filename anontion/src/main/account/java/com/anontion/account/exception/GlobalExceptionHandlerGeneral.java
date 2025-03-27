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
public class GlobalExceptionHandlerGeneral {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ResponseDTO> handleInvalidJson(HttpMessageNotReadableException ex) {
    
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Bad Request (HttpMessageNotReadable)"), new ResponseBodyErrorDTO("Format could not be read."));
    
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
