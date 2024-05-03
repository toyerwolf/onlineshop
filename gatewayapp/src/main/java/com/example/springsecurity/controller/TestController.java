package com.example.springsecurity.controller;

import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.service.impl.OrderServiceImpl;
import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class TestController {

    private final OrderServiceImpl orderService;

//
//    @Secured("ADMIN")
//    @GetMapping()
//    public String sayHello(){
//        return "Hello";
//    }


}
