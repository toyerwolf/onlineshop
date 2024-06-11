package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.JwtResponse;
import com.example.springsecurity.dto.LoginDto;
import com.example.springsecurity.security.JwtTokenProvider;
import com.example.springsecurity.security.UserPrincipal;
import com.example.springsecurity.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;




    @Override
    public JwtResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        JwtResponse jwtResponse = jwtTokenProvider.generateTokens(userPrincipal);
        return new JwtResponse(jwtResponse.getAccessToken(),jwtResponse.getRefreshToken());
    }

    @Override
    public JwtResponse refreshAccessTokenAndGenerateNewToken(String oldToken) {
       if(!jwtTokenProvider.validateToken(oldToken)){
           throw new RuntimeException("none");}
           String username= jwtTokenProvider.getUserNameFromJwtToken(oldToken);
           return new JwtResponse(jwtTokenProvider.generateTokenFromUsername(username), jwtTokenProvider.generateRefreshTokenFromUsername(username) );
       }


    public Long getCustomerIdFromToken(String authHeader) {
        // Извлекаем токен из заголовка "Authorization"
        String token = authHeader.replace("Bearer ", "");

        // Парсим токен и извлекаем идентификатор клиента
        Claims claims = jwtTokenProvider.getAllClaimsFromToken(token);
        return claims.get("customerId", Long.class);
    }
    }



