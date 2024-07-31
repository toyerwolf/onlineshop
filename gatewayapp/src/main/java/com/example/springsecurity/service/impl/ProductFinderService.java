package com.example.springsecurity.service.impl;

import com.example.springsecurity.entity.Product;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductFinderService {


    private final ProductRepository productRepository;

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}
