package com.example.springsecurity.controller;//package com.example.springsecurity.controller;
//
//import com.example.springsecurity.req.CustomerCardReq;
//import com.example.springsecurity.req.OrderRequest;
//import com.example.springsecurity.service.impl.CardServiceImpl;
//import com.example.springsecurity.service.impl.TestService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;

import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.req.PaymentRequest;
import com.example.springsecurity.service.impl.PaymentServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
@AllArgsConstructor
public class TestController {

    @GetMapping("/")
    public String test(){
        return "OK";
    }


    }

