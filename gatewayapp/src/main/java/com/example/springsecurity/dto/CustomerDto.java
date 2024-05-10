package com.example.springsecurity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerDto {

    private Long id;
    private String name;
    private String surname;
//    private BigDecimal balance;
    private String address;
    private LocalDateTime registeredAt;
}
