package com.example.springsecurity.service;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.entity.Customer;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustomerService {

    void decreaseBalance(Long userId, BigDecimal amount);

    CustomerDto getCustomerById(Long customerID);

    Customer findCustomerById(Long customerId);

    Page<CustomerDto> getAllCustomer(int pageNumber,int pageSize);

    Map<Integer, Long> getCustomerRegistrationsByYear();
}
