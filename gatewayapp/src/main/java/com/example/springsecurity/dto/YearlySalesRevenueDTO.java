package com.example.springsecurity.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearlySalesRevenueDTO {

    private Integer year;
    private BigDecimal totalRevenue;
}
