package com.example.springsecurity.dto;


import lombok.Data;

import java.util.List;

@Data
public class YearlySalesRevenueResponseDTO {
    private List<YearlySalesRevenueDTO> yearlySales;
}
