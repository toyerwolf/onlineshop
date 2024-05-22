package com.example.springsecurity.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDtoContainer {
    private List<ProductDto> products;
}
