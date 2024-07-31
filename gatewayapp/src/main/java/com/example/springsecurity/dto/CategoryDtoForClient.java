package com.example.springsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDtoForClient {
    private Long categoryId;
    private String name;
}
