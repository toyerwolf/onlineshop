package com.example.springsecurity.service.impl;

import com.example.springsecurity.feign.dto.OpenWeatherMapResponse;
import com.example.springsecurity.feign.dto.WeatherResponse;
import com.example.springsecurity.feign.WeatherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service

public class WeatherService {


    private final WeatherClient weatherClient;

    @Value("${weatherperson.apiKey}")
    private String apiKey;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

//    public WeatherResponse getWeather(String city) {
//        return weatherClient.getWeather(city, apiKey, "metric");
//    }


    public WeatherResponse getWeather(String city) {
        OpenWeatherMapResponse weatherResponse = weatherClient.getWeather(city, apiKey, "metric");

        WeatherResponse response = new WeatherResponse();
        response.setTemperature(weatherResponse.getMain().getTemp());

        if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
            String iconCode = weatherResponse.getWeather().get(0).getIcon();
            response.setIconUrl("http://openweathermap.org/img/wn/" + iconCode + ".png"); // Изменено на .png
            response.setDescription(weatherResponse.getWeather().get(0).getDescription());
        } else {
            response.setIconUrl("default-icon-url");
            response.setDescription("Описание недоступно");
        }

        return response;
    }
}



