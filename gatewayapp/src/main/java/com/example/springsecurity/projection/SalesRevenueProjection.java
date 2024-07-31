package com.example.springsecurity.projection;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public interface SalesRevenueProjection {
    @Value("#{target.saleYear}")
    Integer getYear(); // Соответствует алиасу saleYear

    @Value("#{target.totalRevenue}")
    BigDecimal getTotalRevenue(); // Соответствует алиасу totalRevenue
}