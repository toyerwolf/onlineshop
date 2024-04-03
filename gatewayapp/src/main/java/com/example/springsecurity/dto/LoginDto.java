package com.example.springsecurity.dto;

import lombok.Data;

@Data
public class LoginDto {

    private Long id;
    private String username;
    private String password;
}
