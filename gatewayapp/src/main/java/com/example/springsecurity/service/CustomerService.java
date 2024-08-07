package com.example.springsecurity.service;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.dto.CustomerRegistrationsByYearResponseDTO;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerCardDetails;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    CustomerDto getCustomerByUsername(String username);

    CustomerDto getCustomerById(Long customerID);

//    Customer findCustomerById(Long customerId);

    List<CustomerDto> getAllCustomer(int pageNumber,int pageSize);

   CustomerRegistrationsByYearResponseDTO getCustomerRegistrationsByYear();

     List<CustomerDto> searchCustomers(String keyword);

    CustomerCardDetails getCustomerCardById(Customer customer, Long cardId);
}
