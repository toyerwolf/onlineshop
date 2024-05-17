package com.example.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoldProductsResponse {
    private Map<String, Integer> soldProductCounts;
    private String productName;
    private Integer totalSold;
}
