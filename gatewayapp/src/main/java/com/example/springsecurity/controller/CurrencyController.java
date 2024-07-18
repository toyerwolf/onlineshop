package com.example.springsecurity.controller;

import com.example.springsecurity.feign.dto.CurrencyResponse;
import com.example.springsecurity.service.impl.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currencies")
@RequiredArgsConstructor
public class CurrencyController {
    private final CurrencyService currencyService;



    @GetMapping("/current")
    public ResponseEntity<CurrencyResponse> getCurrentCurrencies(@RequestParam(defaultValue = "USD") String base) {
        CurrencyResponse currencies = currencyService.getCurrentCurrencies(base);
        return ResponseEntity.ok(currencies);
    }
}