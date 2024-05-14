package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.AlreadyExistException;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.req.UserRegistrationReq;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void testRegister_NewUser() {
        // Arrange
        UserRegistrationReq request = new UserRegistrationReq();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setName("Huseyn");
        request.setSurname("Mammedov");
        request.setAddress("123 Main St");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act
        registrationService.register(request);

        // Assert
        verify(userRepository).findByUsername("newuser");
        verify(passwordEncoder).encode("password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertFalse(savedUser.isLocked());
        assertEquals(Role.USER, savedUser.getRole());
        assertNotNull(savedUser.getCreatedAt());

        Customer savedCustomer = savedUser.getCustomer();
        assertNotNull(savedCustomer);
        assertEquals("Huseyn", savedCustomer.getName());
        assertEquals("Mammedov", savedCustomer.getSurname());
        assertEquals("123 Main St", savedCustomer.getAddress());
        assertNotNull(savedCustomer.getRegisteredAt());
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange
        UserRegistrationReq request = new UserRegistrationReq();
        request.setUsername("existinguser");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        // Assert
        assertThrows(AlreadyExistException.class, () -> registrationService.register(request));
        verify(userRepository).findByUsername("existinguser");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}

