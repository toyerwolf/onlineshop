package com.example.springsecurity.service;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.req.PaymentRequest;

import java.math.BigDecimal;

public interface PaymentService {

    void processPaymentWithCard(PaymentRequest paymentRequest);

    void processPaymentWithPayPal(Long customerId,Long orderId);




}
