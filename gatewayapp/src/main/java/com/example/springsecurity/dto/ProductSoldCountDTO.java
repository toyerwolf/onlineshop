package com.example.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSoldCountDTO {

    private String productName;
    private int soldCount;
}
