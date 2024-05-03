package com.example.springsecurity.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserReq {

    private Long id;
    private String username;
    private LocalDateTime createdAt;
    private boolean locked;

    private CustomerReq customer;
}
