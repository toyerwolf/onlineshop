package com.example.springsecurity.dto;

import com.example.springsecurity.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long orderId;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private boolean isPaid;
    private List<OrderProductDto> products;
}
