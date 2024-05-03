package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class InsufficientQuantityException extends AppException{
    public InsufficientQuantityException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
