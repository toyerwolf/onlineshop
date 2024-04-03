package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class CustomerAlreadyExist extends AppException{
    public CustomerAlreadyExist(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
