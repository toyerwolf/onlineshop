package com.example.springsecurity.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;

    public ErrorResponse(String message) {
        super();
        this.message = message;
    }
}

