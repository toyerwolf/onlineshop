package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.exception.PasswordExpiredException;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.securiy.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {



    private  final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findByUsername(username).
                orElseThrow(()->new NotFoundException("user with username " +username +" not found"));
        if (user.getPasswordExpiration() != null && user.getPasswordExpiration().isBefore(LocalDateTime.now())) {
            throw new PasswordExpiredException("Password has expired");
        }
        UserPrincipal userPrincipal=new UserPrincipal();
        userPrincipal.setUsername(user.getUsername());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setId(user.getId());
        userPrincipal.setLocked(user.getLocked());
        Set<Role> roles=user.getRoles();
        List<GrantedAuthority> authorities=roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
        userPrincipal.setAuthorities(authorities);
        return userPrincipal;
    }



    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        UserPrincipal userPrincipal=new UserPrincipal();
        userPrincipal.setId(user.getId());
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setUsername(user.getUsername());
        Set<Role> roles=user.getRoles();
        List<GrantedAuthority> authorities=roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
        userPrincipal.setAuthorities(authorities);
        return userPrincipal;

    }

}