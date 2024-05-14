package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.AlreadyExistException;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.req.UserRegistrationReq;
import com.example.springsecurity.service.RegistrationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public void register(UserRegistrationReq request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AlreadyExistException("Username is already taken");
        }
       User user=new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(Role.USER);
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setSurname(request.getSurname());
       customer.setRegisteredAt(LocalDateTime.now());
        customer.setAddress(request.getAddress());
//        customer.setBalance(request.getBalance());
        user.setCustomer(customer);
        userRepository.save(user);

    }
}
