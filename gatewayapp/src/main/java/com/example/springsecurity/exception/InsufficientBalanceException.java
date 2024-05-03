package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends AppException{
    public InsufficientBalanceException( String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
