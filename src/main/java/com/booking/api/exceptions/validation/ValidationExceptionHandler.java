package com.booking.api.exceptions.validation;

import com.booking.api.exceptions.NotFoundException.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {
   @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();

        for(FieldError fieldError : bindingResult.getFieldErrors()){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFoundException(NotFoundException ex){
        Map<String,Object> errors = new HashMap<>();
        errors.put("message",ex.getMessage());
        errors.put("status",HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(errors,HttpStatus.NOT_FOUND);
    }
}
