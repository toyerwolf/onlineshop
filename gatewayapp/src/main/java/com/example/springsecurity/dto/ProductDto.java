package com.example.springsecurity.dto;


import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data

public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private String imageUrl;
    private String categoryName;
//    private boolean isPaid;
    private Integer averageRating;




}

