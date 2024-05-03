package com.example.springsecurity.dto;

import com.example.springsecurity.entity.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class OrderDto {
    private Long id;
    private BigDecimal totalAmount;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Status status;

}
