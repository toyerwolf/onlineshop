package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.OrderProduct;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.repository.OrderProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class OrderProductRepositorySave {

    private final OrderProductRepository orderProductRepository;

    public void saveAll(Map<Product, Integer> productQuantities, Order order) {
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(quantity);
            orderProduct.setProductName(product.getName());
            orderProducts.add(orderProduct);
        }
        orderProductRepository.saveAll(orderProducts);
    }
}
