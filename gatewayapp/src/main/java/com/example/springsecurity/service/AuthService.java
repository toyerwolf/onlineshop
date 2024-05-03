package com.example.springsecurity.service;

import com.example.springsecurity.dto.JwtResponse;
import com.example.springsecurity.dto.LoginDto;

public interface AuthService {

    JwtResponse login(LoginDto loginDto);

    JwtResponse refreshAccessTokenAndGenerateNewToken(String oldToken);
}
