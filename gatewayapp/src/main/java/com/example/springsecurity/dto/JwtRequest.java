package com.example.springsecurity.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String token;
    private String refreshToken;
}
