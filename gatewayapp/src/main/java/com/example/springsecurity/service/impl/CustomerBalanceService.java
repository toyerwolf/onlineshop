package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class CustomerBalanceService  {

    private final CustomerService customerService;

    public void validateCustomerBalance(Customer customer, BigDecimal totalAmount) {
        BigDecimal customerBalance = customer.getBalance();
        if (customerBalance.compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }


    public void decreaseCustomerBalance(Long customerId, BigDecimal amount) {
        customerService.decreaseBalance(customerId, amount);
    }
}
