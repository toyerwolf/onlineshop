package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerBalanceServiceTest {

    @Mock
    private CustomerRepository customerRepository;



    @Test
    void testValidateCustomerBalance_InsufficientBalance() {
        // Prepare test data
        BigDecimal customerBalance = BigDecimal.valueOf(500.00);
        BigDecimal totalAmount = BigDecimal.valueOf(1000.00);


        Customer customer = mock(Customer.class);
        when(customer.getBalance()).thenReturn(customerBalance);
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        CustomerBalanceService customerBalanceService = new CustomerBalanceService(customerRepository);


        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class,
                () -> customerBalanceService.validateCustomerBalance(customer, totalAmount));


        assertEquals("Insufficient balance", exception.getMessage());
    }




    @Test
    void testDecreaseBalance_SufficientBalance() {
        // Подготовка данных для теста
        Long customerId = 1L;
        BigDecimal currentBalance = new BigDecimal("100.00");
        BigDecimal amountToDecrease = new BigDecimal("50.00");
        BigDecimal expectedNewBalance = new BigDecimal("50.00");
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setBalance(currentBalance);

        // Мокирование поведения customerRepository
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Создание сервиса
        CustomerBalanceService customerBalanceService = new CustomerBalanceService(customerRepository);

        // Вызов метода, который тестируем
        customerBalanceService.decreaseBalance(customerId, amountToDecrease);

        // Проверка обновления баланса клиента
        verify(customerRepository).save(customer);
        assertEquals(expectedNewBalance, customer.getBalance());
    }

    @Test
    void testDecreaseBalance_InsufficientBalance() {
        // Подготовка данных для теста
        Long customerId = 1L;
        BigDecimal currentBalance = new BigDecimal("100.00");
        BigDecimal amountToDecrease = new BigDecimal("150.00");
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setBalance(currentBalance);


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerBalanceService customerBalanceService = new CustomerBalanceService(customerRepository);

        assertThrows(InsufficientBalanceException.class, () -> customerBalanceService.decreaseBalance(customerId, amountToDecrease));
    }

}