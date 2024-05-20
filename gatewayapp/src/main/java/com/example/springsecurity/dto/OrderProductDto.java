package com.example.springsecurity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderProductDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;



}
