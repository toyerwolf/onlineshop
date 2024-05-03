package com.example.springsecurity.service;

import com.example.springsecurity.dto.OrderProductDto;
import com.example.springsecurity.dto.ProductDto;

import java.util.List;

public interface OrderProductService {
    List<OrderProductDto> findOrderProductsByOrderId(Long orderId);
}
