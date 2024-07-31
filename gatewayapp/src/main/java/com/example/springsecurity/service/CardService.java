package com.example.springsecurity.service;

import com.example.springsecurity.entity.CustomerCardDetails;
import com.example.springsecurity.req.CustomerCardReq;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CardService {

    public void addCardToCustomer(CustomerCardReq customerCardReq, Long customerId);
    void validateCardBalance(CustomerCardDetails card, BigDecimal totalAmount);

    boolean isCardExpired(LocalDate expirationDate);
    void decreaseFromCardBalance(CustomerCardDetails card, BigDecimal amount);


}
