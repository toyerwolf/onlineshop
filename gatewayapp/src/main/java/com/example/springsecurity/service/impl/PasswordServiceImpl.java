package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.ChangePasswordDto;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.PasswordService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@AllArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Override
    public String changePassword(ChangePasswordDto changePasswordDto) {
        String username = changePasswordDto.getUsername();
        String oldPassword = changePasswordDto.getOldPassword();
        String newPassword = changePasswordDto.getNewPassword();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Username not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordCreation(LocalDateTime.now());
        user.setPasswordExpiration(user.getPasswordCreation().plusMonths(3));

        userRepository.save(user);
        return "Success";
    }
}
