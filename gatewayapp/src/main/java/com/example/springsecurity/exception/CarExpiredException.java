package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class CarExpiredException extends AppException{
    public CarExpiredException( String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
