package com.example.springsecurity.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ProductSalesProjection {
    @Value("#{target.sales_year}")
    Integer getSalesYear(); // метод соответствует алиасу sales_year в запросе

    @Value("#{target.totalSold}")
    Integer getTotalSold(); // метод соответствует алиасу totalSold в запросе
}