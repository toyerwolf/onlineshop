package com.example.springsecurity.service;

import com.example.springsecurity.entity.User;
import com.example.springsecurity.req.UserRegistrationReq;

public interface RegistrationService {

    void register(UserRegistrationReq request);
}
