package com.example.springsecurity.front;

import com.example.springsecurity.dto.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DashboardGraphComponent {

    public List<Object> getProductSalesData(YearlySalesResponseDto yearlySalesResponseDto) {
        List<YearlyProductSalesDTO> yearlySales = yearlySalesResponseDto.getYearlySales();
        List<Integer> labels = yearlySales.stream()
                .map(YearlyProductSalesDTO::getYear)
                .toList();
        List<Integer> data = yearlySales.stream()
                .map(YearlyProductSalesDTO::getTotalSold)
                .toList();
        return List.of(labels, data);
    }

    public List<Object> getSalesRevenueData(YearlySalesRevenueResponseDTO yearlySalesRevenueResponseDTO) {
        List<YearlySalesRevenueDTO> yearlySales = yearlySalesRevenueResponseDTO.getYearlySales();
        List<Integer> labels = yearlySales.stream()
                .map(YearlySalesRevenueDTO::getYear)
                .toList();
        List<BigDecimal> data = yearlySales.stream()
                .map(YearlySalesRevenueDTO::getTotalRevenue)
                .toList();
        return List.of(labels, data);
    }

    public List<Object> getCustomerRegistrationsData(CustomerRegistrationsByYearResponseDTO customerRegistrationsByYearResponseDTO) {
        List<CustomerRegistrationDTO> registrationsByYear = customerRegistrationsByYearResponseDTO.getRegistrationsByYear();
        List<Integer> labels = registrationsByYear.stream()
                .map(CustomerRegistrationDTO::getYear)
                .toList();
        List<Long> data = registrationsByYear.stream()
                .map(CustomerRegistrationDTO::getRegistrations)
                .toList();
        return List.of(labels, data);
    }
}
