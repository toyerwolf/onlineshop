//package com.example.springsecurity.util;
//
//import com.example.springsecurity.entity.Customer;
//import com.example.springsecurity.exception.NotFoundException;
//import com.example.springsecurity.repository.CustomerRepository;
//import com.example.springsecurity.service.CustomerService;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Component;
//
//@Component
//@AllArgsConstructor
//public class CustomerUtils {
//    private static CustomerRepository customerRepository;
//
//    public  static Customer findCustomerById(Long customerId) {
//        return customerRepository.findById(customerId)
//                .orElseThrow(() -> new NotFoundException("Customer not found"));
//    }
//}
