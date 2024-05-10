package com.example.springsecurity.controller;

import com.example.springsecurity.dto.SalesStaticDto;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.OrderService;
import com.example.springsecurity.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("products/total-sold")
    public Map<String, Integer> getSoldProductCountsByYear(@RequestParam int year) {
        return productService.countSoldProductsByYear(year);
    }




    @Secured("ADMIN")
    @GetMapping("/product-sales-statistic")
    public ResponseEntity<Map<Integer, Integer>> getProductSalesStatistics() {
        Map<Integer, Integer> salesByYear = productService.getProductSalesStatistics();
        return ResponseEntity.ok(salesByYear);
    }


    @Secured("ADMIN")
    @GetMapping("/totalRevenueByYear")
    public ResponseEntity<Map<Integer, BigDecimal>> getTotalProductSalesRevenue() {
        Map<Integer, BigDecimal> totalRevenueByYear = productService.getTotalProductSalesRevenueByYear();
        return ResponseEntity.ok(totalRevenueByYear);
    }


    @Secured("ADMIN")
    @GetMapping("/customer-registrations")
    public ResponseEntity<Map<Integer, Long>> getCustomerRegistrationsByYear() {
        Map<Integer, Long> customerRegistrationsByYear = customerService.getCustomerRegistrationsByYear();
        return ResponseEntity.ok(customerRegistrationsByYear);
    }


}
