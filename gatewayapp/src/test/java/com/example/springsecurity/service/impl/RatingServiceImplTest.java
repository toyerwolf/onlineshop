package com.example.springsecurity.service.impl;

import com.example.springsecurity.dto.RatingDto;
import com.example.springsecurity.entity.Customer;
import com.example.springsecurity.entity.Product;
import com.example.springsecurity.entity.Rating;
import com.example.springsecurity.exception.NotFoundException;
import com.example.springsecurity.repository.CustomerRepository;
import com.example.springsecurity.repository.ProductRepository;
import com.example.springsecurity.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {


    @Mock
    ProductRepository productRepository;

    @Mock
    CustomerRepository customerRepository;


    @Mock
    RatingRepository ratingRepository;

    @InjectMocks
    RatingServiceImpl ratingService;


    @Test
    public void testAddRatingToProduct() {
        RatingDto ratingDto=new RatingDto();
        ratingDto.setProductId(1L);
        ratingDto.setCustomerId(1L);
        ratingDto.setRating(5);

        Product product=new Product();
        product.setId(1L);
        Customer customer=new Customer();
        customer.setId(1L);
        product.setRatings(new ArrayList<>());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating savedRating = invocation.getArgument(0);
            savedRating.setId(1L); // Mocking ID assignment
            return savedRating;
        });

        // Call the method
        ratingService.addRatingToProduct(ratingDto);

        // Verify interactions
        verify(productRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).findById(1L);
        verify(ratingRepository, times(1)).save(any(Rating.class));

        // Additional assertions can be added based on your requirements
        // For example, check if the rating is added to the product's ratings list
        assertEquals(1, product.getRatings().size());
    }

    @Test
    public void testAddRatingToProduct_ProductNotFound() {
        // Mock data
        RatingDto ratingDto = new RatingDto();
        ratingDto.setRating(5);
        ratingDto.setProductId(1L);
        ratingDto.setCustomerId(1L);

        // Mock repository behavior (product not found)
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify that NotFoundException is thrown
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            ratingService.addRatingToProduct(ratingDto);
        });

        assertEquals("Product not found", exception.getMessage());

        // Verify interactions
        verify(productRepository, times(1)).findById(1L);
        verify(customerRepository, never()).findById(anyLong());
        verify(ratingRepository, never()).save(any(Rating.class));
    }


    @Test
    public void testAddRatingToProduct_CustomerNotFound() {
        // Mock data
        RatingDto ratingDto = new RatingDto();
        ratingDto.setRating(5);
        ratingDto.setProductId(1L);
        ratingDto.setCustomerId(1L);

        Product product = new Product();
        product.setId(1L);

        // Mock repository behavior (customer not found)
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify that NotFoundException is thrown
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            ratingService.addRatingToProduct(ratingDto);
        });

        assertEquals("Customer not found", exception.getMessage());

        // Verify interactions
        verify(productRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).findById(1L);
        verify(ratingRepository, never()).save(any(Rating.class));
    }







    }

