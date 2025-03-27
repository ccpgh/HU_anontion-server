package com.anontion.account.controller;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.validation.Validator;
//import org.springframework.validation.Validation;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//import org.springframework.validation.beanvalidation.ValidationFactory;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.ResponseHeaderDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.Validation;

import com.anontion.account.config.ValidationConfig;
import com.anontion.common.dto.response.ResponseBodyErrorDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

class Car {

  @NotNull
  private String manufacturer;
  
  @NotNull
  @Size(min = 2, max = 14)
  private String licensePlate;
  
  @Min(2)
  private int seatCount;
  
  @AssertTrue
  private boolean blah;
  
  public Car(String manufacturer, String licencePlate, int seatCount) {
  
    this.manufacturer = manufacturer;
  
    this.licensePlate = licencePlate;
  
    this.seatCount = seatCount;
  }
  
  public Car() {
    
  }
  
  public String getManufacturer() {
    
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    
    this.manufacturer = manufacturer;
  }

  public String getLicensePlate() {
    
    return licensePlate;
  }

  public void setLicensePlate(String licensePlate) {
    
    this.licensePlate = licensePlate;
  }

  public int getSeatCount() {
    
    return seatCount;
  }

  public void setSeatCount(int seatCount) {
   
    this.seatCount = seatCount;
  }
  
  public boolean getBlah() {
    
    return blah;
  }

  public void setBlah(boolean blah) {
   
    this.blah = blah;
  }

}

@RestController
@Valid
public class AccountController {

  @Autowired
  private Validator validator;

  @Valid
  public boolean f(@Valid Car requestAccountDTO, BindingResult bindingResult) {

    //Set<ConstraintViolation<Car>> manualViolations = validator.validate(requestAccountDTO);

	  //System.out.println("violations: " + manualViolations.size());

	  //if (!manualViolations.isEmpty()) {

	  //return true;
	  //}

	return bindingResult.hasErrors();
  }
  
  @PostMapping(path = "/")
  @Valid
  public ResponseEntity<ResponseDTO> postAccount(@Valid @RequestBody Car requestAccountDTO, BindingResult bindingResult) {

	//ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	
	//executableValidator = factory.getValidator().forExecutables();
	  
//	if (this.f(requestAccountDTO, bindingResult)) {
				
	  //	  ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 2, "Automatic errors (1)"), new ResponseBodyErrorDTO());
	      
	  //return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
	  //	}
	
    if (bindingResult.hasErrors()) { 

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Automatic errors (2)"), new ResponseBodyErrorDTO());
      
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
    }

    Set<ConstraintViolation<Car>> manualViolations = validator.validate(requestAccountDTO);
    
    String violationsString = manualViolations.stream()
            .map(violation -> violation.getMessage())
            .collect(Collectors.joining(", "));
    
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Test errors " + violationsString + "  " + validator.getClass().toString()), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    

    /**
    if (bindingResult.hasErrors()) { 

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Automatic errors"), new ResponseBodyErrorDTO());
      
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
    }
  
    Set<ConstraintViolation<RequestAccountDTO>> manualViolations = validator.validate(requestAccountDTO);

    System.out.println("violations: " + manualViolations.size());

    if (!manualViolations.isEmpty()) {

      String violationsString = manualViolations.stream()
          .map(violation -> violation.getMessage())
          .collect(Collectors.joining(", "));
      
      System.err.println("violationsString " + violationsString);

      ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "Manual errors"), new ResponseBodyErrorDTO(violationsString));
        
      
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);      
    }
        
    ResponseDTO response = new ResponseDTO(new ResponseHeaderDTO(false, 1, "No errors"), new ResponseBodyErrorDTO());
    
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);    
  */
  }
}

//org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
//org.springframework.validation.beanvalidation.LocalValidatorFactoryBean