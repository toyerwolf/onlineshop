package com.example.springsecurity.feign.dto;

import com.example.springsecurity.service.impl.WeatherService;
import com.sun.tools.javac.Main;
import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherMapResponse {
    private Main main;
    private List<Weather> weather;

    @Data
    public static class Main {
        private double temp;
    }

    @Data
    public static class Weather {
        private String description;
        private String icon;
    }
}
