package com.voting.voteapp.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAdminUsernameException.class)
    public ResponseEntity<String> handleInvalidAdminUsernameException(InvalidAdminUsernameException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin user name");
    }

    @ExceptionHandler(AdminAlreadyExistsException.class)
    public ResponseEntity<String> handleAdminAlreadyExistsException(AdminAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin already exists");
    }



}
