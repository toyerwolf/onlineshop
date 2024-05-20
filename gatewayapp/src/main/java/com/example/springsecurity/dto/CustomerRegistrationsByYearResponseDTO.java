package com.example.springsecurity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CustomerRegistrationsByYearResponseDTO {
    private List<CustomerRegistrationDTO> registrationsByYear;
}
