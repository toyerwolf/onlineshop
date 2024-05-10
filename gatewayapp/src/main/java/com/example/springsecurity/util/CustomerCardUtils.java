//package com.example.springsecurity.util;
//
//import com.example.springsecurity.entity.Customer;
//import com.example.springsecurity.entity.CustomerCardDetails;
//import com.example.springsecurity.exception.NotFoundException;
//import com.example.springsecurity.repository.CustomerCardDetailsRepository;
//import com.example.springsecurity.service.CustomerService;
//import jakarta.persistence.Column;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//
//public class CustomerCardUtils {
//
//    private static CustomerService customerService;
//
//    public static void setCustomerService(CustomerService service) {
//        customerService = service;
//    }
//
//    public static CustomerCardDetails getCustomerCardOrThrow(Customer customer, Long cardId) {
//        CustomerCardDetails card = customerService.getCustomerCardById(customer, cardId);
//        if (card != null) {
//            return card;
//        } else {
//            throw new NotFoundException("Customer card details not found");
//        }
//    }
//}
