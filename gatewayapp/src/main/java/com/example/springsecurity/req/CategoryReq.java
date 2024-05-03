package com.example.springsecurity.req;

import lombok.Data;

@Data
public class CategoryReq {
    private Long categoryId;
    private String name;
    private String description;
    private boolean isDeleted;
}
