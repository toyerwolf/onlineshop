package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class PasswordExpiredException extends AppException{
    public PasswordExpiredException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}
