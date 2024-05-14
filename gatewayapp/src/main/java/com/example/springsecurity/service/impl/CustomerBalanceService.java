package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class CustomerBalanceService  {



    private final CustomerRepository customerRepository;

    public void validateCustomerBalance(Customer customer, BigDecimal totalAmount) {
        BigDecimal customerBalance = customer.getBalance();
        if (customerBalance.compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
    }


    public void decreaseBalance(Long customerId, BigDecimal amount) {
        Customer customer=customerRepository.findById(customerId).orElseThrow(()->new NotFoundException("Customer not found"));
        BigDecimal currentBalance = customer.getBalance();
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        BigDecimal newBalance = currentBalance.subtract(amount);
        customer.setBalance(newBalance);
        customerRepository.save(customer);

    }
}
