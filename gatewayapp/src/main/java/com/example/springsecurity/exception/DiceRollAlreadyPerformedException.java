package com.example.springsecurity.exception;

import org.springframework.http.HttpStatus;

public class DiceRollAlreadyPerformedException extends AppException{
    public DiceRollAlreadyPerformedException(String message) {
        super(HttpStatus.I_AM_A_TEAPOT, message);
    }
}
