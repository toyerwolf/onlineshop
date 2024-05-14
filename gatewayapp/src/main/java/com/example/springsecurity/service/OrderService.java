package com.example.springsecurity.service;

import com.example.springsecurity.dto.OrderDto;

import com.example.springsecurity.dto.OrderResponse;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.dto.SalesStaticDto;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.req.OrderRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
//tested

public interface OrderService {

    OrderResponse makeOrder(Long customerId, OrderRequest orderRequest);

    void markOrderAsDelivered(Long orderId);

    List<OrderDto> getAllOrders();

    List<OrderDto> findOrdersByCustomerID(Long customerId);
    OrderResponse makeOrderWithCard(Long customerId, OrderRequest orderRequest, Long cardId);


}
