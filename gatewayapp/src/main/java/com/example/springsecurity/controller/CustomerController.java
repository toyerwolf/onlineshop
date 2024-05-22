package com.example.springsecurity.controller;


import com.example.springsecurity.dto.CustomerDto;
import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.service.CustomerService;
import com.example.springsecurity.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;


    @Secured({"USER"})
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long customerId) {
        CustomerDto customerDto = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customerDto);

    }


    @Secured("ADMIN")
    @GetMapping()
    public ResponseEntity<List<CustomerDto>> getAllCustomers(
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        List<CustomerDto> customersPage = customerService.getAllCustomer(pageNumber, pageSize);
        return ResponseEntity.ok(customersPage);
    }


    @Secured("ADMIN")
    @GetMapping("/searching")
    public List<CustomerDto> searchCustomers(@RequestParam String keyword) {
        return customerService.searchCustomers(keyword);
    }


    @Secured({"USER"})
    @GetMapping("{customerId}/orders")
    public List<OrderDto> getOrdersByCustomerId(@PathVariable Long customerId) {
        return orderService.findOrdersByCustomerID(customerId);
    }

}
