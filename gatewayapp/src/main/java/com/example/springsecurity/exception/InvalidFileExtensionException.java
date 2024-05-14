package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class InvalidFileExtensionException extends AppException{
    public InvalidFileExtensionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
