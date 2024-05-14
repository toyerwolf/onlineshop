package com.example.springsecurity.controller;

import com.example.springsecurity.dto.OrderDto;
import com.example.springsecurity.dto.OrderResponse;
import com.example.springsecurity.dto.ProductDto;
import com.example.springsecurity.entity.Order;
import com.example.springsecurity.req.OrderRequest;
import com.example.springsecurity.service.OrderService;
import com.example.springsecurity.service.ProductService;
import com.example.springsecurity.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ProductService productService;

    @PostMapping("{customerId}")
    public ResponseEntity<OrderResponse> makeOrder(@PathVariable("customerId")Long customerId,@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse=orderService.makeOrder(customerId,orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }


    @Secured("ADMIN")
    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<String> markOrderAsDelivered(@PathVariable Long orderId) {
        orderService.markOrderAsDelivered(orderId);
        return ResponseEntity.ok("Order marked as delivered successfully.");
    }

    @GetMapping("/{orderId}/products")
    public ResponseEntity<List<ProductDto>> getProductsByOrderId(@PathVariable Long orderId) {
        List<ProductDto> products = productService.findProductsByOrderId(orderId);
        return ResponseEntity.ok(products);
    }

    @GetMapping()
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderDto> getOrdersByCustomerId(@PathVariable Long customerId) {
        return orderService.findOrdersByCustomerID(customerId);
    }

    @PostMapping("/{customerId}/{cardId}")
    public ResponseEntity<OrderResponse> makeOrderWithCard(@PathVariable Long customerId,
                                                           @PathVariable Long cardId,
                                                           @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.makeOrderWithCard(customerId, orderRequest, cardId);
        return ResponseEntity.ok(orderResponse);
    }
}



