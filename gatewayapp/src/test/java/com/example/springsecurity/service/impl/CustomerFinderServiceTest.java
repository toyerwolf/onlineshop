package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



    @ExtendWith(MockitoExtension.class)
    class CustomerFinderServiceTest {

        @Mock
        private CustomerRepository customerRepository;

        @InjectMocks
        private CustomerFinderService customerFinderService;



        @Test
        void testFindCustomerById_Success() {
            Long customerId = 1L;
            Customer customer = new Customer();
            customer.setId(customerId);

            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            Customer foundCustomer = customerFinderService.findCustomerById(customerId);

            assertEquals(customer, foundCustomer);
            verify(customerRepository).findById(customerId);
        }

        @Test
        void testFindCustomerById_CustomerNotFound() {
            Long customerId = 1L;

            when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> customerFinderService.findCustomerById(customerId));
            verify(customerRepository).findById(customerId);
        }
    }


