package com.example.springsecurity.controller;

import com.example.springsecurity.req.UserRegistrationReq;
import com.example.springsecurity.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody UserRegistrationReq request) {
        registrationService.register(request);
        return new ResponseEntity<>("registered", HttpStatus.CREATED);
    }


}
