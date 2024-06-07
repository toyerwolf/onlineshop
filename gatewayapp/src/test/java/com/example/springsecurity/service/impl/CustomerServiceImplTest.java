package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.dto.CustomerRegistrationDTO;
import com.example.springsecurity.dto.CustomerRegistrationsByYearResponseDTO;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.CustomerCardDetails;
import com.example.springsecurity.exception.InsufficientBalanceException;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.mapper.CustomerMapper;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
private CustomerRepository customerRepository;

    @Mock
    Customer customer;

    @Mock
    private CustomerMapper customerMapper;






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


    @Test
    void testGetCustomerById_CustomerFound() {
        // Подготовка данных для теста
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Huseyn");
        customer.setSurname("Mamedov");


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));


        CustomerService customerService = new CustomerServiceImpl(customerRepository);


        CustomerDto customerDto = customerService.getCustomerById(customerId);


        assertEquals(customer.getId(), customerDto.getId());
        assertEquals(customer.getName(), customerDto.getName());
        assertEquals(customer.getSurname(), customerDto.getSurname());
    }

    @Test
    void testGetCustomerById_CustomerNotFound() {
        Long customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        CustomerService customerService = new CustomerServiceImpl(customerRepository);
        assertThrows(NotFoundException.class, () -> customerService.getCustomerById(customerId));
    }

    @Test
    void testGetCustomerRegistrationsByYear() {

        List<Object[]> registrationData = new ArrayList<>();
        registrationData.add(new Object[]{Timestamp.valueOf("2021-01-01 00:00:00"), 100L});
        registrationData.add(new Object[]{Timestamp.valueOf("2022-01-01 00:00:00"), 150L});
        registrationData.add(new Object[]{Timestamp.valueOf("2023-01-01 00:00:00"), 200L});

        CustomerRepository customerRepository = mock(CustomerRepository.class);
        when(customerRepository.getCustomerRegistrationsByYear()).thenReturn(registrationData);

        CustomerService customerService = new CustomerServiceImpl(customerRepository);


        CustomerRegistrationsByYearResponseDTO responseDTO = customerService.getCustomerRegistrationsByYear();


        assertNotNull(responseDTO);
        List<CustomerRegistrationDTO> registrationsByYear = responseDTO.getRegistrationsByYear();
        assertNotNull(registrationsByYear);
        assertFalse(registrationsByYear.isEmpty());
        assertEquals(3, registrationsByYear.size());


        assertEquals(100L, registrationsByYear.get(0).getRegistrations());
        assertEquals(150L, registrationsByYear.get(1).getRegistrations());
        assertEquals(200L, registrationsByYear.get(2).getRegistrations());
        assertEquals(2021, registrationsByYear.get(0).getYear());
        assertEquals(2022, registrationsByYear.get(1).getYear());
        assertEquals(2023, registrationsByYear.get(2).getYear());
    }

    @Test
    void testSearchCustomers() {

        String keyword = "Huseyn";
        List<Customer> searchResults = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("Huseyn Testov");
        customer1.setBalance(BigDecimal.valueOf(1000.0));

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Huseyn Mamedov");
        customer2.setBalance(BigDecimal.valueOf(2000.0));

        searchResults.add(customer1);
        searchResults.add(customer2);


        when(customerRepository.searchCustomers(keyword)).thenReturn(searchResults);

        CustomerService customerService = new CustomerServiceImpl(customerRepository);


        List<CustomerDto> result = customerService.searchCustomers(keyword);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Huseyn Testov", result.get(0).getName());
        assertEquals(BigDecimal.valueOf(1000.0), result.get(0).getBalance());
        assertEquals("Huseyn Mamedov", result.get(1).getName());
        assertEquals(BigDecimal.valueOf(2000.0), result.get(1).getBalance());
    }


    @Test
    void testGetCustomerCardById_CardExists() {

        Long cardId = 1L;
        CustomerCardDetails expectedCard = new CustomerCardDetails();
        expectedCard.setId(cardId);
        expectedCard.setCardNumber("1234567890123456");
        expectedCard.setExpirationDate(LocalDate.now().plusYears(1));
        expectedCard.setCvv("123");
        expectedCard.setCardBalance(BigDecimal.valueOf(1000.00));
        expectedCard.setCustomer(customer);

        List<CustomerCardDetails> cards = new ArrayList<>();
        cards.add(expectedCard);
        when(customer.getCards()).thenReturn(cards);

        CustomerService customerService = new CustomerServiceImpl(customerRepository);
        CustomerCardDetails resultCard = customerService.getCustomerCardById(customer, cardId);

        assertEquals(expectedCard, resultCard);
    }
    @Test
    void testGetCustomerCardById_CardNotFound() {

        Long cardId = 1L;


        List<CustomerCardDetails> cards = new ArrayList<>();
        when(customer.getCards()).thenReturn(cards);
        CustomerService customerService = new CustomerServiceImpl(customerRepository);

        assertThrows(NotFoundException.class, () -> customerService.getCustomerCardById(customer, cardId));
    }

    @Test
    void testGetAllCustomer() {

        CustomerRepository customerRepository = mock(CustomerRepository.class);
        CustomerService customerService = new CustomerServiceImpl(customerRepository);

        Sort sort = Sort.unsorted();
        int pageNumber = 1;
        int pageSize = 10;

        Pageable pageable = PageRequest.of(0, pageSize, sort);
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            Customer customer = new Customer();
            customer.setId((long) (i + 1));
            customer.setName("Customer " + (i + 1));
            customer.setSurname("Surname " + (i + 1));
            customerList.add(customer);
        }
        Page<Customer> customersPage = new PageImpl<>(customerList, pageable, customerList.size());
        when(customerRepository.findAll(pageable)).thenReturn(customersPage);

        List<CustomerDto> customerDtoPage = customerService.getAllCustomer(pageNumber, pageSize);


        assertEquals(pageSize, customerDtoPage.size());
        for (int i = 0; i < pageSize; i++) {
            CustomerDto customerDto = customerDtoPage.get(i);
            Customer customer = customerList.get(i);
            assertEquals(customer.getId(), customerDto.getId());
            assertEquals(customer.getName(), customerDto.getName());
            assertEquals(customer.getSurname(), customerDto.getSurname());
        }
    }

    @Test
    void testGetCustomerByUsername_Found() {
        // Arrange
        String username = "john_doe";
        Customer customer = new Customer();
        customer.setId(1L);

        CustomerService customerService = new CustomerServiceImpl(customerRepository);

        Mockito.when(customerRepository.findByUser_Username(username))
                .thenReturn(Optional.of(customer));

        // Act
        CustomerDto customerDto = customerService.getCustomerByUsername(username);

        // Assert
        Assertions.assertNotNull(customerDto);
        Assertions.assertEquals(customer.getId(), customerDto.getId());

    }

    @Test
    void testGetCustomerByUsername_NotFound() {
        // Arrange
        String username = "non_existing_user";

        Mockito.when(customerRepository.findByUser_Username(username))
                .thenReturn(Optional.empty());

        CustomerService customerService = new CustomerServiceImpl(customerRepository);
        Assertions.assertThrows(NotFoundException.class, () -> customerService.getCustomerByUsername(username));
    }
}









