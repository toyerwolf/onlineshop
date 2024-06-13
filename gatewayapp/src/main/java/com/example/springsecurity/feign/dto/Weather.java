package com.example.springsecurity.feign.dto;

import lombok.Data;

@Data
public class Weather {
    private String main;
    private String description;
    private String icon;
}
