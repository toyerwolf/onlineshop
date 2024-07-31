package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class UploadPathException extends AppException{
    public UploadPathException( String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
