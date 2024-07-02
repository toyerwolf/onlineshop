package com.example.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DiscountProductResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal discountDescription;
    private String  imageUrl;



}