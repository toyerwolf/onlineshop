package com.example.springsecurity.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserRegistrationReq {
    private String username;
    private String password;
    private String name;
    private String surname;
    private String address;
    private BigDecimal balance;
}
