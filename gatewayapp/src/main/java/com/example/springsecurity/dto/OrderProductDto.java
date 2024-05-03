package com.example.springsecurity.dto;

import lombok.Data;

@Data
public class OrderProductDto {
    private Long id;
    private String productName;
    private int quantity;

}
