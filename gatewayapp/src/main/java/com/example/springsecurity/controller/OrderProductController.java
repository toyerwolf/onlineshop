package com.example.springsecurity.controller;

import com.example.springsecurity.dto.OrderProductDto;
import com.example.springsecurity.dto.OrderProductsResponse;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.service.OrderProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order-products")
@AllArgsConstructor
public class OrderProductController {

    private final OrderProductService orderProductService;



    @Secured("USER")
    @GetMapping("/{orderId}/products")
    public ResponseEntity<OrderProductsResponse> findOrderProductsByOrderId(@PathVariable Long orderId) {
        List<OrderProductDto> products = orderProductService.findOrderProductsByOrderId(orderId);
        return ResponseEntity.ok(new OrderProductsResponse(products));
    }

}
