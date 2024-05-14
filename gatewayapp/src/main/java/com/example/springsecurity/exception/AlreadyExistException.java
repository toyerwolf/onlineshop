package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistException extends AppException{
    public AlreadyExistException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
