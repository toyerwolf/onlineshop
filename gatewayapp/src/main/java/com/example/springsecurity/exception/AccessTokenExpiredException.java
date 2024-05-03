package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class AccessTokenExpiredException extends AppException{
    public AccessTokenExpiredException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}
