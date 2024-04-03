package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Role;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.repository.RoleRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.repository.UserRoleRepository;
import com.example.springsecurity.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;



    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            user.setDeleted(true);
            userRepository.save(user);
            userRoleRepository.deleteByUser(user);
        });
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        optionalRole.ifPresent(role -> {
            role.setDeleted(true);
            roleRepository.save(role);
            userRoleRepository.deleteByRole(role);
        });
    }
}

