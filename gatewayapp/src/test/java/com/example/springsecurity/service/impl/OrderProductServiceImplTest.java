package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.OrderProductDto;
import com.example.springsecurity.entity.OrderProduct;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.repository.OrderProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProductServiceImplTest {

    @Mock
    private OrderProductRepository orderProductRepository;


    @InjectMocks
    private OrderProductServiceImpl orderProductService;


    @Test
    void testFindOrderProductsByOrderId() {
        // Arrange
        Long orderId = 1L;
        List<OrderProduct> orderProducts = new ArrayList<>();
        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setId(1L);
        orderProduct1.setQuantity(2);
        orderProduct1.setProductName("Product 1");
        orderProduct1.setProduct(new Product());
        orderProduct1.getProduct().setPrice(new BigDecimal("10.00"));
        orderProduct1.getProduct().setDiscountPrice(new BigDecimal("8.00"));
        orderProducts.add(orderProduct1);

        when(orderProductRepository.findOrderProductsByOrderId(orderId)).thenReturn(orderProducts);

        // Act
        List<OrderProductDto> result = orderProductService.findOrderProductsByOrderId(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderProducts.size(), result.size());
        OrderProductDto orderProductDto = result.get(0);
        assertEquals(orderProduct1.getId(), orderProductDto.getProductId());
        assertEquals(orderProduct1.getQuantity(), orderProductDto.getQuantity());
        assertEquals(orderProduct1.getProductName(), orderProductDto.getProductName());
        assertEquals(orderProduct1.getProduct().getPrice(), orderProductDto.getPrice());
        assertEquals(orderProduct1.getProduct().getDiscountPrice(), orderProductDto.getDiscountPrice());
    }
}