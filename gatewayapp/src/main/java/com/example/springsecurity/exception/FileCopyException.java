package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class FileCopyException extends AppException{
    public FileCopyException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
