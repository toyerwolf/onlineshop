package com.example.springsecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTestUtil {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public String generateToken(String username, String role) {
        return jwtTokenProvider.generateTokenFromUsername(username);
    }

    public String generateRefreshToken(String username) {
        return jwtTokenProvider.generateRefreshTokenFromUsername(username);
    }
}
