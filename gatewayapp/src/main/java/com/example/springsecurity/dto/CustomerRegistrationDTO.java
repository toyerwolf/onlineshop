package com.example.springsecurity.dto;

import jakarta.persistence.Access;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistrationDTO {
    private Integer year;
    private Long registrations;
}
