package com.example.springsecurity.controller;

import com.example.springsecurity.req.PaymentRequest;
import com.example.springsecurity.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("payments")
@AllArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;


    @PostMapping
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) {
        paymentService.processPaymentWithCard(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Order with card created");
    }

    @PostMapping("/paypal/{customerId}/{orderId}")
    public ResponseEntity<String> processPaymentWithPayPal(@PathVariable Long customerId, @PathVariable Long orderId) {
        paymentService.processPaymentWithPayPal(customerId, orderId);
        return ResponseEntity.ok("Payment with PayPal processed successfully.");
    }
}
