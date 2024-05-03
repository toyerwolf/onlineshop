package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.JwtResponse;
import com.example.springsecurity.dto.LoginDto;
import com.example.springsecurity.exception.AccessTokenExpiredException;
import com.example.springsecurity.securiy.JwtTokenProvider;
import com.example.springsecurity.securiy.UserPrincipal;
import com.example.springsecurity.service.AuthService;
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
        return new JwtResponse(jwtResponse.getToken(),jwtResponse.getRefreshToken());
    }

    @Override
    public JwtResponse refreshAccessTokenAndGenerateNewToken(String oldToken) {
       if(!jwtTokenProvider.validateToken(oldToken)){
           throw new RuntimeException("none");}
           String username= jwtTokenProvider.getUserNameFromJwtToken(oldToken);
           return new JwtResponse(jwtTokenProvider.generateTokenFromUsername(username), jwtTokenProvider.generateRefreshTokenFromUsername(username) );
       }
    }



