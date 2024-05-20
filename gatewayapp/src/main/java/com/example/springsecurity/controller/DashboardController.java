package com.example.springsecurity.controller;

import com.example.springsecurity.dto.*;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.OrderService;
import com.example.springsecurity.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dashboard")
@AllArgsConstructor
public class DashboardController {

    private final ProductService productService;
private final CustomerService customerService;

    @Secured("ADMIN")
    @GetMapping("/products/total-sold")
    @ResponseBody
    public ProductSalesResponseDto getSoldProductCountsByYear(@RequestParam int year) {
        return productService.countSoldProductsByYear(year);
    }




    @Secured("ADMIN")
    @GetMapping("/products/sales-statistics")
    @ResponseBody
    public YearlySalesResponseDto getProductSalesStatistics() {
        return productService.getProductSalesStatistics();
    }



    @Secured("ADMIN")
    @GetMapping("/totalRevenueByYear")
    public ResponseEntity<YearlySalesRevenueResponseDTO> getTotalProductSalesRevenueByYear() {
        YearlySalesRevenueResponseDTO responseDTO = productService.getTotalProductSalesRevenueByYear();
        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("customer/registrations")
    public ResponseEntity<CustomerRegistrationsByYearResponseDTO> getCustomerRegistrationsByYear() {
        CustomerRegistrationsByYearResponseDTO responseDTO = customerService.getCustomerRegistrationsByYear();
        return ResponseEntity.ok(responseDTO);
    }


}
