package com.example.springsecurity.controller;

import com.example.springsecurity.dto.DiceRollResult;
import com.example.springsecurity.service.CustomerDiscountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DiceController {

    private final CustomerDiscountService customerDiscountService;

    @PostMapping("/roll-dice")
    @Secured("USER")
    public ResponseEntity<DiceRollResult> rollDice(@RequestParam Long customerId,
                                                   @RequestParam Long productId) {
        DiceRollResult rollResult = customerDiscountService.applyDiscountIfDiceRollsAreSix(customerId, productId);
        return ResponseEntity.ok(rollResult);
    }
}