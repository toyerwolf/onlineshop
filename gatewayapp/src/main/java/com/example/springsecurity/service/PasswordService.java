package com.example.springsecurity.service;

import com.example.springsecurity.dto.ChangePasswordDto;

public interface PasswordService {

    public String changePassword(ChangePasswordDto changePasswordDto);
}

