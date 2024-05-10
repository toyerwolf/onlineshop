package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerCardDetails;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
@Mock
    private CustomerService customerService;

    @Test
    void testGetCustomerCardById() {
        Customer customer = new Customer();
        customer.setId(1L);
        CustomerCardDetails card1 = new CustomerCardDetails();
        card1.setId(1L);
        CustomerCardDetails card2 = new CustomerCardDetails();
        card2.setId(2L);

        List<CustomerCardDetails> cards = new ArrayList<>();
        cards.add(card1);
        cards.add(card2);
        customer.setCards(cards);
        CustomerService customerService = mock(CustomerService.class);
        when(customerService.getCustomerCardById(customer, 1L)).thenReturn(card1);
        CustomerCardDetails resultCard = customerService.getCustomerCardById(customer, 1L);
        assertEquals(card1, resultCard);
    }

    @Test
    void testGetCustomerCardByIdCardNotFound() {
        Customer customer = new Customer();
        customer.setId(1L);

        List<CustomerCardDetails> cards = new ArrayList<>();
        customer.setCards(cards);


        CustomerService customerService = mock(CustomerService.class);


        when(customerService.getCustomerCardById(customer, 1L))
                .thenThrow(new RuntimeException("Card not found for customer"));


        assertThrows(RuntimeException.class, () -> {
            customerService.getCustomerCardById(customer, 1L);
        });
    }


}

