package com.example.springsecurity.projection;

import org.springframework.beans.factory.annotation.Value;

public interface CustomerRegistrationsProjection {
    @Value("#{target.registration_year}")
    Integer getRegistrationYear();

    @Value("#{target.customer_registrations}")
    Long getCustomerRegistrations();
}