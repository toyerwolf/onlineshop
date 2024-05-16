package com.example.springsecurity.service.impl;


import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;

import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findByUsername(username).
                orElseThrow(()->new NotFoundException("user with username " +username +" not found"));
        UserPrincipal userPrincipal=new UserPrincipal();
        userPrincipal.setUsername(user.getUsername());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setId(user.getId());
        userPrincipal.setLocked(user.isLocked());
        userPrincipal.setRole(user.getRole());

        return userPrincipal;
    }



    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        UserPrincipal userPrincipal=new UserPrincipal();
        userPrincipal.setId(user.getId());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setUsername(user.getUsername());
        userPrincipal.setRole(user.getRole());
        return userPrincipal;

    }

}
