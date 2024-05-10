package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.OrderStatus;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class OrderCancellationService {

    private final OrderRepository orderRepository;
    private final ProductInventoryService productInventoryService;

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
        returnProductsToInventory(order);
        order.setStatus(OrderStatus.CANCEL);
        orderRepository.save(order);
    }

    private void returnProductsToInventory(Order order) {
        Map<Product, Integer> productQuantities = order.getProductQuantities();
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            productInventoryService.increaseProductCount(product, quantity);
        }
    }
}
