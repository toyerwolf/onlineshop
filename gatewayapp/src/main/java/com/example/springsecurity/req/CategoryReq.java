package com.example.springsecurity.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CategoryReq {
    private Long categoryId;

    @NotNull
    @NotEmpty
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Description must contain only letters and numbers")
    private String description;

    private boolean isDeleted;
}
