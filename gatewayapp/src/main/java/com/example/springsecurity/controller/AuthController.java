package com.example.springsecurity.controller;


import com.example.springsecurity.dto.JwtRequest;
import com.example.springsecurity.dto.JwtResponse;
import com.example.springsecurity.dto.LoginDto;
import com.example.springsecurity.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginDto loginDto) {
        JwtResponse jwtResponse = authService.login(loginDto);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody JwtRequest jwtRequest) {
        JwtResponse jwtResponse = authService.refreshAccessTokenAndGenerateNewToken(jwtRequest.getRefreshToken());
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }






}
