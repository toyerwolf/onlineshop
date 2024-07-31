package com.example.springsecurity.controller;

import com.example.springsecurity.req.CustomerCardReq;
import com.example.springsecurity.service.AuthService;
import com.example.springsecurity.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cards")
@AllArgsConstructor
public class CardController {

    private final CardService cardService;
    private final AuthService authService;


    @Secured({"USER"})
    @PostMapping()
    public ResponseEntity<String> addCardToCustomer(@RequestBody CustomerCardReq customerCardReq, @RequestHeader("Authorization") String authHeader) {
        Long customerId = authService.getCustomerIdFromToken(authHeader);
        cardService.addCardToCustomer(customerCardReq, customerId);
        return ResponseEntity.ok("Card added successfully");
    }

}
