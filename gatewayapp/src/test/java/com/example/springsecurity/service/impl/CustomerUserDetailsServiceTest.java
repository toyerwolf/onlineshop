package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.security.UserPrincipal;
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

}

