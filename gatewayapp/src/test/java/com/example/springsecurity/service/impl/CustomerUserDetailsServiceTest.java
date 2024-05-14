package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.securiy.UserPrincipal;
import com.example.springsecurity.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerUserDetailsService customerUserDetailsService;

    @Mock
    private CustomerRepository customerRepository;



    @Test
    void testLoadUserByUsername() {
        // Подготовка данных для теста
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setId(1L);
        user.setLocked(false);
        user.setRole(Role.USER);

        // Мокирование поведения userRepository
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Вызов метода, который тестируем
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

        // Проверка результатов
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getId(), ((UserPrincipal) userDetails).getId());
        assertEquals(user.isLocked(), ((UserPrincipal) userDetails).getLocked());
        assertEquals(user.getRole(), ((UserPrincipal) userDetails).getRole());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Подготовка данных для теста
        String username = "nonexistentuser";

        // Мокирование поведения userRepository
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Проверка генерации исключения UsernameNotFoundException
        assertThrows(
                NotFoundException.class,
                () -> customerUserDetailsService.loadUserByUsername(username)
        );
    }

    @Test
    void testLoadUserById_UserFound() {
        // Подготовка данных для теста
        Long userId = 1L;
        String username = "testUser";
        String password = "password";


        // Мокирование поведения userRepository
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Вызов метода, который тестируем
        CustomerUserDetailsService customerUserDetailsService = new CustomerUserDetailsService(userRepository);
        UserDetails userDetails = customerUserDetailsService.loadUserById(userId);

        // Проверка возвращаемых данных
        assertEquals(username, userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.getId(), ((UserPrincipal) userDetails).getId());
        assertEquals(user.getRole(), ((UserPrincipal) userDetails).getRole());
}


    @Test
    void testLoadUserById_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> customerUserDetailsService.loadUserById(userId)
        );
    }



//    Метод правильно извлекает страницу клиентов из репозитория с правильными параметрами pageNumber и pageSize,
//    Метод правильно преобразует каждого клиента в объект CustomerDto c помощью for.
//     Метод корректно создает объект Page<CustomerDto> с правильными значениями.
    @Test
    void testGetAllCustomer() {
        // Подготовка данных для теста
        int pageNumber = 1;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            Customer customer = new Customer();
            customer.setId((long) (i + 1));
            customer.setName("Customer " + (i + 1));
            customer.setSurname("Surname " + (i + 1));
            customerList.add(customer);
        }
        Page<Customer> customersPage = new PageImpl<>(customerList, pageable, customerList.size());

        when(customerRepository.findAll(any(Pageable.class))).thenReturn(customersPage);

        // Создание сервиса
        CustomerService customerService = new CustomerServiceImpl(customerRepository);

        // Вызов метода, который тестируем
        Page<CustomerDto> customerDtoPage = customerService.getAllCustomer(pageNumber, pageSize);

        // Проверка корректности преобразования клиентов в CustomerDto
        List<CustomerDto> customerDtoList = customerDtoPage.getContent();
        assertEquals(pageSize, customerDtoList.size());
        for (int i = 0; i < pageSize; i++) {
            CustomerDto customerDto = customerDtoList.get(i);
            Customer customer = customerList.get(i);
            assertEquals(customer.getId(), customerDto.getId());
            assertEquals(customer.getName(), customerDto.getName());
            assertEquals(customer.getSurname(), customerDto.getSurname());

        }

        // Проверка корректности создания объекта Page<CustomerDto>
        assertEquals(pageable, customerDtoPage.getPageable());
        assertEquals(customerList.size(), customerDtoPage.getTotalElements());
    }

}