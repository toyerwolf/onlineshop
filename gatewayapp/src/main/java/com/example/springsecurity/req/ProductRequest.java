package com.example.springsecurity.req;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private Long id;

    @NotNull(message = "Name cannot be null")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    private String description;


    @NotNull(message = "Price cannot be null")
    @Positive
    private BigDecimal price;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be positive or zero")
    private int quantity;


    @PositiveOrZero
    private BigDecimal discountPrice;

    @PositiveOrZero
    private BigDecimal discount;


    private List<String> imageUrls;


    @Past(message = "Created at date must be in the past")
    private LocalDateTime createdAt;

    @Past(message = "Updated at date must be in the past")
    private LocalDateTime updatedAt;

    private boolean isDeleted;





}