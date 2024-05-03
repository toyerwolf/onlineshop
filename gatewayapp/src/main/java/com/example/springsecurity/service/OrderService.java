package com.example.springsecurity.service;

import com.example.springsecurity.dto.OrderDto;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.dto.SalesStaticDto;
import com.example.springsecurity.req.OrderRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface OrderService {

    void makeOrder(Long customerId,OrderRequest orderRequest);

    void markOrderAsDelivered(Long orderId);

    List<ProductDto> findProductsByOrderId(Long orderId);

    List<OrderDto> getAllOrders();

    Map<String, Integer> countSoldProductsByYear(int year);

    Map<Integer, Integer> getProductSalesStatistics();

    Map<Integer, BigDecimal> getTotalProductSalesRevenueByYear();

    List<OrderDto> findOrdersByCustomerID(Long customerId);
}
