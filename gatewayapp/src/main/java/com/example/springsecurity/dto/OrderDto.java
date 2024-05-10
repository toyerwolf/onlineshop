package com.example.springsecurity.dto;

import com.example.springsecurity.entity.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private BigDecimal totalAmount;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderStatus status;

}
