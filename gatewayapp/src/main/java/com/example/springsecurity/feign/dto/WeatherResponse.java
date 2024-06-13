package com.example.springsecurity.feign.dto;

import lombok.Data;

@Data
public class WeatherResponse {
    private double temperature;
    private String iconUrl; // Optional, set a default icon URL if not available
    private String description;

}
