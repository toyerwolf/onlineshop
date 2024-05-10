package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class OrderDeliveredException extends AppException{
    public OrderDeliveredException( String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
