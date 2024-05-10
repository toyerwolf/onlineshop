package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Order;
import com.example.springsecurity.entity.OrderProduct;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.InsufficientQuantityException;

import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductInventoryService {
    private final ProductService productService;
    private final ProductRepository productRepository;

    public void validateProductQuantities(Map<Product, Integer> productQuantities) {
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            if (product.getQuantity() < quantity) {
                throw new InsufficientQuantityException("Insufficient quantity for product: " + product.getName());
            }
        }
    }


    //prinimayet map s id producta i kolichestvo kajdogo produkta
    public Map<Product, Integer> getProductQuantities(Map<Long, Integer> productQuantities) {
        Map<Product, Integer> products = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();
            Product product = productService.findProductById(productId);
            products.put(product, quantity);
        }
        return products;
    }

    public void decreaseProductCount(Map<Product, Integer> productQuantities) {
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            productService.decreaseCount(product.getId(), quantity);
        }
    }

    @Transactional
    public void increaseProductCount(Product product, int quantity) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + product.getId()));

        int newQuantity = existingProduct.getQuantity() + quantity;
        existingProduct.setQuantity(newQuantity);
        productRepository.save(existingProduct);
    }

//    public void decreaseProductCount(Order order) {
//        Map<Product, Integer> productQuantities = order.getOrderProducts().stream()
//                .collect(Collectors.toMap(
//                        OrderProduct::getProduct,
//                        OrderProduct::getQuantity
//                ));
//        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
//            Product product = entry.getKey();
//            Integer quantity = entry.getValue();
//            productService.decreaseCount(product.getId(), quantity);
//        }
//    }
//
//    public Map<Product, Integer> getProductQuantitiesFromOrder(Order order) {
//        return order.getOrderProducts().stream()
//                .collect(Collectors.toMap(OrderProduct::getProduct, OrderProduct::getQuantity));
//    }

}
