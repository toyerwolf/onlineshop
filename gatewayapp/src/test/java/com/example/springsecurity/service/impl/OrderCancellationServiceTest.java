package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.OrderStatus;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCancellationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductInventoryService productInventoryService;

    @InjectMocks
    @Spy
    private OrderCancellationService orderCancellationService;

    @Test
    @Transactional
    void testCancelOrder() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderCancellationService.cancelOrder(orderId);

        // Assert
        verify(orderCancellationService, times(1)).returnProductsToInventory(order);
        verify(orderRepository, times(1)).save(order);
    }


    @Test
    void testReturnProductsToInventory() {
        // Arrange
        Order order = new Order();
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);
        order.addProduct(product1, 5); // Adding 5 units of product1
        order.addProduct(product2, 3); // Adding 3 units of product2

        // Act
        orderCancellationService.returnProductsToInventory(order);

        // Assert
        verify(productInventoryService, times(1)).increaseProductCount(product1, 5);
        verify(productInventoryService, times(1)).increaseProductCount(product2, 3);
    }
}