package com.example.springsecurity.controller;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
@AllArgsConstructor
public class TestController {




    @GetMapping()
    public String test(){
        return "Success";
    }


}