package com.example.springsecurity.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter(AccessLevel.PUBLIC)
@Setter
public class CategoryDto {

    private Long categoryId;
    private String name;
    private String description;
    private boolean deleted;
//    List<ProductDto> productDtoList;

}
