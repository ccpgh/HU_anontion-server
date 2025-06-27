package com.anontion.common.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Responses {

  final private static ResponseDTO _NYI = new ResponseGenericDTO(
      new ResponseHeaderDTO(false, 1, "Not Implemented"), new ResponseBodyErrorDTO());
  
  final private static ResponseEntity<ResponseDTO> _responseNYI = 
      new ResponseEntity<>(_NYI, HttpStatus.UNAUTHORIZED); 
  
  public static ResponseEntity<ResponseDTO> getBAD_REQUEST(String message) {
    
    return Responses.getBAD_REQUEST(message, message);
  }
  
  public static ResponseEntity<ResponseDTO> getBAD_REQUEST(String head, String body) {

    ResponseDTO response = new ResponseGenericDTO(new ResponseHeaderDTO(
        false, 
        1, 
        head), 
        new ResponseBodyErrorDTO(body));

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
  
  public static ResponseEntity<ResponseDTO> getNYI() {
  
    return _responseNYI;
  }

}
