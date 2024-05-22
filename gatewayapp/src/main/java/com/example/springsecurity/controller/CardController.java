package com.example.springsecurity.controller;

import com.example.springsecurity.req.CustomerCardReq;
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


    @Secured({"USER"})
@PostMapping("/{customerId}")
    public ResponseEntity<String> addCardToCustomer(
            @PathVariable Long customerId,
            @RequestBody CustomerCardReq customerCardReq) {
        cardService.addCardToCustomer(customerId, customerCardReq);
        return ResponseEntity.ok("Card added successfully");
    }

}
