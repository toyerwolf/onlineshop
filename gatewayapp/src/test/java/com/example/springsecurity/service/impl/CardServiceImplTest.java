package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerCardDetails;
import com.example.springsecurity.exception.AlreadyExistException;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.repository.CustomerCardDetailsRepository;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.req.CustomerCardReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerCardDetailsRepository cardDetailsRepository;

    @InjectMocks
    @Spy
    private CardServiceImpl cardService;


    @Test
    void testAddCardToCustomer() {

        Long customerId = 1L;
        CustomerCardReq customerCardReq = new CustomerCardReq();
        customerCardReq.setCardNumber("1234567890123456");
        customerCardReq.setCardBalance(BigDecimal.valueOf(1000.0));
        customerCardReq.setCvv("333");

        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(cardDetailsRepository.findByCardNumber(any())).thenReturn(Optional.empty());
        when(cardService.isCardNumberUnique(anyString())).thenReturn(true);


        assertDoesNotThrow(() -> cardService.addCardToCustomer(customerId, customerCardReq));

        verify(customerRepository).findById(customerId);

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testAddCardToCustomer_CardAlreadyExists() {

        Long customerId = 1L;
        CustomerCardReq customerCardReq = new CustomerCardReq();
        customerCardReq.setCardNumber("1234567890123456");
        customerCardReq.setCardBalance(BigDecimal.valueOf(1000.0));
        customerCardReq.setCvv("333");

        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(cardDetailsRepository.findByCardNumber(any())).thenReturn(Optional.of(new CustomerCardDetails()));


        assertThrows(AlreadyExistException.class, () -> cardService.addCardToCustomer(customerId, customerCardReq));


        verify(customerRepository).findById(customerId);
    }


    @Test
    void testValidateCardBalance_InsufficientBalance() {

        CustomerCardDetails card = new CustomerCardDetails();
        card.setCardBalance(BigDecimal.valueOf(50.0));
        BigDecimal totalAmount = BigDecimal.valueOf(100.0);


        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> cardService.validateCardBalance(card, totalAmount));

        assertEquals("Insufficient balance on card", exception.getMessage());
    }

    @Test
    void testValidateCardBalance_SufficientBalance() {
        // Prepare test data
        CustomerCardDetails card = new CustomerCardDetails();
        card.setCardBalance(BigDecimal.valueOf(150.0)); // Setting card balance higher than the total amount
        BigDecimal totalAmount = BigDecimal.valueOf(100.0); // Total amount lower than the card balance

        // Verify that the method does not throw any exception
        assertDoesNotThrow(() -> cardService.validateCardBalance(card, totalAmount));
    }

    @Test
    void testIsCardExpired() {
        LocalDate expirationDate = LocalDate.of(2023, 5, 14);
        boolean isExpired = cardService.isCardExpired(expirationDate);
        assertTrue(isExpired);
    }

    @Test
    void testIsCardNotExpired() {
        LocalDate expirationDate = LocalDate.now().plusYears(1);
        boolean isExpired = cardService.isCardExpired(expirationDate);
        assertFalse(isExpired);
    }

    @Test
    void testIsCardNumberUnique_UniqueCardNumber() {
        when(cardDetailsRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        assertTrue(cardService.isCardNumberUnique("1234567890123456"));
    }

    @Test
    void testIsCardNumberUnique_NonUniqueCardNumber() {
        when(cardDetailsRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(new CustomerCardDetails()));
        assertFalse(cardService.isCardNumberUnique("1234567890123456"));
    }

    @Test
    void testDecreaseFromCardBalance_EnoughBalance() {
        CustomerCardDetails card = new CustomerCardDetails();
        card.setCardBalance(BigDecimal.valueOf(1000));
        BigDecimal amount = BigDecimal.valueOf(500);
        Mockito.lenient().when(cardDetailsRepository.findById(any())).thenReturn(Optional.of(card));
        cardService.decreaseFromCardBalance(card, amount);

        assertEquals(BigDecimal.valueOf(500), card.getCardBalance());
    }

    @Test
    void testDecreaseFromCardBalance_InsufficientBalance() {
        CustomerCardDetails card = new CustomerCardDetails();
        card.setCardBalance(BigDecimal.valueOf(100));
        BigDecimal amount = BigDecimal.valueOf(500);
        Mockito.lenient().when(cardDetailsRepository.findById(any())).thenReturn(Optional.of(card));
        assertThrows(InsufficientBalanceException.class, () -> cardService.decreaseFromCardBalance(card, amount));
    }
}




