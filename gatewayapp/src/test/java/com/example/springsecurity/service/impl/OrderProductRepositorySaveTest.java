package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.repository.OrderProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderProductRepositorySaveTest {


    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private OrderProductRepositorySave orderProductRepositorySave;



    @Test
    void testSaveAll() {
        // Arrange
        Order order = new Order();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setDiscountPrice(new BigDecimal("8.00"));
        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setDiscountPrice(new BigDecimal("15.00"));
        Map<Product, Integer> productQuantities = new HashMap<>();
        productQuantities.put(product1, 2);
        productQuantities.put(product2, 1);

        // Act
        orderProductRepositorySave.saveAll(productQuantities, order);

        // Assert
        verify(orderProductRepository, times(1)).saveAll(anyList());
    }
}

