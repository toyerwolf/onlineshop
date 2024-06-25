package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.RatingDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.Rating;
import com.example.springsecurity.entity.User;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.repository.RatingRepository;
import com.example.springsecurity.repository.UserRepository;
import com.example.springsecurity.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final ProductRepository productRepository;


    private final RatingRepository ratingRepository;

    private final CustomerRepository customerRepository;

    public void addRatingToProduct(RatingDto ratingDto) {
        Product product = productRepository.findById(ratingDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
        Customer customer = customerRepository.findById(ratingDto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        Rating rating = Rating.builder()
                .rating(ratingDto.getRating())
                .createdAt(LocalDateTime.now())
                .product(product)
                .customer(customer)
                .build();
        product.getRatings().add(rating);
        ratingRepository.save(rating);
        productRepository.save(product);
    }


}

