package com.example.springsecurity.controller;

import com.example.springsecurity.dto.ChangePasswordDto;
import com.example.springsecurity.service.PasswordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("admin")
public class TestController {



    private final PasswordService passwordService;



    @PostMapping("changePassword")
    public ResponseEntity<String> changePassword(@ModelAttribute ChangePasswordDto changePasswordDto){
        return new ResponseEntity<>(passwordService.changePassword(changePasswordDto), HttpStatus.OK);
    }



    @GetMapping("test")
    public String sayHello(){
        return "Hello";
    }


}
