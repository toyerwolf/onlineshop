package com.example.springsecurity.projection;

import org.springframework.beans.factory.annotation.Value;

public interface ProductSalesProjectionByYear {

    @Value("#{target.product_name}")
    String getProductName(); // Соответствует алиасу product_name

    @Value("#{target.total_sold}")
    Integer getTotalSold(); //
}
