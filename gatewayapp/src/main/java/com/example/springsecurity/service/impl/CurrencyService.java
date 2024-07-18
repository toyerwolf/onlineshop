package com.example.springsecurity.service.impl;

import com.example.springsecurity.feign.CurrencyClient;
import com.example.springsecurity.feign.dto.CurrencyResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CurrencyService {


    private final CurrencyClient currencyClient;
    private final List<String> selectedCurrencies = List.of("USD", "EUR", "GBP", "RUB", "TRY", "AZN");


    public CurrencyResponse getCurrentCurrencies(String base) {
        CurrencyResponse allCurrencies = currencyClient.getCurrencies(base);
        Map<String, Double> filteredRates = allCurrencies.getRates().entrySet().stream()
                .filter(entry -> selectedCurrencies.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        CurrencyResponse filteredResponse = new CurrencyResponse();
        filteredResponse.setBase(allCurrencies.getBase());
        filteredResponse.setDate(allCurrencies.getDate());
        filteredResponse.setRates(filteredRates);

        return filteredResponse;
    }
}