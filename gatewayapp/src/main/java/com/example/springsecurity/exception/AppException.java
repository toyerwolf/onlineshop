package com.example.springsecurity.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class AppException extends RuntimeException{

    HttpStatus status;

    public AppException(HttpStatus status,String message) {
        super(message);
        this.status = status;

    }
}
