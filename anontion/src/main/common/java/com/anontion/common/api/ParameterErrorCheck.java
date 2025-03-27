package com.anontion.common.api;    

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;

public class ParameterErrorCheck {

  public static boolean check_OFF(Object target, BindingResult bindingResult, Validator validator) { // TODO - replace by built-in spring error checks.

    System.err.println("Validator check");

    System.err.println("Validator BEFORE " + validator);

    Set<ConstraintViolation<Object>> violations = validator.validate(target);

    System.err.println("Validator AFTER " + validator);

    for (ConstraintViolation<Object> violation : violations) {

      String errorMessage = violation.getMessage();

      String fieldName = violation.getPropertyPath().toString();
      
      ObjectError error = new ObjectError(fieldName, errorMessage);
      
      bindingResult.addError(error);
    }
    
    return !violations.isEmpty();
  }
  
}