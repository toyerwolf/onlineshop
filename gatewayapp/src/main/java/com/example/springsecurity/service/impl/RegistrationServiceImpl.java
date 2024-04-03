package com.example.springsecurity.service.impl;


import com.example.springsecurity.dto.RoleDto;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.CustomerAlreadyExist;

import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.req.UserReq;
import com.example.springsecurity.service.RegistrationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {


private final UserRepository userRepository;

private final PasswordEncoder passwordEncoder;


@Transactional
    @Override
    public void register(UserReq userReq) {
        if (userRepository.existsByUsername(userReq.getUsername())) {
            throw new CustomerAlreadyExist("Username is already taken");
        }

        if (userRepository.existsByEmail(userReq.getEmail())) {
            throw new CustomerAlreadyExist("Email is already in use");
        }

        User newUser=new User();
        newUser.setUsername(userReq.getUsername());
        newUser.setEmail(userReq.getEmail());
        String hashedPassword = passwordEncoder.encode(userReq.getPassword());
        newUser.setPassword(hashedPassword);
        newUser.setBirthday(userReq.getBirthday());
        newUser.setName(userReq.getName());
        newUser.setSurname(userReq.getSurname());
        newUser.setPhoneNumber(userReq.getPhoneNumber());
        Set<Role> roles=new HashSet<>();
        newUser.setLocked(userReq.getLocked());
        for(RoleDto roleDto:userReq.getRoleDtos()){
            Role role=new Role();
            role.setRole(roleDto.getRole());
            roles.add(role);
        }
        newUser.setRoles(roles);
        userRepository.save(newUser);
    }

    }





