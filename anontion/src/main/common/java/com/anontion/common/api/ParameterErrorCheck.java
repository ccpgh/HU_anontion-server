//package com.anontion.common.api; TODO DELETE FILE   
//
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.ObjectError;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validator;
//
//import java.util.Set;
//
//public class ParameterErrorCheck {
//
//  public static boolean check(Object target, BindingResult bindingResult, Validator validator) { // TODO - replace by built-in spring error checks.
//
//    Set<ConstraintViolation<Object>> violations = validator.validate(target);
//
//    for (ConstraintViolation<Object> violation : violations) {
//
//      String errorMessage = violation.getMessage();
//
//      String fieldName = violation.getPropertyPath().toString();
//      
//      ObjectError error = new ObjectError(fieldName, errorMessage);
//      
//      bindingResult.addError(error);
//    }
//    
//    return !violations.isEmpty();
//  }
//  
//}