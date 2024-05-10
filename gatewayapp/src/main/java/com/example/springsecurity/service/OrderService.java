package com.example.springsecurity.service;

import com.example.springsecurity.dto.OrderDto;

import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.dto.SalesStaticDto;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.req.OrderRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
//tested

public interface OrderService {

    void makeOrder(Long customerId,OrderRequest orderRequest);

    void markOrderAsDelivered(Long orderId);

    List<OrderDto> getAllOrders();

    List<OrderDto> findOrdersByCustomerID(Long customerId);
    Order makeOrderWithCard(Long customerId, OrderRequest orderRequest, Long cardId);


}
