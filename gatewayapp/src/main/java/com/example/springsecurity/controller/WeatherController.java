package com.example.springsecurity.controller;

import com.example.springsecurity.feign.dto.WeatherResponse;
import com.example.springsecurity.service.impl.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;



//    @GetMapping("/weather")
//    public WeatherResponse getWeather(@RequestParam String city) {
//        return weatherService.getWeather(city);
//
//    }

    @GetMapping("/weather")
    public WeatherResponse getWeatherForBaku() {
        String city = "Baku";
        return weatherService.getWeather(city);
    }
}
