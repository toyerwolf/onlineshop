package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class DirectoryCreationException extends AppException{
    public DirectoryCreationException( String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
