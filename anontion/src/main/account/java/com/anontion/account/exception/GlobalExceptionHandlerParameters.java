package com.anontion.account.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.anontion.common.dto.response.ResponseBodyErrorDTO;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseGenericDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;

@ControllerAdvice
public class GlobalExceptionHandlerParameters {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
    
    Map<String, String> errors = new HashMap<>();
    
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    String flat = errors.entrySet()
        .stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.joining(", "));
    
    ResponseDTO response = new ResponseGenericDTO(new ResponseHeaderDTO(false, 1, "Bad Request Information(MethodArgumentNotValid)"), new ResponseBodyErrorDTO(flat));
    
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}
